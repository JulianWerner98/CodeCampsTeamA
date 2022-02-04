package de.uniks.ws2122.cc.teamA.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppViewModel : ViewModel() {
    private var user = User()
    private var liveValueUser: MutableLiveData<User> = MutableLiveData()

    //Setter
    fun setLiveValueUser(value: User):User {
        liveValueUser.value = value
        return user
    }

    fun setUser(user: User): User{
        this.user = user
        return user
    }

    //Getter
    fun getLiveValueUser(): LiveData<User> {
        return liveValueUser
    }
    fun getUser(): User{
        return user
    }

}