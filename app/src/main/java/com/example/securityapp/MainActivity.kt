package com.example.securityapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.example.securityapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION", "SameParameterValue")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Setup navigation drawer
        setupView()

        binding.inDoorActivity.setOnClickListener {
            binding.inDoorActivity.background = resources.getDrawable(R.drawable.card_border, null)
            checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE)
        }

        binding.outDoorActivity.setOnClickListener {
            binding.outDoorActivity.background = resources.getDrawable(R.drawable.card_border, null)
            startActivity(Intent(this@MainActivity, OutActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        } else {
            Log.d("login user id", "onStart: " + auth.currentUser!!.uid)
        }
    }

    // Check camera permission
    @SuppressLint("QueryPermissionsNeeded")
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, CAMERA_PERMISSION_CODE)
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Camera Permission Granted", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(packageManager) != null) {
                    startActivityForResult(intent, CAMERA_PERMISSION_CODE)
                }
            } else {
                Toast.makeText(this@MainActivity, "Camera Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // Click picture using Camera
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_PERMISSION_CODE && resultCode == RESULT_OK) {
            val bitmap = data?.extras?.get("data") as Bitmap
            val intent = Intent(this@MainActivity, InDoorActivity::class.java)
            intent.putExtra("visitorImage", bitmap)
            startActivity(intent)
        }
    }

    private fun setupView() {
        binding.menuIcon.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.customMenu.profile.setOnClickListener {
            Toast.makeText(this@MainActivity, "Profile", Toast.LENGTH_SHORT).show()
        }
        binding.customMenu.privacyPolicy.setOnClickListener {
            Toast.makeText(this@MainActivity, "Privacy Policy", Toast.LENGTH_SHORT).show()
        }
        binding.customMenu.termCondition.setOnClickListener {
            Toast.makeText(this@MainActivity, "Terms Condition", Toast.LENGTH_SHORT).show()
        }
        binding.customMenu.logout.setOnClickListener {
            Toast.makeText(this@MainActivity, "Logout", Toast.LENGTH_LONG).show()
            auth.signOut()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.close()
        } else {
            super.onBackPressed()
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.close()
        }
    }
}