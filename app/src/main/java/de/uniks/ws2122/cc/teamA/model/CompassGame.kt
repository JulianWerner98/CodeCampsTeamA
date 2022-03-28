package de.uniks.ws2122.cc.teamA.model

class CompassGame : Game {
    var objectList: ArrayList<Feature> = ArrayList()
    var player1Time: Int = 0
    var player2Time: Int = 0
    constructor() {}

    constructor(
        objectList: ArrayList<Feature>,
        player1Time: Int,
        player2Time: Int
    ){
        this.objectList = objectList
        this.player1Time = player1Time
        this.player2Time = player2Time
    }

}
