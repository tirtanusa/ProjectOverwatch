package com.example.projekpmob_bagianmain

// Import statements
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekpmob_bagianmain.databinding.ReportActiveBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList
import androidx.appcompat.app.AppCompatActivity
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

//        val postReportButton = view.findViewById<ImageView>(R.id.reportButton)
//        postReportButton.setOnClickListener {
//            goToPostReportFragment()
//        }
    }

    private fun getNotificationsFromFirestore() {
        val sharedPreferences = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val emailShared = sharedPreferences.getString("EMAIL", null)

        if (emailShared != null) {
            val firestore = Firebase.firestore
            firestore.collection("report")
                .whereEqualTo("user", emailShared.toString()) // Filter by email
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    if (isAdded) { // Check if fragment is still attached to activity
                        val notificationList = ArrayList<Report>()
                        for (document in documents) {
                            val notification = document.toObject(Report::class.java)
                            notificationList.add(notification)
                        }

                        updateRecyclerView(notificationList)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("ReportFragment", "Error getting documents: ", exception)
                    Toast.makeText(context, "Failed to load reports.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "No email found in shared preferences.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToPostReportFragment() {
        // Replace ReportFragment with PostReportFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PostReportFragment())
            .addToBackStack(null)
            .commit()
        Log.d("ReportFragment", "PostReportFragment opened")
    }

    private fun updateRecyclerView(notificationList: List<Report>) {
        binding.let {
            val adapter = it.recyclerViewReport.adapter as? NotificationAdapter
            adapter?.updateData(notificationList)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewReport.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewReport.adapter = NotificationAdapter(emptyList()) { headerReport, isiReport, docID ->
            // Implement desired action when an item is clicked
            // For example, navigate to UpdateReportFragment with the necessary data
            val bundle = Bundle().apply {
                putString("HEADER_REPORT", headerReport)
                putString("ISI_REPORT", isiReport)
                putString("DOCUMENT_ID", docID)
            }
            val updateReportFragment = UpdateReportFragment().apply {
                arguments = bundle
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, updateReportFragment)
                .addToBackStack(null)
                .commit()
        }
        getNotificationsFromFirestore()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
