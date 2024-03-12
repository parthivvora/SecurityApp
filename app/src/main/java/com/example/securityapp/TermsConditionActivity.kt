package com.example.securityapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.securityapp.databinding.ActivityMainBinding
import com.example.securityapp.databinding.ActivityTermsConditionBinding

@Suppress("DEPRECATION")
class TermsConditionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTermsConditionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_condition)

        binding = ActivityTermsConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this@TermsConditionActivity, MainActivity::class.java))
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@TermsConditionActivity, MainActivity::class.java))
        finish()
    }
}