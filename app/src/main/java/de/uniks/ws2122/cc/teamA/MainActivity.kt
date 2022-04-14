package de.uniks.ws2122.cc.teamA

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.Constant.LOGIN_SUCCESS_MSG
import de.uniks.ws2122.cc.teamA.auth.ForgotPasswordActivity
import de.uniks.ws2122.cc.teamA.auth.RegisterActivity
import de.uniks.ws2122.cc.teamA.databinding.ActivityMainBinding
import de.uniks.ws2122.cc.teamA.model.AppViewModel

/** Shows all Authentication possibilities
 * */
class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: AppViewModel

    private lateinit var loginButton: Button
    private lateinit var emailField: EditText
    private lateinit var pwdField: EditText
    private lateinit var forgotPwd: TextView
    private lateinit var register: TextView
    private lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Create Viewmodel
        viewModel = ViewModelProvider(this)[AppViewModel::class.java]

        //Bind elements to variables
        loginButton = binding.btnLogin
        emailField = binding.editTextEmail
        pwdField = binding.editTextPassword
        forgotPwd = binding.textForgotPwd
        register = binding.textRegister
        spinner = binding.spinner

        //Set visibility and listener
        spinner.isVisible = false
        register.setOnClickListener(this)
        forgotPwd.setOnClickListener(this)
        loginButton.setOnClickListener(this)
        
    }

    override fun onStart() {
        // Change to Game Select Screen if the User is already logged in
        super.onStart()
        if (viewModel.isLoggedIn()) {
            println("Already logged in")
            changeToGameSelectScreen()
        }
    }

    override fun onBackPressed() {
        //Disable Back Button
    }

    override fun onClick(v: View?) {
        //Start Register, Forgot password and login User
        when (v!!.id) {
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

    /** Checks if the input is correct and then login in the user
     * */
    private fun loginUser() {
        // Get input
        val email = emailField.text.trim().toString()
        val pwd = pwdField.text.trim().toString()
        //Is there an email?
        if (email.isEmpty() or email.isBlank()) {
            emailField.error = "Email is required"
            emailField.requestFocus()
            return
        }
        //Is the email valid?
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.error = "Please provide valid Email"
            emailField.requestFocus()
            return
        }
        //Is there a password?
        if (pwd.isEmpty() or pwd.isBlank()) {
            pwdField.error = "Password is required"
            pwdField.requestFocus()
            return
        }
        //Start login in
        spinner.isVisible = true
        loginButton.isEnabled = false
        viewModel.loginUser(email, pwd) { statusMsg ->
            Toast.makeText(this, statusMsg, Toast.LENGTH_SHORT).show()
            spinner.isVisible = false
            if (statusMsg.equals(LOGIN_SUCCESS_MSG)) {
                changeToGameSelectScreen()
            } else {
                loginButton.isEnabled = true
            }
        }
    }
    /** Change to Game Select Screen
     */
    private fun changeToGameSelectScreen() {
        val intent = Intent(this, GameSelectActivity::class.java).apply { }
        startActivity(intent)
    }

}
