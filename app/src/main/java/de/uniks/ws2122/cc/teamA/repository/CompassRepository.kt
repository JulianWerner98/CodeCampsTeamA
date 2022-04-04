package de.uniks.ws2122.cc.teamA.repository

import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.*
import com.google.gson.Gson
import de.uniks.ws2122.cc.teamA.CompassActivity
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.Constant.COMPASS_API_URL
import de.uniks.ws2122.cc.teamA.Constant.COMPASS_GAME
import de.uniks.ws2122.cc.teamA.Constant.GAMES
import de.uniks.ws2122.cc.teamA.Constant.MATCH_REQUEST
import de.uniks.ws2122.cc.teamA.Constant.USERS_PATH
import de.uniks.ws2122.cc.teamA.model.AppViewModel
import de.uniks.ws2122.cc.teamA.model.compassGame.CompassGame
import de.uniks.ws2122.cc.teamA.model.compassGame.Feature
import de.uniks.ws2122.cc.teamA.model.compassGame.GeoportalData
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
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

    fun createGame(compassGame: CompassGame?, callback: (CompassGame?) -> Unit) {
        if (compassGame != null) {
            val matchRef = compassGamesRef.push()
            compassGame.id = matchRef.key
            compassGamesRef.child(matchRef.key!!).setValue(compassGame).addOnCompleteListener {
                if (it.isSuccessful) {
                    rootRef.child(USERS_PATH).child(compassGame.players[0])
                        .child(Constant.COMPASS_GAME).setValue(matchRef.key).addOnCompleteListener {
                            if (it.isSuccessful) {
                                gamesRef.child(Constant.MATCH_REQUEST).child(COMPASS_GAME)
                                    .setValue(object {
                                        val GAME = matchRef.key;
                                    })
                                callback.invoke(compassGame)
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

    fun getRequest(appViewModel: AppViewModel, callback: (CompassGame?) -> Unit) {
        gamesRef.child(MATCH_REQUEST).child(COMPASS_GAME).get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result.value != null) {
                    // Remove Match Request
                    it.result.ref.removeValue()
                    // Set Game id to User
                    val gameId = (it.result.value as HashMap<String, String>)["game"].toString()
                    rootRef.child(USERS_PATH).child(appViewModel.getUID()).child(COMPASS_GAME)
                        .setValue(gameId)
                        .addOnSuccessListener {
                            rootRef.child(GAMES).child(COMPASS_GAME).child(gameId).child("players")
                                .child("1").setValue(appViewModel.getUID())
                            getGame(appViewModel, callback)
                        }
                } else {
                    callback.invoke(null)
                }
            } else {
                callback.invoke(null)
            }
        }
    }

    fun setListenerToGame(
        currentGameId: String?,
        callback: (CompassGame?) -> Unit
    ) {
        compassGamesRef.child(currentGameId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback.invoke(snapshot.getValue(de.uniks.ws2122.cc.teamA.model.compassGame.CompassGame::class.java))
                }

                override fun onCancelled(error: DatabaseError) {}

            })
    }

    fun startTime(currentGame: CompassGame, playerNumber: String) {
        compassGamesRef.child(currentGame.id!!).child("player" + playerNumber + "Starttime").setValue(Date())
    }

    fun endTime(currentGame: CompassGame, playerNumber: String) {
        compassGamesRef.child(currentGame.id!!).child("player" + playerNumber + "Endtime").setValue(Date())
    }

    fun exitGame(appViewModel: AppViewModel, game: CompassGame?) {
        rootRef.child(USERS_PATH).child(appViewModel.getUID()).child(COMPASS_GAME).removeValue()
        if(game!!.players.size < 2) {
            compassGamesRef.child(game!!.id.toString()).removeValue()
        }
    }

}

