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
import com.example.securityapp.dataAdapter.StaffDataAdapter
import com.example.securityapp.dataModel.StaffDataModel
import com.example.securityapp.dataModel.StaffDetailsModel
import com.example.securityapp.databinding.ActivityStaffBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Suppress("DEPRECATION")
class StaffActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStaffBinding
    private lateinit var adapter: StaffDataAdapter
    private lateinit var staffList: ArrayList<StaffDataModel>
    private lateinit var progressDialog: ProgressDialog

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this@StaffActivity)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this@StaffActivity, MainActivity::class.java))
            finish()
        }

        staffList = arrayListOf<StaffDataModel>()
        getStaffData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStaffData() {
        try {
            val database = FirebaseDatabase.getInstance().reference.child("staff")
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (staff in snapshot.children) {
                            val staffData = staff.getValue(StaffDataModel::class.java)
                            progressDialog.dismiss()
                            staffList.add(staffData!!)
                            binding.staffDataRecyclerView.layoutManager =
                                LinearLayoutManager(
                                    this@StaffActivity,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                            binding.staffDataRecyclerView.setHasFixedSize(true)
                            adapter = StaffDataAdapter(staffList, this@StaffActivity)
                            binding.staffDataRecyclerView.adapter = adapter
                            adapter.setOnItemClickListener(object :
                                StaffDataAdapter.OnItemClickListener {
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
        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault())
        val date = currentDateTime.format(dateFormatter)
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
        val inTime = currentDateTime.format(timeFormatter)

        val database = FirebaseDatabase.getInstance().getReference("staffWorkingDetails")
        val staff = StaffDetailsModel(
            staffData,
            date.toString(),
            inTime.toString()
        )
        val childReference = database.child(staffData ?: "")
        childReference.setValue(staff)
            .addOnSuccessListener {
                Toast.makeText(
                    this@StaffActivity,
                    "Staff information is successfully add",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this@StaffActivity,
                    "Failed to save data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@StaffActivity, MainActivity::class.java))
    }
}