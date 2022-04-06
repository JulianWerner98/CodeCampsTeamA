package de.uniks.ws2122.cc.teamA.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.friendlist.controller.FriendRequestController

class FriendRequestViewModel : ViewModel(){
    private var receivedList = mutableListOf<User>()
    private var liveDataRequestList = MutableLiveData<List<User>>()
    private var sendList = mutableListOf<User>()
    private var liveDataSendList = MutableLiveData<List<User>>()
    private var friendRequestController = FriendRequestController()

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

    fun getFriendRequestController(): FriendRequestController{
        return friendRequestController
    }

    // Logic

    // Get friend request received list from database
    fun fetchReceivedRequestList(){
        friendRequestController.getReceivedList { result ->
           //Log.d("Request", "Requestsize: ${result.size}")
           // Log.d("Request", "Requestsize2: ${receivedList.size}")
            receivedList = result
           // Log.d("Request", "Requestsize3: ${receivedList.size}")
           // Log.d("Request", "Requestsize4: ${liveDataRequestList.value?.size}")
            setLiveDataRequestList()
           // Log.d("Request", "Requestsize5: ${liveDataRequestList.value?.size}")
        }
    }

    // Get friend request send list
    fun fetchSendRequestList(){
        friendRequestController.getSendRequestList { result ->
            sendList = result
            setLiveDateSendList()
        }
    }
}