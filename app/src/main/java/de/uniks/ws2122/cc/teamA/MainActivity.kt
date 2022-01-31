package de.uniks.ws2122.cc.teamA

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import de.uniks.ws2122.cc.teamA.login.ForgotPasswordActivity
import de.uniks.ws2122.cc.teamA.login.RegisterActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var loginButton: Button
    private lateinit var emailField: EditText
    private lateinit var pwdField: EditText
    private lateinit var forgotPwd: TextView
    private lateinit var register: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Set IDs to Vars
        loginButton = findViewById(R.id.loginButton)
        emailField = findViewById(R.id.emailField)
        pwdField = findViewById(R.id.pwdField)
        forgotPwd = findViewById(R.id.forgotPwdText)
        register = findViewById(R.id.registerText)
        progressBar = findViewById(R.id.progressBarMain)
        progressBar.isVisible = false

        register.setOnClickListener(this)
        forgotPwd.setOnClickListener(this)
        loginButton.setOnClickListener(this)

        auth = Firebase.auth

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val intent = Intent(this, SelectGameActivity::class.java).apply { }
            startActivity(intent)
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.registerText -> {
                    val intent = Intent(this, RegisterActivity::class.java).apply { }
                    startActivity(intent)
                }
                R.id.forgotPwdText -> {
                    val intent = Intent(this, ForgotPasswordActivity::class.java).apply { }
                    startActivity(intent)
                }
                R.id.loginButton -> {
                    userLogin()
                }
            }
        }
    }

    private fun userLogin() {
        val email = emailField.text.trim().toString()
        val pwd = pwdField.text.trim().toString()
        if (email.isEmpty() or email.isBlank()) {
            emailField.error = "Email is required"
            emailField.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.error = "Please provide valid Email"
            emailField.requestFocus()
            return
        }
        if (pwd.isEmpty() or pwd.isBlank()) {
            pwdField.error = "Password is required"
            pwdField.requestFocus()
            return
        }
        progressBar.isVisible = true
        loginButton.isEnabled = false
        auth.signInWithEmailAndPassword(email, pwd)
            .addOnCompleteListener {
                progressBar.isVisible = false
                if (it.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    loginButton.isEnabled = true
                    if (user!!.isEmailVerified) {
                        val intent = Intent(this, SelectGameActivity::class.java).apply { }
                        startActivity(intent)
                    } else {
                        user.sendEmailVerification()
                        Toast.makeText(
                            applicationContext, "Check your Email to verify account", Toast.LENGTH_LONG
                        ).show()

                    }

                } else {
                    loginButton.isEnabled = true
                    Toast.makeText(
                        applicationContext, "Failed to login! Try again", Toast.LENGTH_LONG
                    ).show()

                }
            }
    }

}