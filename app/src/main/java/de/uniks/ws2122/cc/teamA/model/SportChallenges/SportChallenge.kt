package de.uniks.ws2122.cc.teamA.model.SportChallenges

import de.uniks.ws2122.cc.teamA.model.Game

data class SportChallenge(
    val mode: String,
    val option: String,
    val currentTime: String = "0",
    val countedSteps: Int = 0
) : Game()
