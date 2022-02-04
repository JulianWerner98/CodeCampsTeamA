package de.uniks.ws2122.cc.teamA

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.Constant.SUCCESS_MSG
import de.uniks.ws2122.cc.teamA.auth.ForgotPasswordActivity
import de.uniks.ws2122.cc.teamA.auth.RegisterActivity
import de.uniks.ws2122.cc.teamA.controller.AuthController
import de.uniks.ws2122.cc.teamA.databinding.ActivityMainBinding
import de.uniks.ws2122.cc.teamA.model.AppViewModel


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: AppViewModel
    private lateinit var authController: AuthController

    private lateinit var loginButton: Button
    private lateinit var emailField: EditText
    private lateinit var pwdField: EditText
    private lateinit var forgotPwd: TextView
    private lateinit var register: TextView
    private lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        authController = AuthController(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginButton = binding.btnLogin
        emailField = binding.editTextEmail
        pwdField = binding.editTextPassword
        forgotPwd = binding.textForgotPwd
        register = binding.textRegister
        spinner = binding.spinner

        spinner.isVisible = false

        register.setOnClickListener(this)
        forgotPwd.setOnClickListener(this)
        loginButton.setOnClickListener(this)

        viewModel = ViewModelProvider(this)[AppViewModel::class.java]

        if (authController.isLoggedIn()) {
            changeToGameSelectScreen()
        }

        //var user = User()
        //user.email = "mail"

        //viewModel.setLiveValueUser(user)

        //initLiveDataObserver()

    }

    private fun initLiveDataObserver() {
        viewModel.getLiveValueUser().observe(this, { value ->
            forgotPwd.text = value.email
        })
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                register.id -> {
                    val intent = Intent(this, RegisterActivity::class.java).apply { }
                    startActivity(intent)
                }
                forgotPwd.id -> {
                    val intent = Intent(this, ForgotPasswordActivity::class.java).apply { }
                    startActivity(intent)
                }
                loginButton.id -> {
                    loginUser()
                }
            }
        }
    }

    private fun loginUser() {
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
        spinner.isVisible = true
        loginButton.isEnabled = false
        authController.loginUser(email, pwd) { statusMsg ->
            Toast.makeText(this, statusMsg, Toast.LENGTH_SHORT).show()
            spinner.isVisible = false
            if (statusMsg.equals(SUCCESS_MSG)) {
                changeToGameSelectScreen()
            } else {
                loginButton.isEnabled = true

            }
        }
    }

    private fun changeToGameSelectScreen() {
        println("Already Logged In")
        TODO("Not yet implemented")
    }

}
