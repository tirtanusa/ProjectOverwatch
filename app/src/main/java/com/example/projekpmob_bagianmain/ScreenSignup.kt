package com.example.projekpmob_bagianmain

import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.regex.Pattern
import com.example.projekpmob_bagianmain.R

class ScreenSignup : AppCompatActivity() {
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_screen)

//        val usernameWarning = findViewById<TextView>(R.id.usernameWarning)
//        val passwordWarning = findViewById<TextView>(R.id.passwordWarning)

        val UsernameField = findViewById<EditText>(R.id.fullname)
        val email_addressField = findViewById<EditText>(R.id.email_address)
        val password_fieldField = findViewById<EditText>(R.id.password_field)
        val confirm_password_fieldField = findViewById<EditText>(R.id.confirm_password_field)
        val signupButton = findViewById<Button>(R.id.signuplabel)

        signupButton.setOnClickListener {

            val username = UsernameField.text.toString()
            val email = email_addressField.text.toString()
            val password = password_fieldField.text.toString()
            val confpassword = confirm_password_fieldField.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confpassword.isEmpty()) {
                Toast.makeText(this, "Cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                userRepository.registerUser(username, password, email)
                // TODO: Add your sign up logic here
                // For example, save the user data to your database
                Toast.makeText(this, "User $username has been registered", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this, ScreenLogin::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}

// }
