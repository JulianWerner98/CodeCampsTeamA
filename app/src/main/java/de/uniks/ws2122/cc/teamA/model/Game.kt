package de.uniks.ws2122.cc.teamA.model

abstract class Game(
    var winner: String = "",
    var players: MutableList<String> = mutableListOf(),
    var id: String? = null
)