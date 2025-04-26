package com.example.tutoradmin

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tutoradmin.databinding.ActivityFeedbackListBinding
import com.google.firebase.database.*

class FeedbackListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackListBinding
    private lateinit var listViewFeedback: ListView
    private lateinit var database: DatabaseReference

    private val feedbackList = mutableListOf<String>()
    private lateinit var feedbackAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Correctly initialize binding
        binding = ActivityFeedbackListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set click listener after initializing binding
        binding.back.setOnClickListener {
            finish()
        }

        listViewFeedback = binding.listViewFeedback // Use binding instead of findViewById
        feedbackAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, feedbackList)
        listViewFeedback.adapter = feedbackAdapter

        database = FirebaseDatabase.getInstance().reference.child("Tutor Users")

        loadAllFeedback()
    }


    private fun loadAllFeedback() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                feedbackList.clear()

                for (userSnapshot in snapshot.children) { // Loop through users
                    for (teacherSnapshot in userSnapshot.child("Details Teacher").children) { // Loop through teachers
                        val feedbackNode = teacherSnapshot.child("feedback")

                        for (feedbackEntry in feedbackNode.children) { // Loop through feedback entries
                            val username = feedbackEntry.child("username").getValue(String::class.java)
                            val feedbackText = feedbackEntry.child("feedback").getValue(String::class.java)
                            val rating = feedbackEntry.child("rating").getValue(Int::class.java) // Get rating

                            if (username != null && feedbackText != null && rating != null) {
                                feedbackList.add("Username: $username\nFeedback: $feedbackText\nRating: ⭐$rating/5")
                            }
                        }
                    }
                }

                if (feedbackList.isEmpty()) {
                    Toast.makeText(this@FeedbackListActivity, "No feedback available", Toast.LENGTH_SHORT).show()
                }

                feedbackAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching feedback: ${error.message}")
                Toast.makeText(this@FeedbackListActivity, "Error fetching feedback", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
