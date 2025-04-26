package com.example.tutoradmin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tutoradmin.databinding.ActivityProfileadminBinding
import com.example.tutoradmin.model.TutorModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class profileadmin : AppCompatActivity() {

    private lateinit var binding: ActivityProfileadminBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var tutorReference: DatabaseReference
    private var valueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileadminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        tutorReference = database.reference.child("Tutor Users").child(auth.currentUser?.uid ?: "")

        binding.adminprofileback.setOnClickListener { finish() }

        setTutorData()

        binding.taptoedit.setOnClickListener { toggleEditing() }

        binding.saveinfo.setOnClickListener {
            saveTutorData()
        }
    }

    private var isEditable = false
    private fun toggleEditing() {
        isEditable = !isEditable
        binding.apply {
            tutorname.isEnabled = isEditable
            tutorage.isEnabled = isEditable
            tutornumber.isEnabled = isEditable
            tutoremail.isEnabled = isEditable
            tutorquali.isEnabled = isEditable
            tutoradress.isEnabled = isEditable
            saveinfo.visibility = if (isEditable) View.VISIBLE else View.GONE

            if (isEditable) tutorname.requestFocus()
        }
    }

    private fun saveTutorData() {
        val tutorID = auth.currentUser?.uid
        if (tutorID != null) {
            val tutorData = mapOf(
                "name" to binding.tutorname.text.toString().trim(),
                "age" to binding.tutorage.text.toString().trim(),
                "phone" to binding.tutornumber.text.toString().trim(),
                "emailid" to binding.tutoremail.text.toString().trim(),
                "qualification" to binding.tutorquali.text.toString().trim(),
                "address" to binding.tutoradress.text.toString().trim()
            )

            tutorReference.setValue(tutorData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    toggleEditing()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setTutorData() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val tutorProfile = snapshot.getValue(TutorModel::class.java)
                    if (tutorProfile != null) {
                        binding.apply {
                            tutorname.setText(tutorProfile.name ?: "")
                            tutorage.setText(tutorProfile.age ?: "")
                            tutornumber.setText(tutorProfile.phone ?: "")
                            tutoremail.setText(tutorProfile.emailid ?: auth.currentUser?.email ?: "")
                            tutorquali.setText(tutorProfile.qualification ?: "")
                            tutoradress.setText(tutorProfile.address ?: "")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@profileadmin, "Failed to fetch tutor data", Toast.LENGTH_SHORT).show()
            }
        }
        tutorReference.addListenerForSingleValueEvent(valueEventListener!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        valueEventListener?.let { tutorReference.removeEventListener(it) }
    }
}
