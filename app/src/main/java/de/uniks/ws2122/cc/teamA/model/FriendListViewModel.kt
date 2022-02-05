package de.uniks.ws2122.cc.teamA.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FriendListViewModel : ViewModel(){
    private var friendsList = mutableListOf<User>()
    private var liveDataFriendList = MutableLiveData<List<User>>()

    init {
        liveDataFriendList.value = friendsList
    }

    // Setter
    fun setLiveDataFriendsList(){
        liveDataFriendList.value = friendsList
    }

    // Getter

    fun getLiveDataFriendList():LiveData<List<User>>{
        return liveDataFriendList
    }
}