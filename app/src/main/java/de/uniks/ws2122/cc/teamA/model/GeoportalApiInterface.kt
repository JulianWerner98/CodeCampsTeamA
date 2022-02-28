package de.uniks.ws2122.cc.teamA.model

import retrofit2.Call
import retrofit2.http.GET

interface GeoportalApiInterface {

    @GET("")
    fun getData(): Call<GeoportalData>
}