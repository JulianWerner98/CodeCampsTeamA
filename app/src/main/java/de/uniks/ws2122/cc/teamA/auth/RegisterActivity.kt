package de.uniks.ws2122.cc.teamA.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import de.uniks.ws2122.cc.teamA.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var nickname: EditText
    private lateinit var emailAddress: EditText
    private lateinit var pwdField: EditText
    private lateinit var registerButton: Button
    private lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nickname = binding.editTextNickname
        emailAddress = binding.editTextEmailAdresse
        pwdField = binding.editTextPassword
        registerButton = binding.btnRegister
        spinner = binding.spinner

    }
}