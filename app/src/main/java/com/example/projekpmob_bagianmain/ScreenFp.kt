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

            sendPasswordResetEmail(email)
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        if (email.isNotEmpty()) {
            userRepository.checkEmailUniqueness(email) { isUnique ->
                if (isUnique) {
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Tampilkan pesan bahwa email telah dikirim
                                toast("Link reset password telah dikirim ke email.")
                                val intent = Intent(this, ScreenLogin::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                // Tampilkan pesan kesalahan
                                toast("Gagal mengirim link reset password.")
                            }
                        }
                } else {
                    Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            toast("Masukkan email Anda.")
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
