package com.example.projekpmob_bagianmain

data class Report(val headerReport : String = "",
                  val isiReport : String = "",
                  val user: String ="",
                  val timestamp: String ="",
                  val status: Boolean = false,
                  val docID: String="")