package de.uniks.ws2122.cc.teamA.model.SportChallenges

import de.uniks.ws2122.cc.teamA.model.Game

data class SportChallenge(
    var mode: String = "",
    var option: String = "",
    var userTime: Long = 1,
    var userCountedSteps: Int = 1,
    var enemyCountedSteps: Int = 1
) : Game()
