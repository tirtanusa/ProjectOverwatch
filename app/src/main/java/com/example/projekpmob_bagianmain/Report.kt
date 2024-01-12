package com.example.projekpmob_bagianmain

data class Report(
    //variable di report
    val headerReport: String = "",
    val isiReport: String = "",
    val user: String ="",
    val timestamp: String ="",
    val status: Boolean = false,
    val docID: String="",
    val recentLatitude: Double = 0.0,
    val recentLongitude: Double= 0.0
)