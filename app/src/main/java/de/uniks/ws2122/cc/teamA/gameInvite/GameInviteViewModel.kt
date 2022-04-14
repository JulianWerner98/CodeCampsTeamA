package de.uniks.ws2122.cc.teamA.gameInvite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.model.GameInvites
import de.uniks.ws2122.cc.teamA.repository.GameInviteRepository

class GameInviteViewModel: ViewModel() {
    private var gameInviteList = mutableListOf<GameInvites>()

    // Live Data
    private var gameInviteListData = MutableLiveData<List<GameInvites>>()

    // Repo
    private var gameInviteRepo = GameInviteRepository()

    init {
        gameInviteListData.value = gameInviteList
    }

    // Setter
    fun setLiveGameInviteListData(){
        gameInviteListData.value = gameInviteList
    }

    // Getter
    fun getLiveGameInviteListData() : MutableLiveData<List<GameInvites>>{
        return gameInviteListData
    }

    // Logic
    // Delete invite
    fun deleteInvite(gameName: String, friendName: String) {
        gameInviteRepo.deleteInvite(gameName, friendName)
    }

    // Fetch invite list
    fun fetchInvitesList() {
        gameInviteRepo.fetchInvitesList(){ inviteList ->
            gameInviteList = inviteList
            setLiveGameInviteListData()
        }
    }


}