package com.example.tutoradmin

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tutoradmin.databinding.ActivityAttendanceBinding
import com.example.tutoradmin.databinding.ActivityAttendanceListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Locale

class AttendanceListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceListBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var attendanceAdapter: ArrayAdapter<String>

    private val attendanceList = mutableListOf<String>()
    private val filteredList = mutableListOf<String>() // For search functionality

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAttendanceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backss.setOnClickListener {
            finish()
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("Tutor Users")

        loadAttendance()
        setupSearchView()
    }

    private fun loadAttendance() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                attendanceList.clear()
                filteredList.clear()

                for (userSnapshot in snapshot.children) { // Loop through Student IDs
                    for (teacherSnapshot in userSnapshot.child("Details Teacher").children) { // Loop through Teachers
                        val attendanceNode = teacherSnapshot.child("attendance")

                        for (dateSnapshot in attendanceNode.children) { // Loop through Attendance dates
                            val date = dateSnapshot.key ?: continue
                            val mark = dateSnapshot.child("mark").getValue(String::class.java) ?: "Unknown"
                            val studentName = dateSnapshot.child("studentName").getValue(String::class.java) ?: "Unknown Student"

                            val attendanceEntry = "Date: $date\nStudent: $studentName\nStatus: $mark"
                            attendanceList.add(attendanceEntry)
                        }
                    }
                }

                if (attendanceList.isEmpty()) {
                    attendanceList.add("No attendance records found")
                }

                filteredList.addAll(attendanceList)
                attendanceAdapter = ArrayAdapter(this@AttendanceListActivity, android.R.layout.simple_list_item_1, filteredList)
                binding.listViewAttendance.adapter = attendanceAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching attendance: ${error.message}")
            }
        })
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filteredList.clear()
                if (newText.isNullOrEmpty()) {
                    filteredList.addAll(attendanceList)
                } else {
                    val searchText = newText.lowercase(Locale.getDefault())
                    filteredList.addAll(attendanceList.filter { it.lowercase(Locale.getDefault()).contains(searchText) })
                }
                attendanceAdapter.notifyDataSetChanged()
                return true
            }
        })
    }
}
