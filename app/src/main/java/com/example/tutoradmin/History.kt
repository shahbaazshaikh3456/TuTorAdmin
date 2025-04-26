package com.example.tutoradmin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tutoradmin.adapter.Reachadapter
import com.example.tutoradmin.databinding.ActivityHistoryBinding
import com.example.tutoradmin.model.Pendingdetails
import com.google.firebase.database.*

class History : AppCompatActivity() {

    private val binding: ActivityHistoryBinding by lazy {
        ActivityHistoryBinding.inflate(layoutInflater)
    }

    private lateinit var database: FirebaseDatabase
    private val listofcomplete: ArrayList<Pendingdetails> = arrayListOf()
    private val keysList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        retrieveCompleteClassDetail()



        binding.historyback.setOnClickListener {
            finish()
        }
    }

    private fun retrieveCompleteClassDetail() {
        database = FirebaseDatabase.getInstance()
        val completeClassReference = database.reference.child("Completed Class").orderByChild("currentTime")
        completeClassReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listofcomplete.clear()
                keysList.clear()
                for (completeClassSnapshot in snapshot.children) {
                    val completeClass = completeClassSnapshot.getValue(Pendingdetails::class.java)
                    completeClass?.let { listofcomplete.add(it) }
                    completeClassSnapshot.key?.let { keysList.add(it) } // Store Firebase keys
                }

                listofcomplete.reverse()
                keysList.reverse()
                setDataIntoRecycle()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@History, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setDataIntoRecycle() {
        val studentname = mutableListOf<String>()
        val reachStatus = mutableListOf<Boolean>()

        for (pendingDetails in listofcomplete) {
            pendingDetails.username?.let { studentname.add(it) }
            reachStatus.add(pendingDetails.reachteacher)
        }

        val adapter = Reachadapter(this, studentname, reachStatus, keysList)
        binding.historyrecycle.layoutManager = LinearLayoutManager(this)
        binding.historyrecycle.adapter = adapter
    }
}
