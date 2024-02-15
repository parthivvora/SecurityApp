package com.example.securityapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.securityapp.dataModel.VisitorUserData
import com.example.securityapp.databinding.ActivityInDoorBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("DEPRECATION")
class InDoorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInDoorBinding
    private lateinit var calendar: Calendar
    private lateinit var visitorImage: ShapeableImageView
//    private var database = FirebaseDatabase.getInstance().reference
    private lateinit var imageData: ByteArray
    private lateinit var name: String
    private lateinit var mobile: String
    private lateinit var buildingName: String
    private lateinit var flatNo: String
    private lateinit var person: String
    private lateinit var vehicleNo: String
    private lateinit var entryDate: String
    private lateinit var inTime: String
    private lateinit var category: String
    private lateinit var remark: String

    @SuppressLint("WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInDoorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        calendar = Calendar.getInstance()

        binding.btnDatePicker.setOnClickListener {
            showDatePicker()
        }

        binding.btnTimePicker.setOnClickListener {
            showTimePicker()
        }

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.submitBtn.setOnClickListener {
            submitVisitorData()
        }

        // Select language using Dropdown menu
        val languages = resources.getStringArray(R.array.visitor_category)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, languages)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
        binding.autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            category = parent.getItemAtPosition(position).toString()
        }

        // Get click image of Visitor from intent and convert into ByteArray for store into firebase Storage
        val image = intent.extras?.get("visitorImage") as Bitmap
        visitorImage = findViewById(R.id.visitor_image)
        visitorImage.setImageBitmap(image)
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        imageData = baos.toByteArray()
    }

    // For store visitor data into firebase real time DB
    private fun submitVisitorData() {
        name = binding.name.text.toString()
        mobile = binding.mobile.text.toString()
        buildingName = binding.buildingName.text.toString()
        flatNo = binding.flatNo.text.toString()
        person = binding.person.text.toString()
        vehicleNo = binding.vehicleNo.text.toString()
        remark = binding.remark.text.toString()

        uploadImage()
    }

    // For upload visitor image
    private fun uploadImage() {
        val imageName = "visitor_image_${System.currentTimeMillis()}.jpg"
        val storageReference: StorageReference =
            FirebaseStorage.getInstance().reference.child("visitor/${imageName}")
        val uploadTask = storageReference.putBytes(imageData)
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    saveVisitorData(downloadUrl)
                }
                    .addOnFailureListener { e ->
                        Log.d(
                            "upload visitor image error",
                            "uploadImage: Failed to get download URL: ${e.message}"
                        )
                    }
            } else {
                Toast.makeText(
                    this,
                    "Failed to upload image: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Save visitor data into FireBase Real Time Database
    private fun saveVisitorData(image: String) {
        val database = FirebaseDatabase.getInstance().getReference("visitor")
        val id = database.push().key
        val user = VisitorUserData(
            id,
            image,
            name,
            mobile,
            buildingName,
            flatNo,
            person,
            vehicleNo,
            entryDate,
            inTime,
            category,
            remark,
        )
        val childReference = database.child(id ?: "")
        childReference.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this, { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                val formattedHour = if (selectedHour > 12) selectedHour - 12 else selectedHour
                val amPm = if (selectedHour >= 12) "PM" else "AM"
                val formattedMinute = String.format("%02d", selectedMinute)
                inTime = "$formattedHour:$formattedMinute $amPm"
            }, hour, minute, false
        )

        timePickerDialog.show()
    }

    private fun showDatePicker() {
        try {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    entryDate = dateFormat.format(selectedDate.time)
                    Toast.makeText(this, entryDate, Toast.LENGTH_SHORT).show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        } catch (e: Exception) {
            Log.d("date picker error", "showDatePicker: ${e.message}")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }
}