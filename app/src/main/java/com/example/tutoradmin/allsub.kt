package com.example.tutoradmin

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tutoradmin.adapter.AllSubItemAdapter
import com.example.tutoradmin.databinding.ActivityAllsubBinding
import com.example.tutoradmin.model.AllSub
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class allsub : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var menuItems: ArrayList<AllSub> = ArrayList()

    private val binding: ActivityAllsubBinding by lazy {
        ActivityAllsubBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Correct Firebase Database Reference Path
        databaseReference = FirebaseDatabase.getInstance().reference
            .child("Tutor Users")
            .child(uid)
            .child("Details Teacher")

        retrieveAllSubjects()

        binding.allsubback.setOnClickListener {
            finish()
        }
    }

    private fun retrieveAllSubjects() {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userSubjectsRef = FirebaseDatabase.getInstance().reference
            .child("Tutor Users")
            .child(uid)
            .child("Details Teacher")

        userSubjectsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuItems.clear()

                for (teacherSnapshot in snapshot.children) { // Loop through current user's teacher details
                    val menuItem = teacherSnapshot.getValue(AllSub::class.java)
                    menuItem?.let { menuItems.add(it) }
                }

                setAdapter() // Update RecyclerView adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Error: ${error.message}")
            }
        })
    }


    private fun setAdapter() {
        val adapter = AllSubItemAdapter(this@allsub, menuItems, databaseReference)
        binding.allsubrecycle.layoutManager = LinearLayoutManager(this)
        binding.allsubrecycle.adapter = adapter
    }
}
