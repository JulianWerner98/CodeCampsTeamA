package de.uniks.ws2122.cc.teamA.model.compassGame

import com.google.firebase.database.PropertyName

data class Properties(
    @get:PropertyName("Adresse")
    val Adresse: String,
    @get:PropertyName("Existiert")
    val Existiert: Any,
    @get:PropertyName("Geprueft")
    val Geprueft: Any,
    @get:PropertyName("Hsnr")
    val Hsnr: String,
    @get:PropertyName("Link")
    val Link: Any,
    @get:PropertyName("OBJECTID")
    val OBJECTID: Int,
    @get:PropertyName("Objekt")
    val Objekt: String,
    @get:PropertyName("Ort")
    val Ort: String,
    @get:PropertyName("PLZ")
    val PLZ: Int,
    @get:PropertyName("Plz_Ort")
    val Plz_Ort: String,
    @get:PropertyName("Str")
    val Str: String,
    @get:PropertyName("Tel")
    val Tel: String
) {
    constructor() :
            this(
                "", false, false, "", "", 0,
                "", "", 0, "", "", ""
            )
}