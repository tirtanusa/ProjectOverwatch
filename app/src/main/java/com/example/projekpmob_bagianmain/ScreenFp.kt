@file:Suppress("DEPRECATION")

package com.example.projekpmob_bagianmain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class ScreenFp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fp_screen)

        auth = FirebaseAuth.getInstance()

        val resetButton = findViewById<Button>(R.id.reset_label)
        val reset_email = findViewById<EditText>(R.id.email_reset)
        val logButton = findViewById<Button>(R.id.logButton)

        logButton.setOnClickListener {
            val intent = Intent(this, ScreenLogin::class.java)
            startActivity(intent)
            finish()
        }

        resetButton.setOnClickListener {
            val email = reset_email.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Masukkan alamat email Anda", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result!!.signInMethods!!.isNotEmpty()) {
                            // Email terdaftar, kirim link reset password
                            sendPasswordResetEmail(email)
                        } else {
                            // Email tidak terdaftar
                            Toast.makeText(this, "Email tidak terdaftar", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Terjadi error
                        Toast.makeText(this, "Terjadi kesalahan: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email reset password telah dikirim", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Terjadi kesalahan: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
