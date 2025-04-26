package com.example.tutoradmin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageButton

class obout : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_obout)

        // Apply window insets


        // Back button functionality
        val backBtn = findViewById<ImageButton>(R.id.aboutBackBtn)
        backBtn.setOnClickListener {
            finish() // Close current activity and go back
        }
    }
}
