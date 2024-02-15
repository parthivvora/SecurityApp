@file:Suppress("DEPRECATION")

package com.example.securityapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securityapp.dataAdapter.VisitorDataAdapter
import com.example.securityapp.dataModel.VisitorUserData
import com.example.securityapp.databinding.ActivityOutBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("DEPRECATION", "NAME_SHADOWING")
class OutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOutBinding
    private lateinit var adapter: VisitorDataAdapter
    private lateinit var visitorUserList: ArrayList<VisitorUserData>
    private lateinit var progressDialog: ProgressDialog
    private lateinit var noDataTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this@OutActivity, MainActivity::class.java))
            finish()
        }

        noDataTextView = findViewById(R.id.noDataTextView)
        progressDialog = ProgressDialog(this@OutActivity)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Search bar functionality
        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        visitorUserList = arrayListOf<VisitorUserData>()
        getVisitorData()
    }

    // Searching function
    private fun filterList(newText: String?) {
        if (newText != null) {
            val filterListData = ArrayList<VisitorUserData>()
            for (i in visitorUserList) {
                if (i.name?.toLowerCase(Locale.ROOT)!!
                        .matches(Regex(newText, RegexOption.IGNORE_CASE))
                ) {
                    filterListData.add(i)
                }
            }

            if (visitorUserList.isEmpty()) {
                getVisitorData()
            } else if (filterListData.isEmpty()) {
                Toast.makeText(this@OutActivity, "No data found", Toast.LENGTH_SHORT).show()
                noDataTextView.visibility = View.VISIBLE
                visitorUserList.clear()
            } else {
                adapter.setFilteredList(filterListData)
            }
        }
    }

    // Get visitor data
    private fun getVisitorData() {
        try {
            val database = FirebaseDatabase.getInstance().reference.child("visitor")
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (visitor in snapshot.children) {
                            val visitorData = visitor.getValue(VisitorUserData::class.java)
                            progressDialog.dismiss()
                            // if visitor status is true then show visitor data
                            if (visitorData!!.isVisitorStatus == true) {
                                visitorUserList.add(visitorData)
                                binding.visitorDataRecyclerView.layoutManager =
                                    LinearLayoutManager(
                                        this@OutActivity,
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )
                                binding.visitorDataRecyclerView.setHasFixedSize(true)
                                adapter = VisitorDataAdapter(visitorUserList, this@OutActivity)
                                binding.visitorDataRecyclerView.adapter = adapter
                                adapter.setOnItemClickListener(object :
                                    VisitorDataAdapter.OnItemClickListener {
                                    override fun onItemClick(visitorData: String?) {
                                        updateVisitorOutTime(visitorData)
                                    }
                                })
                            } else {
                                noDataTextView.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    progressDialog.dismiss()
                    Log.d("Error when get data from FB", "onCancelled: ${error.message}")
                }
            })
        } catch (e: Exception) {
            Log.d("Error when get data from FB", "onCancelled: ${e.message}")
        }
    }

    // Update visitor exit time
    private fun updateVisitorOutTime(visitorData: String?) {
        val database = FirebaseDatabase.getInstance().getReference("visitor").orderByChild("id")
            .equalTo(visitorData!!)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snapshot in snapshot.children) {
                        val visitor = snapshot.getValue(VisitorUserData::class.java)
                        val currentTime = Calendar.getInstance().time
                        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

                        visitor?.let {
                            it.outTime = dateFormat.format(currentTime)
                            it.isVisitorStatus = false
                            snapshot.ref.setValue(visitor)
                        }
                        Toast.makeText(
                            this@OutActivity,
                            "Visitor exit time updated",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        startActivity(Intent(this@OutActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(this@OutActivity, "Visitor not found", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@OutActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }
}