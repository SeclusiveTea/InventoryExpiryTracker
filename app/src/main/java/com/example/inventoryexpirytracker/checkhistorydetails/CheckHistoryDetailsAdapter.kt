package com.example.inventoryexpirytracker.checkhistorydetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryexpirytracker.R
import com.example.inventoryexpirytracker.inventory.InventoryItem
import kotlinx.android.synthetic.main.inventorycard.view.image
import kotlinx.android.synthetic.main.inventorycard.view.tv_item_name
import kotlinx.android.synthetic.main.inventorycard.view.tv_id
import kotlinx.android.synthetic.main.inventorycard.view.tv_status

class CheckHistoryDetailsAdapter(
    private val itemList: List<InventoryItem>
) : RecyclerView.Adapter<CheckHistoryDetailsAdapter.CheckHistoryDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckHistoryDetailsAdapter.CheckHistoryDetailsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.inventorycard,
            parent, false)

        return CheckHistoryDetailsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CheckHistoryDetailsViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.imageView.setImageResource(currentItem.imageResource)
        holder.itemId.text = currentItem.itemId
        holder.itemName.text = currentItem.itemName
        holder.status.text = currentItem.status
    }

    override fun getItemCount() = itemList.size

    inner class CheckHistoryDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.image
        val itemId: TextView = itemView.tv_id
        val itemName: TextView = itemView.tv_item_name
        val status: TextView = itemView.tv_status
    }

}