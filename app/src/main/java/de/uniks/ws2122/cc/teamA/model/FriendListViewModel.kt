package de.uniks.ws2122.cc.teamA.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.friendlist.controller.FriendListController

class FriendListViewModel : ViewModel(){
    private var friendsList = mutableListOf<User>()
    private var liveDataFriendList = MutableLiveData<List<User>>()
    private var friendListController = FriendListController()

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

    fun getFriendListController(): FriendListController {
        return friendListController
    }

    // Logic

    // Get friendslist from database
    fun fetchFriendList(){
        friendListController.getFriendList { friendList ->
            friendsList = friendList
            setLiveDataFriendsList()
        }
    }

}