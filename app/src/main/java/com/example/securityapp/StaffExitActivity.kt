@file:Suppress("DEPRECATION")

package com.example.securityapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securityapp.dataAdapter.StaffExitDataAdapter
import com.example.securityapp.dataModel.StaffDetailsModel
import com.example.securityapp.databinding.ActivityStaffExitBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Suppress("NAME_SHADOWING", "DEPRECATION")
class StaffExitActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStaffExitBinding
    private lateinit var adapter: StaffExitDataAdapter
    private lateinit var staffDetailList: ArrayList<StaffDetailsModel>
    private lateinit var progressDialog: ProgressDialog

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffExitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this@StaffExitActivity, MainActivity::class.java))
            finish()
        }

        progressDialog = ProgressDialog(this@StaffExitActivity)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        staffDetailList = arrayListOf<StaffDetailsModel>()
        getStaffData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStaffData() {
        try {
            val database = FirebaseDatabase.getInstance().reference.child("staffWorkingDetails")
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (staff in snapshot.children) {
                            val staffData = staff.getValue(StaffDetailsModel::class.java)
                            progressDialog.dismiss()
                            staffDetailList.add(staffData!!)
                            binding.staffExitDataRecyclerView.layoutManager =
                                LinearLayoutManager(
                                    this@StaffExitActivity,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                            binding.staffExitDataRecyclerView.setHasFixedSize(true)
                            adapter = StaffExitDataAdapter(staffDetailList, this@StaffExitActivity)
                            binding.staffExitDataRecyclerView.adapter = adapter
                            adapter.setOnItemClickListener(object :
                                StaffExitDataAdapter.OnItemClickListener {
                                @RequiresApi(Build.VERSION_CODES.O)
                                override fun onItemClick(staffData: String?) {
                                    updateStaffDetails(staffData)
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    progressDialog.dismiss()
                    Log.d("Error when get data from DB", "onCancelled: ${error.message}")
                }
            })
        } catch (e: Exception) {
            Log.d("Error when get data from DB", "onCancelled: ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateStaffDetails(staffData: String?) {
        val currentDateTime = LocalDateTime.now()
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
        val outTime = currentDateTime.format(timeFormatter)

        val database = FirebaseDatabase.getInstance().getReference("staffWorkingDetails")
            .orderByChild("staffId")
            .equalTo(staffData!!)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snapshot in snapshot.children) {
                        val staff = snapshot.getValue(StaffDetailsModel::class.java)

                        staff?.let {
                            it.outTime = outTime.toString()
                            snapshot.ref.setValue(staff)
                        }
                        Toast.makeText(this@StaffExitActivity, "Staff details updated...!", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this@StaffExitActivity,MainActivity::class.java))
                    }
                } else {
                    Toast.makeText(this@StaffExitActivity, "Staff not found", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@StaffExitActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@StaffExitActivity, MainActivity::class.java))
    }
}