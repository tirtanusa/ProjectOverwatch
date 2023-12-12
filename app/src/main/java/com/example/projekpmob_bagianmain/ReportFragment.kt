package com.example.projekpmob_bagianmain

// Import statements
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekpmob_bagianmain.databinding.ReportActiveBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList


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
    }

    private fun getNotificationsFromFirestore() {
        val firestore = Firebase.firestore
        firestore.collection("report")
            .get()
            .addOnSuccessListener { documents ->
                val notificationList = ArrayList<Report>()
                for (document in documents) {
                    val notification = document.toObject(Report::class.java)
                    notificationList.add(notification)
                }
                updateRecyclerView(notificationList)
            }
            .addOnFailureListener { exception ->
                Log.w("ReportFragment", "Error getting documents: ", exception)
            }
    }

    private fun updateRecyclerView(notificationList: List<Report>) {
        val adapter = binding.recyclerViewReport.adapter as NotificationAdapter
        adapter.updateData(notificationList)
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
