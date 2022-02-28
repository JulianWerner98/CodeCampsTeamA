package de.uniks.ws2122.cc.teamA.model

import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.repository.CompassRepository
import de.uniks.ws2122.cc.teamA.repository.TicTacToeRepository

class CompassViewModel: ViewModel() {
    private var compassRepo: CompassRepository = CompassRepository()



    fun getRandomLocation(){
        compassRepo.getApiObject()
    }
}