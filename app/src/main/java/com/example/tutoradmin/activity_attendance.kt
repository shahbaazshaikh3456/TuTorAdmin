package com.example.tutoradmin

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AttendanceActivity : AppCompatActivity() {

    private lateinit var spinnerStudents: AutoCompleteTextView
    private lateinit var calendarView: CalendarView
    private lateinit var btnMarkAttendance: Button
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private val studentList = mutableListOf<String>()
    private var selectedStudent: String? = null
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        spinnerStudents = findViewById(R.id.spinnerStudents)
        calendarView = findViewById(R.id.calendarView)
        btnMarkAttendance = findViewById(R.id.btnMarkAttendance)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("Student Data")

        loadStudentNames()

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$year-${month + 1}-$dayOfMonth"
        }

        btnMarkAttendance.setOnClickListener {
            markAttendance()
        }
    }

    private fun loadStudentNames() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear()
                for (student in snapshot.children) {
                    val studentName = student.child("name").getValue(String::class.java)
                    if (!studentName.isNullOrEmpty()) {
                        studentList.add(studentName)
                    }
                }

                val adapter = ArrayAdapter(this@AttendanceActivity, android.R.layout.simple_list_item_1, studentList)
                spinnerStudents.setAdapter(adapter)

                spinnerStudents.setOnItemClickListener { _, _, position, _ ->
                    selectedStudent = studentList[position]
                    checkMarkedAttendance()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AttendanceActivity, "Error loading students", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun markAttendance() {
        if (selectedStudent.isNullOrEmpty()) {
            Toast.makeText(this, "Please select a student", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUserId = auth.currentUser?.uid ?: return
        val attendanceRef = FirebaseDatabase.getInstance().reference
            .child("Tutor Users").child(currentUserId).child("attendance").child(selectedStudent!!)

        attendanceRef.child(selectedDate).setValue(true)
            .addOnSuccessListener {
                Toast.makeText(this, "Attendance marked!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error marking attendance", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkMarkedAttendance() {
        if (selectedStudent.isNullOrEmpty()) return

        val currentUserId = auth.currentUser?.uid ?: return
        val attendanceRef = FirebaseDatabase.getInstance().reference
            .child("Tutor Users").child(currentUserId).child("attendance").child(selectedStudent!!)

        attendanceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val markedDates = mutableSetOf<String>()
                for (dateSnapshot in snapshot.children) {
                    markedDates.add(dateSnapshot.key ?: "")
                }

                calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                    selectedDate = "$year-${month + 1}-$dayOfMonth"
                    if (markedDates.contains(selectedDate)) {
                        calendarView.setBackgroundColor(Color.GREEN)
                    } else {
                        calendarView.setBackgroundColor(Color.WHITE)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AttendanceActivity, "Error loading attendance", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
