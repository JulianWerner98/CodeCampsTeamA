package de.uniks.ws2122.cc.teamA.model.compassGame

data class Geometry(
    val coordinates: List<Double>,
    val type: String
) {
    constructor() : this(ArrayList(),"")
}