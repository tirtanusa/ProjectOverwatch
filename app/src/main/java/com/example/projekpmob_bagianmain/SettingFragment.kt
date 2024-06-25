package com.example.projekpmob_bagianmain

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingFragment : Fragment(){
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_setting, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editAccount = view.findViewById<TextView>(R.id.edit)
        editAccount.setOnClickListener {
            val newFragment = editAccount()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()

            // Replace whatever is in the fragment_container view with this fragment
            transaction.replace(R.id.fragment_container, newFragment)
            // If you want to add this transaction to the back stack
            transaction.addToBackStack(null)

            // Commit the transaction
            transaction.commit()
        }
        val username = arguments?.getString("USERNAME") ?: "User"
        val usernameTextView = view.findViewById<TextView>(R.id.nama)
        usernameTextView.text = "$username"
    }

    companion object {
        private const val TAG = "SettingFragment"
    }
}

