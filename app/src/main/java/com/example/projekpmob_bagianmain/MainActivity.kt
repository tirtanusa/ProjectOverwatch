    package com.example.projekpmob_bagianmain

import android.Manifest
import android.content.pm.PackageManager
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


    class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mGoogleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
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
        }

        val map = findViewById<TextView>(R.id.maps)
        map.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mapFragment)
                .addToBackStack(null)
                .commit()
        }
        //set map View
        val setting = findViewById<TextView>(R.id.setting)
        setting.setOnClickListener{
            replaceFragment(SettingFragment())
        }

        // Set default view
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, mapFragment)
                .commit()
        }
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
                // Izin ditolak, Anda bisa menambahkan logika lebih lanjut di sini
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