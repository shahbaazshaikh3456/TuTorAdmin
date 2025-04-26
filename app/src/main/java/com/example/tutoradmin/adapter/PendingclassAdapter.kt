package com.example.tutoradmin.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.tutoradmin.databinding.RequestclassesBinding

class PendingclassAdapter(
    private val Reqsubname: MutableList<String>,
    private val Reqteachername: MutableList<String>,
    private val ReqStudentname: MutableList<String>,
    private val Reqsubprice: MutableList<String>,
    private val context: Context,
    private val itemclick: OnItemClicked
) : RecyclerView.Adapter<PendingclassAdapter.PendingclassViewHolder>() {

    interface OnItemClicked {
        fun onItemClickListeneter(position: Int)
        fun onItemAcceptListeneter(position: Int)
        fun onItemRejectListeneter(position: Int)
        fun onItemDoneListeneter(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingclassViewHolder {
        val binding = RequestclassesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingclassViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingclassViewHolder, position: Int) {
        holder.bind(position)

    }

    override fun getItemCount(): Int = Reqsubname.size

    inner class PendingclassViewHolder(private val binding: RequestclassesBinding) :
        RecyclerView.ViewHolder(binding.root) {



        private var isAccepted = false

        fun bind(position: Int) {
            // Log for debugging
            Log.d("PendingclassAdapter", "List Size: ${Reqsubname.size}, Accessing Index: $position")

            if (position >= Reqsubname.size) {
                return  // Prevents IndexOutOfBoundsException
            }

            binding.apply {
                reqsubname.text = Reqsubname[position]
                reqsubtname.text = Reqteachername[position]
                reqstudentname.text = ReqStudentname[position]

                // Accept/Done Button Logic
                acceptbtn.apply {
                    text = if (!isAccepted) "Accept" else "Done"
                    setOnClickListener {
                        if (!isAccepted) {
                            isAccepted = true
                            text = "Done"
                            showToast("Class Accepted")
                            itemclick.onItemAcceptListeneter(position)
                        } else {
                            removeItem(position)
                            showToast("Class is Done")
                            itemclick.onItemDoneListeneter(position)
                        }
                    }
                }

                // Reject Button Logic
                Rejectbtn.setOnClickListener {
                    removeItem(position)
                    showToast("Class Rejected")
                    itemclick.onItemRejectListeneter(position)
                }

                // Card Click Listener
                itemView.setOnClickListener {
                    itemclick.onItemClickListeneter(adapterPosition)
                }
            }
        }

        private fun removeItem(position: Int) {
            if (position < Reqsubname.size) {
                ReqStudentname.removeAt(position)
                Reqteachername.removeAt(position)
                Reqsubname.removeAt(position)
                Reqsubprice.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, Reqsubname.size) // Update indices
            }
        }

        private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
