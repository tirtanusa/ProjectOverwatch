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
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    if (result != null && result.signInMethods != null && result.signInMethods!!.isNotEmpty()) {
                        // Email sudah terdaftar, kirimkan email untuk reset password
                        auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { resetTask ->
                                if (resetTask.isSuccessful) {
                                    Log.d("RESET_PASSWORD", "Email untuk reset password sudah dikirim ke $email")
                                    // Tampilkan pesan berhasil atau lakukan tindakan sesuai
                                    showSuccessDialog()
                                } else {
                                    Log.e("RESET_PASSWORD", "Gagal mengirim email untuk reset password: ${resetTask.exception?.message}")
                                    // Tampilkan pesan kesalahan atau lakukan tindakan sesuai
                                    showErrorDialog("Gagal mengirim email untuk reset password.")
                                }
                            }
                    } else {
                        // Email belum terdaftar, tampilkan pesan kesalahan
                        Log.d("RESET_PASSWORD", "Email $email belum terdaftar.")
                        showErrorDialog("Email tidak valid atau belum terdaftar.")
                    }
                } else {
                    // Terjadi kesalahan saat mengecek email
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidUserException) {
                        // Email tidak terdaftar di Firebase Authentication
                        Log.d("RESET_PASSWORD", "Email $email belum terdaftar di Firebase.")
                        showErrorDialog("Email tidak terdaftar di sistem kami.")
                    } else {
                        // Kesalahan umum
                        Log.e("RESET_PASSWORD", "Error: ${exception?.message}")
                        showErrorDialog("Terjadi kesalahan. Silakan coba lagi nanti.")
                    }
                }
            }
    }

    private fun showSuccessDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Berhasil")
            .setMessage("Email untuk reset password sudah dikirim.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
