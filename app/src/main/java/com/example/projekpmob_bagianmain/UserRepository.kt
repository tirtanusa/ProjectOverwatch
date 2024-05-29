package com.example.projekpmob_bagianmain

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import android.util.Log

class UserRepository {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    private val TAG = "UserRepository"

    fun checkEmailUniqueness(email: String, callback: (Boolean) -> Unit) {
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result
                    val isUnique = querySnapshot?.isEmpty ?: true
                    callback(isUnique)
                } else {
                    Log.e(TAG, "Error checking email uniqueness: ${task.exception?.message}")
                    callback(false)
                }
            }
    }

    fun registerUser(username: String, password: String, email: String) {

        val user = hashMapOf(
            "username" to username,
            "email" to email,
            "password" to password
        )

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User is registered successfully
                    val user = auth.currentUser

                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Email verifikasi berhasil dikirim
                                // ...
                            } else {
                                // Gagal mengirim email verifikasi
                                // ...
                            }
                        }

                    println("User registered successfully with uid: ${user?.uid}")
                } else {
                    // Registration failed
                    println("Registration failed: ${task.exception?.message}")
                }
            }

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}