package de.uniks.ws2122.cc.teamA.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.Constant.NICKNAME_ERROR
import de.uniks.ws2122.cc.teamA.controller.AuthController

class AppViewModel : ViewModel() {
    private var liveValueUser: MutableLiveData<User> = MutableLiveData()
    private var authController = AuthController()

    //Setter
    fun setUser(user: User): User {
        liveValueUser.value = user
        return user
    }

    //Getter
    fun getLiveValueUser(): LiveData<User> {
        if (authController.isLoggedIn() && liveValueUser.value == null) {
            val uid = authController.getCurrentFBUser()!!.uid
            authController.getCurrentUser(uid) { user ->
                if (user != null) {
                    setUser(user)
                }
            }
        }
        return liveValueUser
    }

    //Auth Functions
    fun loginUser(email: String, pwd: String, callback: (result: String) -> Unit) {
        authController.loginUser(email, pwd, callback)
    }

    fun logoutUser() {
        authController.logoutUser()
    }

    fun registerUser(
        email: String,
        pwd: String,
        nickname: String,
        callback: (result: User?) -> Unit
    ) {
        authController.registerUser(email, pwd, nickname) { user ->
            if (user != null && !user?.id.equals(NICKNAME_ERROR)) {
                setUser(user)
            }
            callback.invoke(user)
        }
    }

    fun isLoggedIn(): Boolean {
        return authController.isLoggedIn()
    }

    fun newPasswordMail(email: String, callback: (result: String) -> Unit) {
        authController.resetMail(email, callback)
    }


}