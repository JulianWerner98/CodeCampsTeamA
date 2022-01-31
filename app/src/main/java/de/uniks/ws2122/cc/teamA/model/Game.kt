package de.uniks.ws2122.cc.teamA.model

open class Game {
    private var gameId: String = ""
    private var players: ArrayList<Player> = ArrayList()

    constructor() {}
    constructor(gameId: String) {
        this.gameId = gameId
    }

    fun withPlayer(player: Player) {
        if (players.contains(player)) return
        players.add(player)
        player.setGame(this)
    }
}