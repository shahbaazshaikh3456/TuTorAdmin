package com.example.tutoradmin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tutoradmin.databinding.ActivityAddsubBinding
import com.example.tutoradmin.model.AllSub
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class addsub : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val binding: ActivityAddsubBinding by lazy {
        ActivityAddsubBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.addinfo.setOnClickListener {
            val subname = binding.addsubname.text.toString().trim()
            val teachername = binding.addteacher.text.toString().trim()
            val subprice = binding.addsubprice.text.toString().trim()
            val introduction = binding.addintoduction.text.toString().trim()
            val experience = binding.addexpreince.text.toString().trim()
            val location = binding.location.text.toString().trim()

            if (subname.isNotBlank() && teachername.isNotBlank() && subprice.isNotBlank() && introduction.isNotBlank() && experience.isNotBlank()) {
                uploadData(subname, teachername, subprice, introduction, experience, location)
            } else {
                Toast.makeText(this, "Fill All Details", Toast.LENGTH_SHORT).show()
            }
        }

        binding.addsubback.setOnClickListener {
            finish()
        }
    }

    private fun uploadData(subname: String, teachername: String, subprice: String, introduction: String, experience: String, location: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = currentUser.uid
        val subRef = database.getReference("Tutor Users").child(uid).child("Details Teacher").push() // Storing inside UID

        val newItem = AllSub(
            subname = subname,
            teachername = teachername,
            subprice = subprice,
            introduction = introduction,
            exprience = experience,
            location = location,
            teacherimage = ""  // No image initially
        )

        subRef.setValue(newItem).addOnSuccessListener {
            Toast.makeText(this, "Data Uploaded Successfully", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Data Upload Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}