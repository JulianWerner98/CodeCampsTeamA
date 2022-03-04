package de.uniks.ws2122.cc.teamA.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.Constant.NICKNAME_ERROR
import de.uniks.ws2122.cc.teamA.repository.AuthRepository

class AppViewModel : ViewModel() {
    private var liveValueUser: MutableLiveData<User> = MutableLiveData()
    private var authRepository = AuthRepository()

    //Setter
    fun setUser(user: User): User {
        liveValueUser.value = user
        return user
    }

    //Getter
    fun getLiveValueUser(): LiveData<User> {
        if (authRepository.isLoggedIn() && liveValueUser.value == null) {
            val uid = authRepository.getCurrentFBUser()!!.uid
            authRepository.getCurrentUser(uid) { user ->
                if (user != null) {
                    setUser(user)
                }
            }
        }
        return liveValueUser
    }

    fun getUID(): String {
        return authRepository.getCurrentFBUser()!!.uid
    }

    //Auth Functions
    fun loginUser(email: String, pwd: String, callback: (result: String) -> Unit) {
        authRepository.loginUser(email, pwd, callback)
    }

    fun logoutUser() {
        authRepository.logoutUser()
    }

    fun registerUser(
        email: String,
        pwd: String,
        nickname: String,
        callback: (result: User?) -> Unit
    ) {
        authRepository.registerUser(email, pwd, nickname) { user ->
            if (user != null && !user.id.equals(NICKNAME_ERROR)) {
                setUser(user)
            }
            callback.invoke(user)
        }
    }

    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }

    fun newPasswordMail(email: String, callback: (result: String) -> Unit) {
        authRepository.resetMail(email, callback)
    }


}