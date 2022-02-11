package de.uniks.ws2122.cc.teamA.model

abstract class Game(
    var id: String = "",
    var players: MutableList<String> = mutableListOf()
)