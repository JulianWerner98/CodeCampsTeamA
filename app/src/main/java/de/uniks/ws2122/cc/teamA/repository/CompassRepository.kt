package de.uniks.ws2122.cc.teamA.repository

import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import de.uniks.ws2122.cc.teamA.CompassActivity
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.Constant.COMPASS_API_URL
import de.uniks.ws2122.cc.teamA.Constant.COMPASS_GAME
import de.uniks.ws2122.cc.teamA.Constant.GAMES
import de.uniks.ws2122.cc.teamA.Constant.MATCH_REQUEST
import de.uniks.ws2122.cc.teamA.Constant.USERS_PATH
import de.uniks.ws2122.cc.teamA.model.AppViewModel
import de.uniks.ws2122.cc.teamA.model.CompassGame
import de.uniks.ws2122.cc.teamA.model.Feature
import de.uniks.ws2122.cc.teamA.model.GeoportalData
import kotlin.random.Random

class CompassRepository {
    private val rootRef: DatabaseReference =
        FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference
    private var gamesRef: DatabaseReference
    private var compassGamesRef: DatabaseReference

    init {
        gamesRef = rootRef.child(Constant.GAMES).ref
        compassGamesRef = gamesRef.child(Constant.COMPASS_GAME)
    }

    fun getApiObject(
        compassActivity: CompassActivity,
        howMuch: Int,
        callback: (List<Feature>) -> Unit
    ) {
        val requestQueue = Volley.newRequestQueue(compassActivity)
        val objectRequest = JsonObjectRequest(
            COMPASS_API_URL,
            { response ->
                val geoportalData = Gson().fromJson(response.toString(), GeoportalData::class.java)
                val features = geoportalData.features
                val nextValues = List(howMuch) { Random.nextInt(0, features.size) }
                val arrayList = ArrayList<Feature>()
                nextValues.forEach {
                    arrayList.add(features.get(it))
                }
                callback.invoke(arrayList)
            },
            { error ->
                Log.e("Rest Response Error", error.toString())
            }
        )
        requestQueue.add(objectRequest)
    }

    fun createGame(compassGame: CompassGame?) {
        if (compassGame != null) {
            val matchRef = compassGamesRef.push()
            compassGamesRef.child(matchRef.key!!).setValue(compassGame).addOnCompleteListener {
                if (it.isSuccessful) {
                    rootRef.child(USERS_PATH).child(compassGame.players[0])
                        .child(Constant.COMPASS_GAME).setValue(matchRef.key).addOnCompleteListener {
                            if (it.isSuccessful) {
                                gamesRef.child(Constant.MATCH_REQUEST).child(matchRef.key!!)
                                    .setValue(object {
                                        val FROM = compassGame.players[0];
                                        val Game = Constant.COMPASS_GAME
                                    })
                            }
                        }
                }
            }
        }
    }

    fun getGame(appViewModel: AppViewModel, callback: (CompassGame?) -> Unit) {
        rootRef.child(USERS_PATH).child(appViewModel.getUID()).child(COMPASS_GAME).get()
            .addOnCompleteListener { get ->
                if (get.isSuccessful) {
                    if (get.result.value != null) {
                        rootRef.child(GAMES).child(COMPASS_GAME).child(get.result.value.toString())
                            .get().addOnCompleteListener { gameResult ->
                                if (gameResult.isSuccessful) {
                                    if (gameResult.result.value != null) {
                                        callback.invoke(gameResult.result.getValue(CompassGame::class.java))
                                    } else {
                                        callback.invoke(null)
                                    }
                                } else {
                                    callback.invoke(null)
                                }
                            }
                    } else {
                        callback.invoke(null)
                    }
                } else {
                    callback.invoke(null)
                }
            }

    }

    fun getRequest(callback: (CompassGame?) -> Unit) {
        callback.invoke(null)
        return
        rootRef.child(GAMES).child(MATCH_REQUEST).get().addOnCompleteListener {
            if(it.isSuccessful) {

            }
        }
    }
}
