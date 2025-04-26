package com.example.tutoradmin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tutoradmin.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var database:FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var completeclassreference :DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.addmainsub.setOnClickListener{
            val intent = Intent(this,addsub::class.java)
            startActivity(intent)

        }
        binding.feedback.setOnClickListener{
            val intent = Intent(this,FeedbackListActivity::class.java)
            startActivity(intent)

        }


        binding.Allsubject.setOnClickListener{
            val intent = Intent(this,allsub::class.java)
            startActivity(intent)
        }

        binding.Attendance.setOnClickListener{
            val intent = Intent(this,AttendanceListActivity::class.java)
            startActivity(intent)
        }


        binding.reching.setOnClickListener{
            val intent = Intent(this,History::class.java)
            startActivity(intent)
        }

        binding.profiletutor.setOnClickListener{
            val intent = Intent(this,profileadmin::class.java)
            startActivity(intent)
        }
        binding.taptopending.setOnClickListener{
            val intent = Intent(this,Pendingclasses::class.java)
            startActivity(intent)
        }

        binding.taptologout.setOnClickListener{
            val intent = Intent(this,login::class.java)
            startActivity(intent)
        }

        binding.help.setOnClickListener{
            val intent = Intent(this,helpsupport::class.java)
            startActivity(intent)
        }

        binding.obout.setOnClickListener{
            val intent = Intent(this,obout::class.java)
            startActivity(intent)
        }

        completeclass()


    }

    override fun onResume() {
        super.onResume()
        RequestClass()
    }

    private fun completeclass() {
        database = FirebaseDatabase.getInstance()
        val completedClass = database.reference.child("Completed Class")

        completedClass.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val comItemCount = snapshot.childrenCount.toInt()
                binding.complted.text = comItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    private fun RequestClass() {
        database = FirebaseDatabase.getInstance()
        val requestpending = database.reference.child("Booking Details")

        requestpending.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val requestitemcount = snapshot.childrenCount.toInt()
                binding.counts.text = requestitemcount.toString()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}