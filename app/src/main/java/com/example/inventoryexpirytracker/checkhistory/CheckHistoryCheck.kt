package com.example.inventoryexpirytracker.checkhistory
//data class for the check history recycler views
data class CheckHistoryCheck(
    val checkID: String,
    val date: String,
    val staff: String,
    val location: String
) {
}