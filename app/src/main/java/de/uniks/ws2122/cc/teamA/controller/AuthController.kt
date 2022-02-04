package de.uniks.ws2122.cc.teamA.controller

import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import de.uniks.ws2122.cc.teamA.Constant.ERROR_MSG
import de.uniks.ws2122.cc.teamA.Constant.SUCCESS_MSG
import de.uniks.ws2122.cc.teamA.MainActivity
import de.uniks.ws2122.cc.teamA.model.AppViewModel

class AuthController {
    private var auth: FirebaseAuth = Firebase.auth
    private lateinit var viewModel: AppViewModel

    constructor(mainActivity: MainActivity) {
        viewModel = ViewModelProvider(mainActivity)[AppViewModel::class.java]
    }

    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun loginUser(email: String, pwd: String, callback: (result: String) -> Unit) {
        if (email.isNotEmpty() && email.isNotEmpty() && pwd.isNotEmpty() && pwd.isNotBlank()) {
            auth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = getCurrentUser()
                        callback.invoke(SUCCESS_MSG)
                    } else {
                        callback.invoke(ERROR_MSG)
                    }
                }
        } else {
            callback.invoke(ERROR_MSG)
        }
    }

    fun isLoggedIn(): Boolean {
        return getCurrentUser() != null
    }
}



