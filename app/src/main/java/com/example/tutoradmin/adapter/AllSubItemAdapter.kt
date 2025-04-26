package com.example.tutoradmin.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tutoradmin.databinding.AllsubjectBinding
import com.example.tutoradmin.model.AllSub
import com.google.firebase.database.*

class AllSubItemAdapter(
    private val context: Context,
    private val suballlist: ArrayList<AllSub>,
    private val databaseReference: DatabaseReference // Reference to "Details Teacher/UID"
) : RecyclerView.Adapter<AllSubItemAdapter.AllSubViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllSubViewHolder {
        val binding = AllsubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllSubViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllSubViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = suballlist.size

    inner class AllSubViewHolder(private val binding: AllsubjectBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val menuItem = suballlist[position]

            binding.allsubname.text = menuItem.subname
            binding.allsubtname.text = menuItem.teachername
            binding.allsubprice.text = menuItem.subprice

            // Load teacher image using Glide

            // Delete button click listener
            binding.delete.setOnClickListener {
                deletesub(position)
            }
        }

        // Function to delete subject from Firebase and list
        private fun deletesub(position: Int) {
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var index = 0
                    for (itemSnapshot in snapshot.children) {
                        if (index == position) {
                            val itemKey = itemSnapshot.key
                            itemKey?.let {
                                // Remove from Firebase
                                databaseReference.child(it).removeValue().addOnSuccessListener {
                                    // Remove from local list and notify adapter
                                    suballlist.removeAt(position)
                                    notifyItemRemoved(position)
                                    notifyItemRangeChanged(position, suballlist.size)
                                    Toast.makeText(context, "Subject Deleted", Toast.LENGTH_SHORT).show()
                                }.addOnFailureListener {
                                    Toast.makeText(context, "Failed to delete subject", Toast.LENGTH_SHORT).show()
                                }
                            }
                            break
                        }
                        index++
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
