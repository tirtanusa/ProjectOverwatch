package com.example.projekpmob_bagianmain

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

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

        val sharedPreferences = getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // Redirect ke halaman utama atau halaman berikutnya
            // Contoh:
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, ScreenSignup::class.java)
            startActivity(intent)
            finish()
        }

        forgetButton.setOnClickListener {
            val intent = Intent(this, ScreenFp::class.java)
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
                    // Membuat atau mengambil instance dari SharedPreferences
                    val sharedPreferences = getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    if (user != null) {
                        if (!user.isEmailVerified) {
                            println("Email belum diverif")
                            Toast.makeText(baseContext, "Harap verifikasi email Anda terlebih dahulu.",
                                Toast.LENGTH_SHORT).show()
                        } else {
//
//                            val userId = user.uid
//                            val username = user.displayName ?: "User"
//                            println("Login berhasil")
//                            Toast.makeText(baseContext, "Login Successful.",
//                                Toast.LENGTH_SHORT).show()
//                            startActivity(intent)
//                            finish()
                            val db = FirebaseFirestore.getInstance()
                            db.collection("users")
                                .whereEqualTo("email", email)
                                .get()
                                .addOnSuccessListener { documents ->
                                    if (!documents.isEmpty) {
                                        // Simpan status login
                                        editor.putBoolean("isLoggedIn", true)
                                        editor.apply()
                                        val document = documents.documents[0]
                                        val username = document.getString("username") ?: "User"
                                        println("Login berhasil")
                                        Toast.makeText(baseContext, "Login Successful.",
                                            Toast.LENGTH_SHORT).show()

                                        // Pass the user data to the next activity or use as needed
                                        val intent = Intent(this, MainActivity::class.java)
                                        intent.putExtra("USERNAME", username)
                                        intent.putExtra("EMAIL",email)
                                        intent.putExtra("PASSWORD", password)

                                        val sharedPreferencesUser = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                                        val editor = sharedPreferencesUser.edit()
                                        editor.putString("EMAIL", email)
                                        editor.putString("USERNAME", username)
                                        editor.putString("PASSWORD", password)
                                        editor.apply()


                                        startActivity(intent)
                                        finish()
                                    } else {
                                        println("User data not found")
                                        Toast.makeText(baseContext, "User data not found.",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    println("Error getting user data: ${exception.message}")
                                    Toast.makeText(baseContext, "Error getting user data.",
                                        Toast.LENGTH_SHORT).show()
                        }
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
