package com.example.tutoradmin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tutoradmin.databinding.ActivityUserBookingDetailsBinding
import com.example.tutoradmin.model.Pendingdetails

class UserBookingDetails : AppCompatActivity() {
    private lateinit var binding: ActivityUserBookingDetailsBinding

    private var username: String? = null
    private var phone: String? = null
    private var address: String? = null
    private var disabilities: String? = null
    private var standard: String? = null
    private var date :String? = null
    private var time : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityUserBookingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button functionality
        binding.Ubackbutton.setOnClickListener {
            finish()
        }

        // Fetch data from Intent
        getDataFromIntent()
    }

    private fun getDataFromIntent() {
        val receivedBookdetails = intent.getParcelableExtra<Pendingdetails>("pendingDetails")

        receivedBookdetails?.let {
            username = it.username ?: ""
            phone = it.phone ?: ""
            address = it.address ?: ""
            date = it.dates?:""
            time = it.times?:""
            disabilities = it.disabilities ?: ""
           standard = it.standard ?: ""
            setData()  // Call function to update UI
        }
    }

    private fun setData() {
        binding.Uname.setText(username)
        binding.Utphone.setText(phone)
        binding.Uadress.setText(address)
        binding.Utbilities.setText(disabilities)
        binding.Utclass.setText(standard)
        binding.Date.setText(date)
        binding.Times.setText(time)

    }
}
