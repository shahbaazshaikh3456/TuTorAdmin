package com.example.tutoradmin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tutoradmin.databinding.ActivityHelpsupportBinding

class helpsupport : AppCompatActivity() {

    private lateinit var binding: ActivityHelpsupportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize ViewBinding
        binding = ActivityHelpsupportBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Back button functionality (assuming you have a back button with id "helpBackBtn")
        binding.helpback.setOnClickListener {
            finish() // Closes the activity and returns to previous one
        }
    }
}
