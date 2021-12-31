package de.uniks.ws2122.cc.teamA.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import de.uniks.ws2122.cc.teamA.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {

    private lateinit var mAuth: FirebaseAuth;
    private lateinit var nickname: EditText
    private lateinit var emailAdresse: EditText
    private lateinit var pwdField: EditText
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = Firebase.auth

        nickname = findViewById(R.id.nicknameRegisterField)
        emailAdresse = findViewById(R.id.emailregisterField)
        pwdField = findViewById(R.id.pwdRegisterField)
        registerButton = findViewById(R.id.registerButton)
        registerButton.isEnabled = false
        progressBar = findViewById(R.id.progressBarRegister)
        progressBar.isVisible = false

        registerButton.setOnClickListener(this)
        nickname.addTextChangedListener(this)
        emailAdresse.addTextChangedListener(this)
        pwdField.addTextChangedListener(this)
    }

    override fun onClick(p0: View?) {
        if (registerButton.isEnabled) {
            var email = emailAdresse.text.trim().toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailAdresse.setError("Please provide valid Email")
                emailAdresse.requestFocus()
                return
            }
            var pwd = pwdField.text.trim().toString()
            if (pwd.length < 6) {
                pwdField.setError("Password muss be longer than 6")
                pwdField.requestFocus()
                return
            }
            progressBar.isVisible = true
            emailAdresse.isEnabled = false
            nickname.isEnabled = false
            pwdField.isEnabled = false
            registerButton.isEnabled = false
            mAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener {
                if (it.isSuccessful()) {
                    var user = User(nickname.text.trim().toString(), email)
                    FirebaseDatabase.getInstance(firebaseDatabaseURL)
                        .getReference("Users")
                        .child((FirebaseAuth.getInstance().currentUser!!.uid))
                        .setValue(user).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "You have been registered successfully!",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressBar.isVisible = false
                                val intent = Intent(this, MainActivity::class.java).apply { }
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Failed to registered! Try again",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressBar.isVisible = false
                                emailAdresse.isEnabled = true
                                nickname.isEnabled = true
                                pwdField.isEnabled = true
                            }
                        }.addOnFailureListener {
                            Toast.makeText(
                                applicationContext,
                                it.message + " <-> Failed to registered! Try again",
                                Toast.LENGTH_LONG
                            ).show()
                            println(it.stackTrace)
                        }

                }
            }

        }
    }


    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        registerButton.isEnabled =
            nickname.text.isNotEmpty() && nickname.text.isNotBlank() &&
                    emailAdresse.text.isNotEmpty() && emailAdresse.text.isNotBlank() &&
                    pwdField.text.isNotEmpty() && pwdField.text.isNotBlank()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(p0: Editable?) {

    }

}
