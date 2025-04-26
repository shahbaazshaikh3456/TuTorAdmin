package com.example.tutoradmin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tutoradmin.adapter.AllSubItemAdapter
import com.example.tutoradmin.adapter.PendingclassAdapter
import com.example.tutoradmin.adapter.Reachadapter
import com.example.tutoradmin.databinding.ActivityPendingclassesBinding
import com.example.tutoradmin.databinding.RequestclassesBinding
import com.example.tutoradmin.model.Pendingdetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Pendingclasses : AppCompatActivity(), PendingclassAdapter.OnItemClicked {
    private lateinit var binding: ActivityPendingclassesBinding
    private var listofusername: MutableList<String> = mutableListOf()
    private var listoftname: MutableList<String> = mutableListOf()
    private var listofsub: MutableList<String> = mutableListOf()
    private var listofprice: MutableList<String> = mutableListOf()
    private var listofpendingclass: ArrayList<Pendingdetails> = arrayListOf()
    private lateinit var databaseBookingReference: DatabaseReference
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingclassesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        databaseBookingReference = database.reference.child("Booking Details")

        getBookingDetails()

        binding.pendingbackbtn.setOnClickListener {
            finish()
        }
    }

    private fun getBookingDetails() {
        databaseBookingReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (bookingSnapshot in snapshot.children) {
                    val pendingdetails = bookingSnapshot.getValue(Pendingdetails::class.java)
                    pendingdetails?.let { listofpendingclass.add(it) }
                }
                addDataToRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }

    private fun addDataToRecyclerView() {
        for (pendingdetails in listofpendingclass) {
            pendingdetails.username?.let { listofusername.add(it) }
            pendingdetails.subnames?.let { listofsub.add(it.toString()) }
            pendingdetails.teacherNames?.let { listoftname.add(it.toString()) }
            pendingdetails.subPrices?.let { listofprice.add(it.toString()) }
        }
        setAdapter()
    }

    private fun setAdapter() {
        binding.requestclasses.layoutManager = LinearLayoutManager(this)
        val adapter =
            PendingclassAdapter(listofusername, listofsub, listoftname, listofprice, this, this)
        binding.requestclasses.adapter = adapter  // Fix: Set the adapter
    }

    override fun onItemClickListeneter(position: Int) {
        val selectedItem = listofpendingclass[position]
        val intent = Intent(this, UserBookingDetails::class.java)
        intent.putExtra("pendingDetails", selectedItem)  // Now properly passing Parcelable object
        startActivity(intent)
    }

    override fun onItemAcceptListeneter(position: Int) {
        val childitempushkey = listofpendingclass[position].itemPushKey
        val clickBookReference = childitempushkey?.let {
            database.reference.child("Booking Details").child(it)
        }
        clickBookReference?.child("orderAccepted")?.setValue(true)
        updateAcceptstatus(position)

    }

    override fun onItemRejectListeneter(position: Int) {
        val rejectedItemKey = listofpendingclass[position].itemPushKey
        val userIdofClickItem = listofpendingclass[position].userId

        if (rejectedItemKey != null && userIdofClickItem != null) {

            // ✅ Step 1: Update "Rejected" in Student Data -> History
            val historyReference = database.reference
                .child("Student Data")
                .child(userIdofClickItem)
                .child("History")

            historyReference.orderByChild("itemPushKey").equalTo(rejectedItemKey)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (childSnapshot in snapshot.children) {
                            childSnapshot.ref.child("Rejected").setValue(true)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseError", "Failed to update Rejected in History", error.toException())
                    }
                })

            // ✅ Step 2: Delete from "Booking Details"
            val rejectReference = database.reference.child("Booking Details").child(rejectedItemKey)
            rejectReference.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Class Rejected and Removed", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to Remove Booking", Toast.LENGTH_SHORT).show()
                }
        }
    }




    override fun onItemDoneListeneter(position: Int) {
        val doneitempushkey = listofpendingclass[position].itemPushKey
        val doneItembookReference=database.reference.child("Completed Class").child(doneitempushkey!!)
        doneItembookReference.setValue(listofpendingclass[position])
            .addOnSuccessListener {
                deletethisitemfromorderdetail(doneitempushkey)
            }

    }

    private fun deletethisitemfromorderdetail(doneitempushkey: String) {
        val bookdetailsitemreference =database.reference.child("Booking Details").child(doneitempushkey)
        bookdetailsitemreference.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Class is Completed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
    }



    private fun updateAcceptstatus(position: Int) {
        val userIdofClickItem = listofpendingclass[position].userId
        val PushKeyofClickItem = listofpendingclass[position].itemPushKey

        if (userIdofClickItem != null && PushKeyofClickItem != null) {
            val BuyHistoryReference = database.reference
                .child("Student Data")
                .child(userIdofClickItem)
                .child("History")


            BuyHistoryReference.orderByChild("itemPushKey").equalTo(PushKeyofClickItem)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (childSnapshot in snapshot.children) {
                            childSnapshot.ref.child("orderAccepted").setValue(true)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseError", "Failed to update orderAccepted", error.toException())
                    }
                })

            // Booking details me bhi update karein
            databaseBookingReference.child(PushKeyofClickItem).child("orderAccepted").setValue(true)
        }
    }




}