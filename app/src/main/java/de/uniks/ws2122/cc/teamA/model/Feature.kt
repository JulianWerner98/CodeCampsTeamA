package de.uniks.ws2122.cc.teamA.model

data class Feature(
    val geometry: Geometry,
    val id: Int,
    val properties: Properties,
    val type: String) {
    constructor(): this(Geometry(),0,Properties(),"")

}