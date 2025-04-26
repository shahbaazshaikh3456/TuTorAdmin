package com.example.tutoradmin.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.tutoradmin.databinding.StatusBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Reachadapter(
    private val context: Context,
    private val studentname: MutableList<String>,
    private val reachstatus: MutableList<Boolean>,
    private val keys: MutableList<String> // List of keys to identify each item in Firebase
) : RecyclerView.Adapter<Reachadapter.ReachViewHolder>() {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Completed Class")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReachViewHolder {
        val binding = StatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReachViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReachViewHolder, position: Int) {
        holder.bind(position)
    }


    override fun getItemCount(): Int = studentname.size

    inner class ReachViewHolder(private val binding: StatusBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                statusstname.text = studentname[position]

                if (reachstatus[position]) {
                    statushistory.text = "Reach"
                } else {
                    statushistory.text = "Not Reach"
                }

                val colorMap = mapOf(
                    true to Color.GREEN,
                    false to Color.RED
                )

                val color = colorMap[reachstatus[position]] ?: Color.BLACK
                statushistory.setTextColor(color)
                colorstatus.backgroundTintList = ColorStateList.valueOf(color)

                // Delete Button Logic
                historyedit.setOnClickListener {
                    val key = keys[position]
                    databaseReference.child(key).removeValue().addOnSuccessListener {
                        // Remove from local lists and update adapter
                        studentname.removeAt(position)
                        reachstatus.removeAt(position)
                        keys.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, studentname.size)
                        Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
