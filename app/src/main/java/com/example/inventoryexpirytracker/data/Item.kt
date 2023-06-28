package com.example.inventoryexpirytracker.data
//firebase data class for drug table
data class Item(
    val itemid: String? = null,
    val itemname: String? = null,
    val itemtype: String? = null,
    val itemlocation: String? = null,
    val itemstatus: String? = null
) {
}