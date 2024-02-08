package com.example.securityapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DemoActivity : AppCompatActivity() {
    private lateinit var sendBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        sendBtn = findViewById(R.id.sendBtn)

        sendBtn.setOnClickListener {
            hello()
        }
    }

    private fun hello() {
        try {


        } catch (e: Exception) {
            Log.d("ERROR MESSAGE NOTIFICATION", "Error sending SMS: ${e.message}")
        }
    }
}