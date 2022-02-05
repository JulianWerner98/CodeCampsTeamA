package de.uniks.ws2122.cc.teamA.controller

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import de.uniks.ws2122.cc.teamA.Constant.ERROR_MSG
import de.uniks.ws2122.cc.teamA.Constant.FIREBASE_DATABASE_URL
import de.uniks.ws2122.cc.teamA.Constant.LOGIN_SUCCESS_MSG
import de.uniks.ws2122.cc.teamA.Constant.New_PASSWORD_SUCCESS_MSG
import de.uniks.ws2122.cc.teamA.model.User

class AuthController {
    private var mAuth: FirebaseAuth = Firebase.auth

    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun loginUser(email: String, pwd: String, callback: (result: String) -> Unit) {
        if (email.isNotEmpty() && email.isNotEmpty() && pwd.isNotEmpty() && pwd.isNotBlank()) {
            mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback.invoke(LOGIN_SUCCESS_MSG)
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

    fun registerUser(
        email: String,
        pwd: String,
        nickname: String,
        callback: (result: User?) -> Unit
    ) {
        mAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                var fbUser = getCurrentUser()
                var user = User(nickname, email, fbUser!!.uid)
                FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL)
                    .getReference("Users")
                    .child(fbUser!!.uid)
                    .setValue(user).addOnCompleteListener { it ->
                        if (it.isSuccessful) {
                            callback.invoke(user)
                        } else {
                            callback.invoke(null)
                        }
                    }
            } else {
                callback.invoke(null)
            }
        }
    }

    fun logoutUser() {
       if (isLoggedIn()){
            mAuth.signOut()
       }
    }

    fun resetMail(email: String, callback: (result: String) -> Unit){
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback.invoke(New_PASSWORD_SUCCESS_MSG)
                } else {
                    callback.invoke(ERROR_MSG)
                }
            }
    }
}



