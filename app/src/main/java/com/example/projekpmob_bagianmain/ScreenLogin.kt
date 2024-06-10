package com.example.projekpmob_bagianmain

import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class ScreenLogin : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        auth = FirebaseAuth.getInstance()

        val emailcheck = findViewById<EditText>(R.id.email_address)
        val passwordcheck = findViewById<EditText>(R.id.password_field)
        val loginButton = findViewById<Button>(R.id.logintext)
        val registerButton = findViewById<Button>(R.id.regisButton)
        val forgetButton = findViewById<Button>(R.id.forget_password)

        registerButton.setOnClickListener {
            val intent = Intent(this, ScreenSignup::class.java)
            startActivity(intent)
            finish()
        }

        forgetButton.setOnClickListener {
            val intent = Intent(this, ScreenPasswordForget::class.java)
            startActivity(intent)
            finish()
        }

        loginButton.setOnClickListener {

            val email = emailcheck.text.toString().trim() // trim untuk menghilangkan whitespace
            val password = passwordcheck.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                if (isValidEmail(email)) {
                    loginUser(email, password)
                } else {
                    println("Format email tidak valid")
                    Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = ("^[A-Za-z0-9+_.-]+@(.+)$")
        return email.matches(emailPattern.toRegex())
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser
                    if (user != null) {
                        if (!user.isEmailVerified) {
                            println("Email belum diverif")
                            Toast.makeText(baseContext, "Harap verifikasi email Anda terlebih dahulu.",
                                Toast.LENGTH_SHORT).show()
                        } else {
                            println("Login berhasil")
                            Toast.makeText(baseContext, "Login Successful.",
                                Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }

                } else {
                    println("Login gagal")
                    Toast.makeText(baseContext, "Login failed, make sure email and password correctly",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

}
