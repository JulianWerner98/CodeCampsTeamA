package de.uniks.ws2122.cc.teamA.model

data class Properties(
    val Adresse: String,
    val Existiert: Any,
    val Geprueft: Any,
    val Hsnr: String,
    val Link: Any,
    val OBJECTID: Int,
    val Objekt: String,
    val Ort: String,
    val PLZ: Int,
    val Plz_Ort: String,
    val Str: String,
    val Tel: String
) {
    constructor() :
            this(
                "", false, false, "", "", 0,
                "", "", 0, "", "", ""
            )


}