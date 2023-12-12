package com.example.projekpmob_bagianmain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(private var notificationList: List<Report>) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewNotifName: TextView = view.findViewById(R.id.textViewNotifName)
        val textViewNotifDetails: TextView = view.findViewById(R.id.textViewNotifDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = notificationList[position]
        holder.textViewNotifName.text = report.headerReport
        holder.textViewNotifDetails.text = report.isiReport
    }

    fun updateData(newData: List<Report>) {
        notificationList = newData
        notifyDataSetChanged()
    }
    override fun getItemCount() = notificationList.size
}
