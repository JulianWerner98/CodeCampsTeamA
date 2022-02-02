package de.uniks.ws2122.cc.teamA.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import de.uniks.ws2122.cc.teamA.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    private lateinit var requestNewPasswordBtn: Button
    private lateinit var emailField: EditText
    private lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestNewPasswordBtn = binding.btnNewPassword
        emailField = binding.editTextEmailAdress
        spinner = binding.spinner

        spinner.isVisible = false
    }
}