package com.example.projekpmob_bagianmain

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class PostReportFragment : Fragment(){
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.post_report, container, false)
    }
    private fun checkLocationPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Companion.LOCATION_PERMISSION_REQUEST_CODE
            )
            return false
        }
        return true
    }


    //Last Location
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val submitButton = view.findViewById<ImageView>(R.id.imageView4)
        submitButton.setOnClickListener {
            if (checkLocationPermissions()) {
                getLastLocation()
            }
        }
    }
    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationResult: Task<Location> = fusedLocationClient.lastLocation
            locationResult.addOnSuccessListener { location: Location? ->
                location?.let {
                    submitReport(it.latitude, it.longitude)
                }
            }
        }
    }



    private fun submitReport(latitude: Double, longitude: Double) {
        var isiReport = view?.findViewById<EditText>(R.id.reportColumn)?.text.toString()
        // Membuat header report dengan tanggal dan waktu saat ini
        val timeFormat= SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault())
        val timeStamp = timeFormat.format(Date())
        val tanggalWaktu = Date()
        var pisahWaktu = timeFormat.format(tanggalWaktu)
        val partisi = pisahWaktu.split(" ")
        val tanggal = partisi[0]
        val waktu = partisi[1]
        val headerReport = "Laporan pada $tanggal"
        isiReport = "$waktu - $isiReport"


        // Kirim objek Report ke Firestore
        val firestore = Firebase.firestore
        val newReportRef = firestore.collection("report").document()
        val idGenerate = newReportRef.id
        // Membuat objek Report
        val report =
            Report(headerReport, isiReport, "Admin", timeStamp, false, idGenerate, latitude,longitude)
        firestore.collection("report").document(idGenerate)
            .set(report)
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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
