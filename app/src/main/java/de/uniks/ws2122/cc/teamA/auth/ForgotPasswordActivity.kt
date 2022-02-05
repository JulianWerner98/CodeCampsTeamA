package de.uniks.ws2122.cc.teamA.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.Constant.New_PASSWORD_SUCCESS_MSG
import de.uniks.ws2122.cc.teamA.MainActivity
import de.uniks.ws2122.cc.teamA.databinding.ActivityForgotPasswordBinding
import de.uniks.ws2122.cc.teamA.model.AppViewModel

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var viewModel: AppViewModel

    private lateinit var requestNewPasswordBtn: Button
    private lateinit var emailField: EditText
    private lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[AppViewModel::class.java]

        requestNewPasswordBtn = binding.btnNewPassword
        emailField = binding.editTextEmailAdress
        spinner = binding.spinner

        spinner.isVisible = false

        requestNewPasswordBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
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
        spinner.isVisible = true
        requestNewPasswordBtn.isEnabled = false

        if (v!!.id == requestNewPasswordBtn.id) {
            viewModel.newPasswordMail(email) { msg ->
                if (msg.equals(New_PASSWORD_SUCCESS_MSG)) {
                    changeToLoginScreen()
                } else {
                    spinner.isVisible = false
                    requestNewPasswordBtn.isEnabled = true
                }
                Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun changeToLoginScreen() {
        val intent = Intent(this, MainActivity::class.java).apply { }
        startActivity(intent)
    }
}