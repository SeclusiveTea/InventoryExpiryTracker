package com.example.inventoryexpirytracker.data
//firebase data class for check history table
data class Check(
    val checkid: String? = null,
    val checkdate: String? = null,
    val checkstaffuser: String? = null,
    val checkstafffname: String? = null,
    val checkstafflname: String? = null,
    val checklocation : String? = null,
    val checkSignature : String? = null
) {
}