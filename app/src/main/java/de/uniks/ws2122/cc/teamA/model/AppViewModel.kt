package de.uniks.ws2122.cc.teamA.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.Constant.NICKNAME_ERROR
import de.uniks.ws2122.cc.teamA.repository.AuthRepository
import de.uniks.ws2122.cc.teamA.repository.NotificationRepository

class AppViewModel : ViewModel() {
    private var liveValueUser: MutableLiveData<User> = MutableLiveData()
    private var authRepository = AuthRepository()
    private var notificationRepository = NotificationRepository()

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

    fun notificationRequestList(callback: (result: Boolean, id: Int, name: String) -> Unit) {
        notificationRepository.notificationRequestList(){ result, id, name ->
            if (result) {
                callback.invoke(true, id, name)
            }
        }
    }

    fun sendGameInviteNotification(callback: (result: Boolean, notification: Notification) -> Unit) {
        notificationRepository.sendGameInviteNotification() { result, notification ->
            if (result){
                callback.invoke(result, notification)
            }
        }
    }
}