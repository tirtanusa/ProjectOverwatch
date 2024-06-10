package com.example.projekpmob_bagianmain

import android.os.Bundle
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ScreenSignup : AppCompatActivity() {
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_screen)

        val UsernameField = findViewById<EditText>(R.id.fullname)
        val email_addressField = findViewById<EditText>(R.id.email_address)
        val password_fieldField = findViewById<EditText>(R.id.password_field)
        val confirm_password_fieldField = findViewById<EditText>(R.id.confirm_password_field)
        val signupButton = findViewById<Button>(R.id.signuplabel)
        val loginButton = findViewById<Button>(R.id.login_button_signup)
        val termsCheckbox = findViewById<CheckBox>(R.id.checkbox_agreed_to_terms)

        loginButton.setOnClickListener {
            val intent = Intent(this, ScreenLogin::class.java)
            startActivity(intent)
            finish()
        }

        // TextWatcher to the username field
        UsernameField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val username = s.toString()
                if (!username.matches(Regex("[a-zA-Z]+"))) {
                    // Only letters are allowed
                    UsernameField.error = "Username must contain only letters"
                } else {
                    UsernameField.error = null
                }
            }
        })

        signupButton.setOnClickListener {

            val username = UsernameField.text.toString()
            val password = password_fieldField.text.toString()
            val confpassword = confirm_password_fieldField.text.toString()
            val email = email_addressField.text.toString().trim() // trim untuk menghilangkan whitespace

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confpassword.isEmpty()) {
                Toast.makeText(this, "Cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (!username.matches(Regex("[a-zA-Z]+"))) {
                Toast.makeText(this, "Username must contain only letters", Toast.LENGTH_SHORT).show()
            } else if (password != confpassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else if (!termsCheckbox.isChecked) {
                Toast.makeText(this, "You must agree to the terms and conditions", Toast.LENGTH_SHORT).show()
            } else {
                userRepository.checkEmailUniqueness(email) { isUnique ->
                    if (isUnique) {
                        // Email is not a duplicate, proceed with registration
                        userRepository.registerUser(username, password, email)
                        Toast.makeText(this, "User $username has been registered", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ScreenLogin::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Email is already registered
                        Toast.makeText(this, "Email address is already registered. Please use another email", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
