package com.example.inventoryexpirytracker.managestaff

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryexpirytracker.R
import kotlinx.android.synthetic.main.staffcard.view.tv_account
import kotlinx.android.synthetic.main.staffcard.view.tv_first_name
import kotlinx.android.synthetic.main.staffcard.view.tv_last_name

class ManageStaffAdapter(
    private val staffList: List<ManageStaffStaff>,
    private val listener: OnItemClickListener
    ): RecyclerView.Adapter<ManageStaffAdapter.ManageNursesViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageNursesViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.staffcard,
                parent, false)

            return ManageNursesViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ManageNursesViewHolder, position: Int) {
            val currentItem = staffList[position]

            holder.fName.text = currentItem.fName
            holder.lName.text = currentItem.lName
            holder.account.text = currentItem.account
        }

        override fun getItemCount() = staffList.size

        inner class ManageNursesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener{
            val fName: TextView = itemView.tv_first_name
            val lName: TextView = itemView.tv_last_name
            val account: TextView = itemView.tv_account

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                }
            }
        }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}