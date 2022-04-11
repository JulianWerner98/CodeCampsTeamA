package de.uniks.ws2122.cc.teamA.model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.repository.FriendSystemRepository

class FriendRequestViewModel : ViewModel(){
    private var receivedList = mutableListOf<User>()
    private var liveDataRequestList = MutableLiveData<List<User>>()
    private var sendList = mutableListOf<User>()
    private var liveDataSendList = MutableLiveData<List<User>>()

    // Repository
    private var friendSystemRepo = FriendSystemRepository()

    init {
        liveDataRequestList.value = receivedList
        liveDataSendList.value = sendList
    }

    // Setter
    fun setLiveDataRequestList(){
        liveDataRequestList.value = receivedList
    }

    fun setLiveDateSendList(){
        liveDataSendList.value = sendList
    }

    // Getter
    fun getLiveDataRequestList():LiveData<List<User>>{
        return liveDataRequestList
    }

    fun getLiveDataSendList():LiveData<List<User>>{
        return liveDataSendList
    }

    // Logic
    // Get friend request received list from database
    fun fetchReceivedRequestList(){
        friendSystemRepo.fetchReceivedList { result ->
            receivedList = result
            setLiveDataRequestList()
        }
    }

    // Get friend request send list
    fun fetchSendRequestList(){
        friendSystemRepo.fetchSendRequestList { result ->
            sendList = result
            setLiveDateSendList()
        }
    }

    fun acceptFriendRequest(friend: User, callback: (result: String) -> Unit) {
        friendSystemRepo.acceptFriendRequest(friend) { msg ->
            callback.invoke(msg)
        }
    }

    fun declineFriendRequest(friend: User, callback: (result: String) -> Unit) {
        friendSystemRepo.declineFriendRequest(friend) { msg ->
            callback.invoke(msg)
        }
    }

    fun cancelSendFriendRequest(friend: User, callback: (result: String) -> Unit) {
        friendSystemRepo.cancelSendFriendRequest(friend) {  msg ->
            callback.invoke(msg)
        }
    }
}