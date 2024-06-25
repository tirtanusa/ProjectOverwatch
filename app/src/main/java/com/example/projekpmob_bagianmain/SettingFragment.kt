package com.example.projekpmob_bagianmain

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

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
        val sharedPreferences = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val emailShared = sharedPreferences.getString("EMAIL", null)
        val usernameShared = sharedPreferences.getString("USERNAME", null)
        val passwordShared = sharedPreferences.getString("PASSWORD", null)
        Log.e(TAG,"$usernameShared $emailShared $passwordShared")
        val editAccount = view.findViewById<TextView>(R.id.edit)
        val logoutButton = view.findViewById<TextView>(R.id.log_out_button)
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

        logoutButton.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isLoggedIn", false)
            editor.apply()
            val intent = Intent(requireContext(), ScreenLogin::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()

            // Tambahan aksi yang perlu dilakukan setelah logout, seperti pindah ke halaman login atau membersihkan data pengguna.
        }
    }

    companion object {
        private const val TAG = "SettingFragment"
    }
}

