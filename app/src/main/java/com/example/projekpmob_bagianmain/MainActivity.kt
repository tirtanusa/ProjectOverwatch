    package com.example.projekpmob_bagianmain

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Camera
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import android.widget.TextView
import android.view.View
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.firebase.Firebase
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.DocumentChange



    class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    //Inisialisasi instance FirebaseFirestore
    private val db : FirebaseFirestore = Firebase.firestore
    private var mGoogleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var markersMap = HashMap<String, Marker>()
    private val REQUEST_LOCATION_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val mapFragment = supportFragmentManager.findFragmentById(R.id.gmaps) as SupportMapFragment
//        mapFragment.getMapAsync(this)
        val mapFragment = SupportMapFragment.newInstance()
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()

        val report = findViewById<TextView>(R.id.report)
        report.setOnClickListener {
            replaceFragment(ReportFragment())
            getReportDataFromFirestore()
            setupReportListener()
        }

        val map = findViewById<TextView>(R.id.maps)
        map.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mapFragment)
                .addToBackStack(null)
                .commit()
            getReportDataFromFirestore()
            setupReportListener()
        }
        //set map View
        val setting = findViewById<TextView>(R.id.setting)
        setting.setOnClickListener{
            replaceFragment(SettingFragment())
            getReportDataFromFirestore()
            setupReportListener()
        }

        // Set default view
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, mapFragment)
                .commit()
            getReportDataFromFirestore()
            setupReportListener()
        }
        getReportDataFromFirestore()
        setupReportListener()
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

        private fun showMapFragment() {
        Log.d("MainActivivty","ShowingMapFragment")
        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? SupportMapFragment
        mapFragment?.let {
            supportFragmentManager.beginTransaction().show(it).commit()
        }
    }

    private fun hideMapFragment() {
        Log.d("MainActivity","HidingMapFragment")
        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? SupportMapFragment
        mapFragment?.let {
            supportFragmentManager.beginTransaction().hide(it).commit()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        updateLocationUI()

       //Menentukan lokasi manual
        val location = LatLng(-7.7956,110.3695)

        // Menambahkan marker ke lokasi yang ditentukan
        mGoogleMap?.addMarker(MarkerOptions().position(location).title("Anda Membuat Pinpoint Disini"))

        // Memindahkan kamera ke lokasi pinpoint
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location,5f))

        mGoogleMap?.addMarker(
            MarkerOptions()
                .position(location)
                .title("Anda membuat Pinpoint Disini")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)) //
        )

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    val userLatLng = LatLng(loc.latitude, loc.longitude)
                    mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 10f))
                }
            }
        }
        // Panggil fungsi untuk mengambil data dari Firestore dan menambahkan marker ke peta
        getReportDataFromFirestore()
    }

        private fun getReportDataFromFirestore() {
            db.collection("report")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {  // Loop melalui setiap dokumen
                        val report = document.toObject(Report::class.java)
                        report.docID = document.id // Menetapkan ID dari dokumen ke field docID.
                        val reportLocation = LatLng(report.recentLatitude, report.recentLongitude)
                        val marker = mGoogleMap?.addMarker(
                            MarkerOptions()
                                .position(reportLocation)
                                .title(report.headerReport)
                                .snippet(report.isiReport)
                        )
                        marker?.let{
                            markersMap[report.docID] = it
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("MainActivity", "Error getting documents: ", exception)
                }
        }

        private fun setupReportListener() {
            db.collection("report").addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                snapshots?.documentChanges?.forEach { change ->
                    when (change.type) {
                        DocumentChange.Type.REMOVED -> {
                            val reportId = change.document.id
                            removeMarkerByReportId(reportId)
                        }
                        DocumentChange.Type.ADDED -> {

                        }
                        DocumentChange.Type.MODIFIED -> {

                        }
                    }
                }
            }
        }

        private fun removeMarkerByReportId(reportId: String) {
            markersMap[reportId]?.remove()
            markersMap.remove(reportId)
        }


        private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION

            )
        } else {
            mGoogleMap?.isMyLocationEnabled = true
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap?.isMyLocationEnabled = true
            } else {
                Toast.makeText(this,"Permintaan Ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateLocationUI() {
        try {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mGoogleMap?.isMyLocationEnabled = true
                mGoogleMap?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                mGoogleMap?.isMyLocationEnabled = false
                mGoogleMap?.uiSettings?.isMyLocationButtonEnabled = false
                fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                    if (location != null) {
                        val userLocation = LatLng(location.latitude, location.longitude)
                        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10f))
                    }
                }
            }
        } catch (e: SecurityException) {
            // Log atau tangani exception
        }
    }

    override fun onStart() {
        super.onStart()
        // Misalnya, Anda ingin mulai memonitor perubahan data dari sebuah database
        // atau mulai mendengarkan perubahan dari sebuah BroadcastReceiver.
        Log.d("ReportFragment", "Fragment mulai.")
    }

    override fun onResume() {
        super.onResume()
        // Lanjutkan atau mulai aktivitas yang memerlukan interaksi pengguna,
        // seperti animasi atau mulai pemutaran musik.
        Log.d("ReportFragment", "Fragment dilanjutkan.")
        getReportDataFromFirestore()
        setupReportListener()
    }

    override fun onPause() {
        super.onPause()
        // Jeda aktivitas yang mungkin menghabiskan sumber daya atau tidak perlu berjalan
        // ketika pengguna tidak fokus pada fragment ini.
        Log.d("ReportFragment", "Fragment dijeda.")
    }

    override fun onStop() {
        super.onStop()
        // Hentikan aktivitas yang tidak perlu berjalan ketika fragment tidak terlihat.
        // Misalnya, Anda mungkin ingin berhenti mendengarkan perubahan pada database.
        Log.d("ReportFragment", "Fragment dihentikan.")
    }

    override fun onDestroy() {
        super.onDestroy()
        // Lakukan pembersihan, misalnya lepaskan koneksi database atau hentikan threads.
        Log.d("ReportFragment", "Fragment dihancurkan.")
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}