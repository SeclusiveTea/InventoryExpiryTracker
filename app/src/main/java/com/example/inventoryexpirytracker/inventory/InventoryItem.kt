package com.example.inventoryexpirytracker.inventory
//drug data class for the recycler view
data class InventoryItem(
    val imageResource: Int,
    var itemId: String,
    var itemName: String,
    var status: String
) {
}