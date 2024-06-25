package com.example.projekpmob_bagianmain

import android.content.Context
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import android.content.SharedPreferences

class editAccount : Fragment(){
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_account, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val username = arguments?.getString("USERNAME") ?: "User"
        val email = arguments?.getString("EMAIL")?: ""
        val password = arguments?.getString("PASSWORD")?: ""
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

        val newUsernameView = view.findViewById<EditText>(R.id.fullname)
        val newPasswordView = view.findViewById<EditText>(R.id.password_field)
        val confirmPasswordView = view.findViewById<EditText>(R.id.confirm_password_field)

        saveButton.setOnClickListener {
            val newUsername = newUsernameView.text.toString().trim()
            val newPassword = newPasswordView.text.toString().trim()
            val confirmPassword = confirmPasswordView.text.toString().trim()

            if (newUsername.isNotEmpty() && newPassword.isEmpty()) {
                updateUsername(email, newUsername)
                refresh(email, newUsername, password)
            } else if (newUsername.isEmpty() && newPassword.isNotEmpty()) {
                if(confirmPassword != newPassword){
                    Toast.makeText(activity, "Password doesnt match!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                else if(confirmPassword.isEmpty() || newPassword.isEmpty()){
                    Toast.makeText(activity, "Password or confirm password cant be empty!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                updatePassword(newPassword)
                refresh(email, username,newPassword)
            }
            else if( newUsername.isNotEmpty() && newPassword.isNotEmpty() && confirmPassword.isNotEmpty()){
                if(confirmPassword != newPassword){
                    Toast.makeText(activity, "Password doesnt match!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                updateUsername(email, newUsername)
                updatePassword(newPassword)
                refresh(email, newUsername,newPassword)
            }
            else {
                Toast.makeText(activity, "Please enter either a new username or a new password.", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun updateUsername(email: String, newUsername: String) {
        val userRef = firestore.collection("users").whereEqualTo("email", email)
        userRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                document.reference.update("username", newUsername)
                    .addOnSuccessListener {
                        Toast.makeText(
                            activity,
                            "Username updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            activity,
                            "Failed to update username: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(activity, "Error fetching user: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun updatePassword(newPassword: String) {
        val user: FirebaseUser? = auth.currentUser
        user?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(activity, "Password updated successfully", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        activity,
                        "Failed to update password: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun refresh(email: String, username: String, password: String){
        val newFragment = SettingFragment()
        val bundle = Bundle().apply {
            putString("USERNAME", username)
            putString("EMAIL", email)
            putString("PASSWORD",password)
        }
        newFragment.arguments = bundle

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, newFragment)
        transaction.commit()
    }

}
