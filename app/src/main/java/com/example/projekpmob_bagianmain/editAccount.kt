package com.example.projekpmob_bagianmain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val editAccount = view.findViewById<TextView>(R.id.edit)
        editAccount.setOnClickListener{
            val newFragment = editAccount()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()

            // Replace whatever is in the fragment_container view with this fragment
            transaction.replace(R.id.fragment_container, newFragment)
            // If you want to add this transaction to the back stack
            transaction.addToBackStack(null)

            // Commit the transaction
            transaction.commit()
        }
    }

}
