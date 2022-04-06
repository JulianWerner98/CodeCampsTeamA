package de.uniks.ws2122.cc.teamA.model.compassGame

import de.uniks.ws2122.cc.teamA.model.Game
import java.util.*
import kotlin.collections.ArrayList

class CompassGame : Game {
    var objectList: ArrayList<Feature> = ArrayList()
    var player0Starttime: Date? = null
    var player1Starttime: Date? = null
    var player0Endtime: Date? = null
    var player1Endtime: Date? = null
    constructor() {}

    constructor(
        objectList: ArrayList<Feature>,
        player0Starttime: Date?,
        player1Starttime: Date?,
        player0Endtime: Date?,
        player1Endtime: Date?
    ){
        this.objectList = objectList
        this.player0Starttime = player0Starttime
        this.player1Starttime = player1Starttime
        this.player0Endtime = player0Endtime
        this.player1Endtime = player1Endtime
    }

}
