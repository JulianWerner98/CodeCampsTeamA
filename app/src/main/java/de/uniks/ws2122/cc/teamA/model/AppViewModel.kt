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

    /** Setter **/
    private fun setUser(user: User): User {
        liveValueUser.value = user
        return user
    }

    /** Getter **/
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
    /** Get uid from current user **/
    fun getUID(): String {
        return authRepository.getCurrentFBUser()!!.uid
    }

    /** login user **/
    fun loginUser(email: String, pwd: String, callback: (result: String) -> Unit) {
        authRepository.loginUser(email, pwd, callback)
    }

    /** logout user **/
    fun logoutUser() {
        authRepository.logoutUser()
    }

    /** register user **/
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

    /** check if user is logged in **/
    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }

    /** Send new password mail **/
    fun newPasswordMail(email: String, callback: (result: String) -> Unit) {
        authRepository.resetMail(email, callback)
    }

    /** Get Notifications from Database **/
    fun notificationRequestList(callback: (result: Boolean, id: Int, name: String) -> Unit) {
        notificationRepository.notificationRequestList(){ result, id, name ->
            if (result) {
                callback.invoke(true, id, name)
            }
        }
    }

    /** Send game invite Notification **/
    fun sendGameInviteNotification(callback: (result: Boolean, notification: Notification) -> Unit) {
        notificationRepository.sendGameInviteNotification() { result, notification ->
            if (result){
                callback.invoke(result, notification)
            }
        }
    }
}