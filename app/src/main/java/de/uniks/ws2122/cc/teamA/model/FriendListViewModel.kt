package de.uniks.ws2122.cc.teamA.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.repository.FriendSystemRepository

class FriendListViewModel : ViewModel(){
    private var friendsList = mutableListOf<User>()
    private var liveDataFriendList = MutableLiveData<List<User>>()

    // Repository
    private var friendSystemRepo = FriendSystemRepository()

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

    // Logic
    // Get friendslist from database
    fun fetchFriendList(){
        friendSystemRepo.fetchFriendList { friendList ->
            friendsList = friendList
            setLiveDataFriendsList()
        }
    }

    fun sendFriendRequest(nickName: String, callback: (result: String) -> Unit) {
        friendSystemRepo.sendFriendRequest(nickName){ msg ->
            callback.invoke(msg)
        }
    }

    fun removeFriend(friendId: String, callback: (result: Boolean) -> Unit) {
        friendSystemRepo.removeFriend(friendId) { result ->
            callback.invoke(result)
        }
    }

}