package com.example.securityapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securityapp.dataAdapter.VisitorDataAdapter
import com.example.securityapp.dataModel.VisitorUserData
import com.example.securityapp.databinding.ActivityOutBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOutBinding
    private lateinit var visitorUserList: ArrayList<VisitorUserData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        visitorUserList = arrayListOf<VisitorUserData>()
        getVisitorData()
    }

    private fun getVisitorData() {
        try {
            val database = FirebaseDatabase.getInstance().reference.child("visitor")
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (visitor in snapshot.children) {
                            val visitorData = visitor.getValue(VisitorUserData::class.java)
                            visitorUserList.add(visitorData!!)
                        }
                        binding.visitorDataRecyclerView.layoutManager =
                            LinearLayoutManager(this@OutActivity, LinearLayoutManager.VERTICAL, false)
                        binding.visitorDataRecyclerView.setHasFixedSize(true)
                        val adapter = VisitorDataAdapter(visitorUserList, this@OutActivity)
                        binding.visitorDataRecyclerView.adapter = adapter
                        adapter.setOnItemClickListener(object :VisitorDataAdapter.OnItemClickListener{
                            override fun onItemClick(visitorData: String?) {
                                Log.d("select visitorData", "onItemClick: $visitorData")
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Error when get data from FB", "onCancelled: ${error.message}")
                }
            })
        } catch (e: Exception) {
            Log.d("Error when get data from FB", "onCancelled: ${e.message}")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }
}