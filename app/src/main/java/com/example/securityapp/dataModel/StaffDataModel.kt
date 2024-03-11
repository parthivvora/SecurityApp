package com.example.securityapp.dataModel

data class StaffDataModel(
    val id: String? = null,
    var name: String? = null,
    val contact: String? = null,
    val gender: String? = null,
    var joinDate: String? = null,
    val isDeleted: Boolean? = null
)
