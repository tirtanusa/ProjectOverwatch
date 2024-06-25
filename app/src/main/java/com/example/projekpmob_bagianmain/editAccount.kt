package com.example.projekpmob_bagianmain

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment

class editAccount : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_account, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val username = arguments?.getString("USERNAME") ?: "User"
        val email = arguments?.getString("EMAIL")
        val password = arguments?.getString("PASSWORD")
        val usernameView = view.findViewById<TextView>(R.id.fullname)
        usernameView.text = username

        val saveButton = view.findViewById<ImageButton>(R.id.save_label)
        val cancelButton = view.findViewById<ImageButton>(R.id.cancel_button)

        cancelButton.setOnClickListener{
            val newFragment = SettingFragment()
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
    }

}
