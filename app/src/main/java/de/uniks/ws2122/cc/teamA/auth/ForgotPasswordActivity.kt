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
import de.uniks.ws2122.cc.teamA.Constant.NEW_PASSWORD_SUCCESS_MSG
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
        //Create Viewmodel
        viewModel = ViewModelProvider(this)[AppViewModel::class.java]

        //Bind elements to variables
        requestNewPasswordBtn = binding.btnNewPassword
        emailField = binding.editTextEmailAdress
        spinner = binding.spinner

        //Set visibility and listener
        spinner.isVisible = false

        requestNewPasswordBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        var email = emailField.text.trim().toString()
        //Is there an email?
        if (email.isBlank() or email.isEmpty()) {
            emailField.setError("Email is required")
            emailField.requestFocus()
            return
        }
        //Is the email valid?
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Please provide valid Email")
            emailField.requestFocus()
            return
        }
        spinner.isVisible = true
        requestNewPasswordBtn.isEnabled = false
        //Start sending password forgot mail
        if (v!!.id == requestNewPasswordBtn.id) {
            viewModel.newPasswordMail(email) { msg ->
                if (msg.equals(NEW_PASSWORD_SUCCESS_MSG)) {
                    changeToLoginScreen()
                } else {
                    spinner.isVisible = false
                    requestNewPasswordBtn.isEnabled = true
                }
                Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    /** Change to login Screen
     */
    private fun changeToLoginScreen() {
        val intent = Intent(this, MainActivity::class.java).apply { }
        startActivity(intent)
    }
}