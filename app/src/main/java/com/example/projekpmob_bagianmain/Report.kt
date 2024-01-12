package com.example.projekpmob_bagianmain

data class Report(
    val headerReport: String = "",
    val isiReport: String = "",
    val user: String ="",
    val timestamp: String ="",
    val status: Boolean = false,
    var docID: String="",
    val recentLatitude: Double = 0.0,
    val recentLongitude: Double= 0.0
)