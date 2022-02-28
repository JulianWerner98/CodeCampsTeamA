package de.uniks.ws2122.cc.teamA.model.TicTacToe

import de.uniks.ws2122.cc.teamA.model.Game

class TicTacToe(
    var fields: String = "_________",
    var isMyTurn: Boolean = false,
    var isCircle: Boolean = false
) : Game()
