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
import de.uniks.ws2122.cc.teamA.Constant.DRAW
import de.uniks.ws2122.cc.teamA.Constant.FIREBASE_URL
import de.uniks.ws2122.cc.teamA.Constant.GAMES
import de.uniks.ws2122.cc.teamA.Constant.HISTORIE
import de.uniks.ws2122.cc.teamA.Constant.INVITES
import de.uniks.ws2122.cc.teamA.Constant.LOSE
import de.uniks.ws2122.cc.teamA.Constant.MATCH_REQUEST
import de.uniks.ws2122.cc.teamA.Constant.NICKNAME
import de.uniks.ws2122.cc.teamA.Constant.STATISTIC
import de.uniks.ws2122.cc.teamA.Constant.USERS_PATH
import de.uniks.ws2122.cc.teamA.Constant.WIN
import de.uniks.ws2122.cc.teamA.model.AppViewModel
import de.uniks.ws2122.cc.teamA.model.Highscore
import de.uniks.ws2122.cc.teamA.model.MatchResult
import de.uniks.ws2122.cc.teamA.model.Notification
import de.uniks.ws2122.cc.teamA.model.compassGame.CompassGame
import de.uniks.ws2122.cc.teamA.model.compassGame.Feature
import de.uniks.ws2122.cc.teamA.model.compassGame.GeoportalData
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class CompassRepository {
    private val rootRef: DatabaseReference =
        FirebaseDatabase.getInstance(FIREBASE_URL).reference
    private var gamesRef: DatabaseReference = rootRef.child(GAMES).ref
    private var compassGamesRef: DatabaseReference = gamesRef.child(COMPASS_GAME)


    /** Get object from geoportal api**/
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

    /** Create Normal public game**/
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

    /** Try to get current game **/
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

    /** Look for a Match Request in the Match query **/
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

    /** Set listener to current game to detect changes **/
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

    /** Write current Start Time in Database **/
    fun startTime(currentGame: CompassGame, playerNumber: String) {
        compassGamesRef.child(currentGame.id!!).child("player" + playerNumber + "Starttime")
            .setValue(Date())
    }

    /** Write current End Time in Database **/
    fun endTime(currentGame: CompassGame, playerNumber: String) {
        compassGamesRef.child(currentGame.id!!).child("player" + playerNumber + "Endtime")
            .setValue(Date())
    }

    /** Surrender Game **/
    fun surrender(currentGame: CompassGame?, opponentPlayerNumber: String, function: () -> Unit) {
        val date = Date()
        date.time = 0
        compassGamesRef.child(currentGame!!.id!!)
            .child("player" + opponentPlayerNumber + "Starttime")
            .setValue(date).addOnSuccessListener {
                date.time = 150000
                compassGamesRef.child(currentGame!!.id!!)
                    .child("player" + opponentPlayerNumber + "Endtime")
                    .setValue(date).addOnSuccessListener { function.invoke() }
            }

    }

    /** Leave the game with statistic and history and calculate points **/
    fun exitGame(appViewModel: AppViewModel, game: CompassGame?) {
        rootRef.child(USERS_PATH).child(appViewModel.getUID()).child(COMPASS_GAME).removeValue()
        var matchResult = MatchResult()
        var userId = appViewModel.getUID()
        var opponentId = ""
        opponentId = if (game!!.players[0] == userId) game!!.players[1] else game!!.players[0]
        getUsername(opponentId) { opponentName ->
            matchResult.currentuser = "You"
            matchResult.gamename = COMPASS_GAME
            matchResult.opponent = opponentName
            if (game!!.winner == userId) {
                if (game.players[0] == userId) matchResult.points =
                    1000 - ((game.player0Endtime!!.time - game.player0Starttime!!.time) / 1000).toInt() * 5
                if (game.players[1] == userId) matchResult.points =
                    1000 - ((game.player1Endtime!!.time - game.player1Starttime!!.time) / 1000).toInt() * 5
                if (matchResult.points < 0) matchResult.points = 0
                matchResult.win = WIN
            } else {
                matchResult.points = 0
                matchResult.win = LOSE
            }
            rootRef.child(USERS_PATH).child(appViewModel.getUID()).child(STATISTIC)
                .child(COMPASS_GAME).get().addOnSuccessListener { dataSnapshot ->
                    var highscore =
                        dataSnapshot.getValue(de.uniks.ws2122.cc.teamA.model.Highscore::class.java)
                    if (highscore == null) highscore = Highscore()
                    if (highscore.points < matchResult.points) highscore.points =
                        matchResult.points
                    if (matchResult.win == WIN) highscore.wins += 1
                    if (matchResult.win == LOSE) highscore.loses += 1
                    if (matchResult.win == DRAW) highscore.draws += 1
                    rootRef.child(USERS_PATH).child(appViewModel.getUID()).child(STATISTIC)
                        .child(COMPASS_GAME).setValue(highscore)
                }
            rootRef.child(USERS_PATH).child(appViewModel.getUID()).child(STATISTIC)
                .child(HISTORIE).child(game!!.id.toString()).setValue(matchResult)
                .addOnSuccessListener {
                    rootRef.child(USERS_PATH).child(opponentId).child(STATISTIC).child(HISTORIE)
                        .child(game!!.id.toString()).get().addOnSuccessListener {
                            if (it.value != null) {
                                compassGamesRef.child(game!!.id.toString()).removeValue()
                            }
                        }
                }
        }
    }

    /** Set winner **/
    fun setWinner(game: CompassGame?) {
        compassGamesRef.child(game!!.id.toString()).child("winner").setValue(game!!.winner)
    }

    /** Delete Request from match request query **/
    fun deleteRequest(callback: () -> Unit) {
        gamesRef.child(MATCH_REQUEST).child(COMPASS_GAME).removeValue().addOnSuccessListener {
            callback.invoke()
        }
    }

    /** Send a game invite to a friend **/
    fun sendInvite(gameId: String, friendId: String, uid: String) {
        getUsername(uid) {
            rootRef.child(USERS_PATH).child(friendId).child(INVITES).child(COMPASS_GAME)
                .child(it)
                .setValue(gameId)
            
            val notficationId =  gameId
            val notification = Notification(notficationId.toString(), it, Constant.COMPASS_GAME)
            rootRef.child(Constant.NOTIFICATION).child(Constant.NOTIFICATIONGAMEINVITE).child(friendId).child(notficationId.toString()).setValue(notification)
        }
    }

    /** Get Username by Id**/
    private fun getUsername(uid: String, callback: (String) -> Unit) {
        rootRef.child(USERS_PATH).child(uid).child(NICKNAME).get().addOnSuccessListener {
            callback.invoke(it.value.toString())
        }
    }

    /** Join a game with als dependencies **/
    fun joinGame(appViewModel: AppViewModel, gameId: String, callback: (CompassGame?) -> Unit) {
        compassGamesRef.child(gameId).child("players").child("1").setValue(appViewModel.getUID())
            .addOnSuccessListener {
                rootRef.child(USERS_PATH).child(appViewModel.getUID()).child(COMPASS_GAME)
                    .setValue(gameId).addOnSuccessListener {
                        getGame(appViewModel, callback)
                    }
            }
    }

    /** Delete game **/
    fun deleteGame(game: CompassGame, uid: String, callback: () -> Unit) {
        compassGamesRef.child(game.id!!).removeValue().addOnSuccessListener {
            rootRef.child(USERS_PATH).child(uid).child(COMPASS_GAME).removeValue()
                .addOnSuccessListener {
                    callback.invoke()
                }
        }
    }
    /** Delete friend invite **/
    fun deleteInvite(uid: String, friendId: String, callback: () -> Unit) {
        getUsername(uid) {
            rootRef.child(USERS_PATH).child(friendId).child(INVITES).child(COMPASS_GAME).child(it)
                .removeValue().addOnSuccessListener {
                    callback.invoke()
                }
        }
    }

}

