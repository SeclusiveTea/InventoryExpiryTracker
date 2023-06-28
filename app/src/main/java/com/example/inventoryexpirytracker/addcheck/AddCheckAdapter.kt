package com.example.inventoryexpirytracker.addcheck

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryexpirytracker.R

import com.example.inventoryexpirytracker.inventory.InventoryItem
import kotlinx.android.synthetic.main.addcheckcard.view.image
import kotlinx.android.synthetic.main.addcheckcard.view.tv_item_name
import kotlinx.android.synthetic.main.addcheckcard.view.tv_id
import kotlinx.android.synthetic.main.addcheckcard.view.tv_status


class AddCheckAdapter (
    private val itemList: List<InventoryItem>,
    private val listener: OnItemClickListener
        ) : RecyclerView.Adapter<AddCheckAdapter.AddCheckViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddCheckViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.addcheckcard,
        parent, false)

        return AddCheckViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AddCheckViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.imageView.setImageResource(currentItem.imageResource)
        holder.itemId.text = currentItem.itemId
        holder.itemName.text = currentItem.itemName
        holder.status.text = currentItem.status
    }


    override fun getItemCount() = itemList.size

    inner class AddCheckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener{
                val imageView: ImageView = itemView.image
                val itemId: TextView = itemView.tv_id
                val itemName: TextView = itemView.tv_item_name
                val status: TextView = itemView.tv_status

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