package de.uniks.ws2122.cc.teamA.model.sportChallenge

import de.uniks.ws2122.cc.teamA.model.Game

data class SportChallenge(
    var mode: String = "",
    var option: String = "",
    var userTime: Double = 0.0,
    var enemyTime: Double = 0.0,
    var userCountedSteps: Int = 0,
    var enemyCountedSteps: Int = 0,
    var userMeters: Float = .0f,
    var enemyMeters: Float = .0f,
    var userSpeed: Float = .0f,
) : Game()
