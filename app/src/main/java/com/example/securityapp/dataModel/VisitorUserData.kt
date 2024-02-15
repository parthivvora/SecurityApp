package com.example.securityapp.dataModel

data class VisitorUserData(
    val id: String? = null,
    val image: String? = null,
    var name: String? = null,
    val mobile: String? = null,
    val buildingName: String? = null,
    var flatNo: String? = null,
    val person: String? = null,
    val vehicleNo: String? = null,
    val entryDate: String? = null,
    val inTime: String? = null,
    var outTime: String? = null,
    val category: String? = null,
    var remark: String? = null,
    var isVisitorStatus: Boolean? = true,
)
