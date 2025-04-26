package com.example.tutoradmin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tutoradmin.databinding.ActivitySignupBinding
import com.example.tutoradmin.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class signup : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private val binding: ActivitySignupBinding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference

        binding.createaccount.setOnClickListener {
            val tutorname = binding.username.text.toString().trim()
            val email = binding.emailaddress.text.toString().trim()
            val password = binding.textpassword.text.toString().trim()

            if (tutorname.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please Fill All Details", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(tutorname, email, password)
            }
        }

        binding.alreadyhavebutton.setOnClickListener {
            startActivity(Intent(this, login::class.java))
        }
    }

    private fun createAccount(tutorname: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {
                    saveUserData(it.uid, tutorname, email, password)
                }
                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, login::class.java))
                finish()
            } else {
                Toast.makeText(this, "Account Creation Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                Log.e("Account", "Create Account Failed", task.exception)
            }
        }
    }

    private fun saveUserData(uid: String, tutorname: String, email: String, password: String) {
        val user = UserModel(tutorname, email, password)
        database.child("Tutor Users").child(uid).setValue(user)
            .addOnSuccessListener {
                Log.d("Firebase", "User data saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to save user data", e)
            }
    }
}
