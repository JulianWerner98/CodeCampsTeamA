package de.uniks.ws2122.cc.teamA.model

class User(
    var nickname: String = "",
    var email: String = "",
    var id: String = "",
    var friends: ArrayList<User> = ArrayList(),
    var games: ArrayList<Game> = ArrayList()
)