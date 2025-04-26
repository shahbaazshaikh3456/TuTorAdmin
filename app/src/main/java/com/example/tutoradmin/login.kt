package com.example.tutoradmin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tutoradmin.databinding.ActivityLoginBinding
import com.example.tutoradmin.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference

        binding.loginbutton.setOnClickListener {
            val email = binding.loginemail.text.toString().trim()
            val password = binding.loginpass.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            } else {
                signInUser(email, password)
            }
        }

        binding.donthavebutton.setOnClickListener {
            startActivity(Intent(this, signup::class.java))
        }
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {
                    saveUserData(it.uid, email, password)
                    updateUi(user)
                }
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                Log.e("Login", "Error: ${task.exception?.message}")
            }
        }
    }

    private fun saveUserData(uid: String, email: String, password: String) {
        val user = UserModel(email = email, password = password)

        database.child("Tutor Login").child(uid).setValue(user)
            .addOnSuccessListener {
                Log.d("Firebase", "User data saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to save user data", e)
            }
    }

    private fun updateUi(user: FirebaseUser?) {
        user?.let {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
