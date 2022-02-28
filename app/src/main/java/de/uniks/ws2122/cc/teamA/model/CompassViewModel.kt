package de.uniks.ws2122.cc.teamA.model

import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.CompassActivity
import de.uniks.ws2122.cc.teamA.repository.CompassRepository

class CompassViewModel : ViewModel() {
    private var compassRepo: CompassRepository = CompassRepository()
    private var numberOfEmblems = 3


    fun getRandomLocation(compassActivity: CompassActivity, callback: (List<Feature>) -> Unit) {
        compassRepo.getApiObject(compassActivity, numberOfEmblems, callback)
    }
}