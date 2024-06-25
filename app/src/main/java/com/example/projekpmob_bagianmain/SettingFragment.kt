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


        val username = arguments?.getString("USERNAME") ?: "User"
        val email = arguments?.getString("EMAIL")
        val password = arguments?.getString("PASSWORD")
        val editAccount = view.findViewById<TextView>(R.id.edit)
        editAccount.setOnClickListener {
            val newFragment = editAccount()
            val bundle = Bundle().apply {
                putString("USERNAME", username)
                putString("EMAIL", email)
                putString("PASSWORD",password)
            }
            newFragment.arguments = bundle

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        val usernameTextView = view.findViewById<TextView>(R.id.nama)
        usernameTextView.text = "$username"
    }

    companion object {
        private const val TAG = "SettingFragment"
    }
}

