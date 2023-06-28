package com.example.inventoryexpirytracker.checkhistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryexpirytracker.R
import kotlinx.android.synthetic.main.checkhistorycard.view.tv_checkdate
import kotlinx.android.synthetic.main.checkhistorycard.view.tv_checkid
import kotlinx.android.synthetic.main.checkhistorycard.view.tv_checkstaff
import kotlinx.android.synthetic.main.checkhistorycard.view.tv_location

class CheckHistoryAdapter(
    private val checkList: List<CheckHistoryCheck>,
    private val listener: OnItemClickListener
): RecyclerView.Adapter<CheckHistoryAdapter.CheckHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckHistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.checkhistorycard,
            parent, false)

        return CheckHistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CheckHistoryViewHolder, position: Int) {
        val currentItem = checkList[position]

        holder.checkID.text = currentItem.checkID
        holder.date.text = currentItem.date
        holder.staff.text = currentItem.staff
        holder.location.text = currentItem.location
    }

    override fun getItemCount() = checkList.size

    inner class CheckHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener{
            val checkID: TextView = itemView.tv_checkid
            val date: TextView = itemView.tv_checkdate
            val staff: TextView = itemView.tv_checkstaff
            val location: TextView = itemView.tv_location

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