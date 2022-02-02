package de.uniks.ws2122.cc.teamA

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import de.uniks.ws2122.cc.teamA.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding

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
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

}