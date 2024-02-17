package com.example.securityapp.dataModel

data class UserDataModel(
    val id: String? = null,
    var name: String? = null,
    val email: String? = null,
    val password: String? = null,
    var contact: String? = null,
    val buildingNo: String? = null,
    val flatNo: String? = null,
    val image: String? = null,
    var userToken: String? = null
)
