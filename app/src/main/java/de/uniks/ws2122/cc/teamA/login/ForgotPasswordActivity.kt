package de.uniks.ws2122.cc.teamA.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import de.uniks.ws2122.cc.teamA.R

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var requestNewPasswordButton: Button
    private lateinit var emailField: EditText
    private lateinit var progressbar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        requestNewPasswordButton = findViewById(R.id.requestNewPasswordButton)
        emailField = findViewById(R.id.emailField)
        progressbar = findViewById(R.id.progressBar)
        progressbar.isVisible = false


        requestNewPasswordButton.setOnClickListener { resetMail() }
        emailField.setOnClickListener { resetMail() }
    }

    private fun resetMail() {
        var email = emailField.text.trim().toString()
        if (email.isBlank() or email.isEmpty()) {
            emailField.setError("Email is required")
            emailField.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Please provide valid Email")
            emailField.requestFocus()
            return
        }
        progressbar.isVisible = true
        requestNewPasswordButton.isEnabled = false

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener {
                progressbar.isVisible = false
                requestNewPasswordButton.isEnabled = true
                if (it.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Check your email to reset your password",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Try again! Something went wrong",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


    }
}