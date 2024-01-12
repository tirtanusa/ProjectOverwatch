package com.example.projekpmob_bagianmain

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpdateReportFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.update_report, container, false)

        val headerReport = arguments?.getString("HEADER_REPORT")
        val isiReport = arguments?.getString("ISI_REPORT")

        val headerEditText = view.findViewById<TextView>(R.id.loremipsum)
        val isiEditText = view.findViewById<TextView>(R.id.isiReportSebelum)

        headerEditText.text = headerReport
        isiEditText.text = isiReport

        return view


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val submitButton = view.findViewById<ImageView>(R.id.imageView4)
        submitButton.setOnClickListener {
            Log.d("Sent","Submit Dipencet")
            val isiReport = arguments?.getString("ISI_REPORT")
            val documentID =arguments?.getString("DOCUMENT_ID")
            Log.d("Sent",isiReport.toString())
            isiReport?.let { it1 -> documentID?.let { it2 -> submitReport(it1, it2) } }
        }
    }

    private fun submitReport(isiSebelumnya : String,documentID:String) {
        Log.d("Failed","Mencoba Submit")
        var isiReport = view?.findViewById<EditText>(R.id.reportColumn)?.text.toString()
        // Membuat header report dengan tanggal dan waktu saat ini
        val timeFormat= SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timeStamp = timeFormat.format(Date())
        val tanggalWaktu = Date()
        var pisahWaktu = timeFormat.format(tanggalWaktu)
        val partisi = pisahWaktu.split(" ")
        val tanggal = partisi[0]
        val waktu = partisi[1]
        val headerReport = "Laporan pada $tanggal"

        isiReport = "$waktu - $isiReport" +
                "\n$isiSebelumnya"
        // Membuat objek Report
        val report = Report(headerReport, isiReport,"Admin",timeStamp)

        val updateData = mapOf(
            "isiReport" to isiReport,
            "timestamp" to timeStamp
            // Tambahkan field lain yang ingin diupdate
        )
        // Kirim objek Report ke Firestore
        val firestore = Firebase.firestore
        firestore.collection("report").document(documentID)
            .update(updateData)
            .addOnSuccessListener {
                // Handle ketika sukses
                Log.d("Success","Laporan berhasil dikirim")
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                // Handle ketika gagal
                Log.d("Failed","Laporan Gagal Dikirim")
            }
    }


}