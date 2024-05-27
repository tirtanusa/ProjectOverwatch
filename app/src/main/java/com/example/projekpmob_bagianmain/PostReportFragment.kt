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
import android.widget.Button
import android.widget.Toast
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val submitButton = view.findViewById<Button>(R.id.reportButton)
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
        // Find the EditText and get its content
        val reportEditText = view?.findViewById<EditText>(R.id.reportColumn)
        var isiReport = reportEditText?.text.toString()

        // Check if the EditText is empty
        if (isiReport.isEmpty()) {
            // Show a Toast message and return to prevent further execution
            Toast.makeText(context, "Report text cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the report header with the current date and time
        val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timeStamp = timeFormat.format(Date())
        val tanggalWaktu = Date()
        val pisahWaktu = timeFormat.format(tanggalWaktu)
        val partisi = pisahWaktu.split(" ")
        val tanggal = partisi[0]
        val waktu = partisi[1]
        val headerReport = "Laporan pada $tanggal"
        isiReport = "$waktu - $isiReport"

        // Send the report object to Firestore
        val firestore = Firebase.firestore
        val newReportRef = firestore.collection("report").document()
        val idGenerate = newReportRef.id
        val status = "dilaporkan"

        // Create the Report object
        val report = Report(headerReport, isiReport, "Admin", timeStamp, status, idGenerate, latitude, longitude)
        firestore.collection("report").document(idGenerate)
            .set(report)
            .addOnSuccessListener {
                // Handle when the report is successfully sent
                Log.d("Success", "Laporan berhasil dikirim")
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                // Handle when sending the report fails
                Log.d("Failed", "Laporan Gagal Dikirim")
            }
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
