package de.uniks.ws2122.cc.teamA.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import de.uniks.ws2122.cc.teamA.Constant.ERROR_MSG
import de.uniks.ws2122.cc.teamA.Constant.FIREBASE_URL
import de.uniks.ws2122.cc.teamA.Constant.LOGIN_SUCCESS_MSG
import de.uniks.ws2122.cc.teamA.Constant.NEW_PASSWORD_SUCCESS_MSG
import de.uniks.ws2122.cc.teamA.Constant.NICKNAME_ERROR
import de.uniks.ws2122.cc.teamA.Constant.USERS_PATH
import de.uniks.ws2122.cc.teamA.model.User

class AuthRepository {

    constructor() {
        dbref = FirebaseDatabase.getInstance(FIREBASE_URL).reference
    }

    private var mAuth: FirebaseAuth = Firebase.auth
    private var dbref: DatabaseReference

    fun getCurrentFBUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    /** Try to login user and call the callback with the given message **/
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

    /** User is logged in? **/
    fun isLoggedIn(): Boolean {
        return getCurrentFBUser() != null
    }

    /** Check unique nickname and register user **/
    fun registerUser(
        email: String, pwd: String, nickname: String, callback: (result: User?) -> Unit
    ) {
        logoutUser()
        loginUser("admin@admin.de", "z4CKfEpBE254p6") { msg ->
            if (msg.equals(LOGIN_SUCCESS_MSG)) {
                getAllUserNicknames { userStringList ->
                    if (nickname in userStringList) {
                        callback.invoke(User("", "", NICKNAME_ERROR))
                    } else {
                        mAuth.createUserWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener { it ->
                                if (it.isSuccessful) {
                                    var fbUser = getCurrentFBUser()
                                    var user = User(nickname, email, fbUser!!.uid)
                                    dbref
                                        .child(USERS_PATH)
                                        .child(fbUser.uid)
                                        .setValue(user).addOnCompleteListener { it2 ->
                                            if (it2.isSuccessful) {
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
                }
            }
        }

    }

    /** logout user **/
    fun logoutUser() {
        if (isLoggedIn()) {
            mAuth.signOut()
        }
    }

    /** send password forgot mail **/
    fun resetMail(email: String, callback: (result: String) -> Unit) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback.invoke(NEW_PASSWORD_SUCCESS_MSG)
                } else {
                    callback.invoke(ERROR_MSG)
                }
            }
    }

    /** get all nicknames from the database, to check if the name exists **/
    private fun getAllUserNicknames(callback: (result: ArrayList<String>) -> Unit) {
        getAllUsers { userList ->
            val userStringList = ArrayList<String>()
            userList.forEach { it ->
                userStringList.add(it.nickname)
            }
            callback.invoke(userStringList)
        }
    }

    /** Get All User from Database **/
    private fun getAllUsers(callback: (result: ArrayList<User>) -> Unit) {
        dbref.child(USERS_PATH).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val array = ArrayList<User>()
                snapshot.children.forEach {
                    var user = it.getValue(User::class.java)
                    if (user != null) array.add(user)
                }
                callback.invoke(array)
                dbref.child(USERS_PATH).removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    /** get Current User **/
    fun getCurrentUser(uid: String, callback: (result: User?) -> Unit) {
        dbref.child(USERS_PATH).child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                callback.invoke(user)
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }
}



