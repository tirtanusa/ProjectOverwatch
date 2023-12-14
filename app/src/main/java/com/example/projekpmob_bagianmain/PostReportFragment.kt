package com.example.projekpmob_bagianmain

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.projekpmob_bagianmain.databinding.PostReportBinding
import com.example.projekpmob_bagianmain.databinding.ReportActiveBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostReportFragment : Fragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.post_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val submitButton = view.findViewById<ImageView>(R.id.imageView4)
        submitButton.setOnClickListener {
            submitReport()
        }
    }

    private fun submitReport() {
        var isiReport = view?.findViewById<EditText>(R.id.reportColumn)?.text.toString()
        // Membuat header report dengan tanggal dan waktu saat ini
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val clockFormat = SimpleDateFormat("HH:mm",Locale.getDefault())
        val timeFormat= SimpleDateFormat("yyyy-MM-dd HH:MM:SS",Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val timeStamp = timeFormat.format(Date())
        val currentClock = clockFormat.format(Date())
        val headerReport = "Laporan pada $currentDate"
        isiReport = "$currentClock - $isiReport"
        // Membuat objek Report
        val report = Report(headerReport, isiReport,"Admin",timeStamp)

        // Kirim objek Report ke Firestore
        val firestore = Firebase.firestore
        firestore.collection("report")
            .add(report)
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
