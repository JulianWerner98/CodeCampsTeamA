package de.uniks.ws2122.cc.teamA.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FriendRequestViewModel : ViewModel(){
    private var requestList = mutableListOf<User>()
    private var liveDataRequestList = MutableLiveData<List<User>>()
    private var sendList = mutableListOf<User>()
    private var liveDataSendList = MutableLiveData<List<User>>()

    init {
        liveDataRequestList.value = requestList
        liveDataSendList.value = sendList
    }

    // Setter
    fun setLiveDataRequestList(){
        liveDataSendList.value = requestList
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
}