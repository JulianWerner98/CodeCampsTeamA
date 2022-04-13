package de.uniks.ws2122.cc.teamA.model.ticTacToe

import de.uniks.ws2122.cc.teamA.model.Game

class TicTacToe(
    var fields: ArrayList<String> = arrayListOf(" ", " ", " ", " ", " ", " ", " ", " ", " "),
    var turn: String = ""
) : Game()
