package de.uniks.ws2122.cc.teamA.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.Constant.ERROR_MSG
import de.uniks.ws2122.cc.teamA.Constant.LOGIN_SUCCESS_MSG
import de.uniks.ws2122.cc.teamA.Constant.NICKNAME_ERROR
import de.uniks.ws2122.cc.teamA.Constant.REGISTER_SUCCESS_MSG
import de.uniks.ws2122.cc.teamA.MainActivity
import de.uniks.ws2122.cc.teamA.databinding.ActivityRegisterBinding
import de.uniks.ws2122.cc.teamA.model.AppViewModel

class RegisterActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AppViewModel

    private lateinit var nickname: EditText
    private lateinit var emailAddress: EditText
    private lateinit var pwdField: EditText
    private lateinit var registerButton: Button
    private lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[AppViewModel::class.java]

        nickname = binding.editTextNickname
        emailAddress = binding.editTextEmailAdresse
        pwdField = binding.editTextPassword
        registerButton = binding.btnRegister
        spinner = binding.spinner

        spinner.isVisible = false
        registerButton.isEnabled = false

        registerButton.setOnClickListener(this)
        nickname.addTextChangedListener(this)
        emailAddress.addTextChangedListener(this)
        pwdField.addTextChangedListener(this)
    }

    override fun onClick(v: View?) {
        var email = emailAddress.text.trim().toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailAddress.error = "Please provide valid Email"
            emailAddress.requestFocus()
            return
        }
        var pwd = pwdField.text.trim().toString()
        if (pwd.length < 6) {
            pwdField.error = "Password muss be longer than 6"
            pwdField.requestFocus()
            return
        }
        emailAddress.error = null
        pwdField.error = null

        spinner.isVisible = true
        emailAddress.isEnabled = false
        nickname.isEnabled = false
        pwdField.isEnabled = false
        registerButton.isEnabled = false

        val name = nickname.text.trim().toString()
        viewModel.registerUser(email, pwd, name) { user ->
            var toastText = ""
            if (user != null) {
                if (user.id.equals(NICKNAME_ERROR)) {
                    toastText = NICKNAME_ERROR
                    spinner.isVisible = false
                    emailAddress.isEnabled = true
                    nickname.isEnabled = true
                    pwdField.isEnabled = true
                    registerButton.isEnabled = true
                    nickname.setText("")

                } else {
                    toastText = REGISTER_SUCCESS_MSG
                    viewModel.loginUser(email, pwd) { msg ->
                        if (msg.equals(LOGIN_SUCCESS_MSG)) {
                            changeToGameSelectScreen()
                        } else {
                            changeToLoginScreen()
                        }
                    }
                }
            } else {
                spinner.isVisible = false
                emailAddress.isEnabled = true
                nickname.isEnabled = true
                pwdField.isEnabled = true
                registerButton.isEnabled = true
                toastText = ERROR_MSG
            }
            Toast.makeText(applicationContext, toastText, Toast.LENGTH_LONG).show()
        }
    }

    private fun changeToLoginScreen() {
        val intent = Intent(this, MainActivity::class.java).apply { }
        startActivity(intent)
    }

    private fun changeToGameSelectScreen() {
        TODO("Not yet implemented")
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        registerButton.isEnabled =
            nickname.text.isNotEmpty() && nickname.text.isNotBlank() &&
                    emailAddress.text.isNotEmpty() && emailAddress.text.isNotBlank() &&
                    pwdField.text.isNotEmpty() && pwdField.text.isNotBlank()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable?) {}
}


