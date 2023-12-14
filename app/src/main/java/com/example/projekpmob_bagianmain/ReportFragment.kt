package com.example.projekpmob_bagianmain

// Import statements
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekpmob_bagianmain.databinding.ReportActiveBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.Query

class ReportFragment : Fragment() {

    private var _binding: ReportActiveBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ReportActiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        val postReportButton = view.findViewById<ImageView>(R.id.postReportButton)
        postReportButton.setOnClickListener {
            goToPostReportFragment()
        }
    }

    private fun getNotificationsFromFirestore() {
        val firestore = Firebase.firestore
        firestore.collection("report")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                if (isAdded) { // Periksa apakah fragment masih terhubung ke activity
                    val notificationList = ArrayList<Report>()
                    for (document in documents) {
                        val notification = document.toObject(Report::class.java)
                        notificationList.add(notification)
                    }

                    updateRecyclerView(notificationList)
                }
            }

    }

    private fun goToPostReportFragment() {
        // Ganti ReportFragment dengan PostReportFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PostReportFragment())
            .addToBackStack(null)
            .commit()
        Log.d("Report Fragment","Fragment Post Kebuka")
    }

    private fun updateRecyclerView(notificationList: List<Report>) {
        binding?.let {
            val adapter = it.recyclerViewReport.adapter as? NotificationAdapter
            adapter?.updateData(notificationList)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewReport.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewReport.adapter = NotificationAdapter(emptyList())
        getNotificationsFromFirestore()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
