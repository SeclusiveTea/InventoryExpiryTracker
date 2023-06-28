package com.example.inventoryexpirytracker.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryexpirytracker.R
import kotlinx.android.synthetic.main.inventorycard.view.*


class InventoryAdapter(
    private val itemList: List<InventoryItem>,
    private val listener: OnItemClickListener
    ) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.inventorycard,
        parent, false)

        return InventoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.imageView.setImageResource(currentItem.imageResource)
        holder.itemId.text = currentItem.itemId
        holder.itemName.text = currentItem.itemName
        holder.status.text = currentItem.status

        if (currentItem.status == "Expiring") {
            holder.drugBackground.setBackgroundResource(R.drawable.recyclerbackgroundexpired)
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.expiringDrug))
        } else {
            holder.drugBackground.setBackgroundResource(R.drawable.recyclerbackground)
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
        }
    }

    override fun getItemCount() = itemList.size

    inner class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener{
        val imageView: ImageView = itemView.image
        val itemId: TextView = itemView.tv_id
        val itemName: TextView = itemView.tv_item_name
        val status: TextView = itemView.tv_status
        val drugBackground: RelativeLayout = itemView.itemcardbackground

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