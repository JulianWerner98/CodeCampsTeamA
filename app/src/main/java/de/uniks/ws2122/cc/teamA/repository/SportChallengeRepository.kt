package de.uniks.ws2122.cc.teamA.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant

class SportChallengeRepository {

    private var mode: String = ""
    private var option: String = ""
    private lateinit var callbackEnemy: (result: String) -> Unit

    //References
    private val rootRef = FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference
    private var gamesRef = rootRef.child(Constant.GAMES).ref
    private var sportRef = gamesRef.child(Constant.SPORT_CHALLENGE).ref
    private lateinit var matchRef: DatabaseReference
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private val currentUserRef = rootRef.child(Constant.USERS_PATH).child(currentUser.uid).ref

    fun startMatchMaking(mode: String, option: String, callback: (player: String) -> Unit) {

        this.callbackEnemy = callback
        this.mode = mode
        this.option = option

        if (mode.isEmpty()) {

            loadRunningGame()
        } else {

            searchMatch()
        }
    }

    fun hasRunningGame(callback: (result: Boolean) -> Unit) {

        rootRef.child(Constant.USERS_PATH)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val hasGame = snapshot.child(currentUser.uid).hasChild(Constant.SPORT_CHALLENGE)
                    Log.d("SPORTRepo", "hasGame: $hasGame")
                    callback.invoke(hasGame)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun determineSportChallengeReference(callback: (result: Boolean) -> Unit) {

        rootRef.child(Constant.USERS_PATH)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val sportRefKey = snapshot.child(currentUser.uid)
                        .child(Constant.SPORT_CHALLENGE).value.toString()
                    matchRef = sportRef.child(sportRefKey)
                    callback.invoke(true)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    //load the running game
    private fun loadRunningGame() {

        determineSportChallengeReference() { foundRef ->

            if (foundRef) {

                matchRef.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.child(Constant.PLAYER1).child(Constant.ID).value.toString() == currentUser.uid) {

                            callbackEnemy.invoke(Constant.PLAYER2)
                        }
                        else {

                            callbackEnemy.invoke(Constant.PLAYER1)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
        }

        Log.d("SPORTRepo", "loaded game")
    }

    //looks for match if not it creates one
    private fun searchMatch() {

        sportRef.child(Constant.OPENMATCHES).orderByKey()
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val openMatches = snapshot.children.iterator()
                    var matchKey = ""

                    while (openMatches.hasNext()) {

                        val openMatch = openMatches.next()

                        Log.d("SportRepo", "${openMatch.child(Constant.MODE).value}")
                        Log.d("SportRepo", "${openMatch.child(Constant.OPTION).value}")

                        if (openMatch.child(Constant.MODE).value.toString() == this@SportChallengeRepository.mode
                            && openMatch.child(Constant.OPTION).value.toString() == option
                        ) {

                            matchKey = openMatch.child(Constant.KEY).value.toString()
                            break
                        }
                    }

                    if (matchKey.isNotEmpty()) {

                        joinMatch(matchKey)
                    } else {

                        createNewMatch()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    //join a match
    private fun joinMatch(openMatchRefKey: String) {

        matchRef = sportRef.child(openMatchRefKey)

        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val nickname = snapshot.child(Constant.NICKNAME).value.toString()
                matchRef.child(Constant.PLAYER2).child(Constant.NICKNAME).setValue(nickname)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        matchRef.child(Constant.PLAYER2).child(Constant.ID).setValue(currentUser.uid)
        matchRef.child(Constant.PLAYER2).child(Constant.STEPS).setValue(0)
        matchRef.child(Constant.PLAYER2).child(Constant.METERS).setValue(0)


        //set match ref to user
        currentUserRef.child(Constant.SPORT_CHALLENGE).setValue(matchRef.key)

        //remove open match and user from the Q
        sportRef.child(Constant.OPENMATCHES).child(openMatchRefKey).removeValue()

        Log.d("SportRepo", "join Match: $matchRef")

        callbackEnemy.invoke(Constant.PLAYER1)
    }

    //create new Match
    private fun createNewMatch() {

        matchRef = sportRef.push()

        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val nickname = snapshot.child(Constant.NICKNAME).value.toString()
                matchRef.child(Constant.PLAYER1).child(Constant.NICKNAME).setValue(nickname)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        matchRef.child(Constant.PLAYER1).child(Constant.ID).setValue(currentUser.uid)
        matchRef.child(Constant.PLAYER1).child(Constant.STEPS).setValue(0)
        matchRef.child(Constant.PLAYER1).child(Constant.METERS).setValue(0)

        //set mode and option
        matchRef.child(Constant.MODE).setValue(mode)
        matchRef.child(Constant.OPTION).setValue(option)

        //create open matches
        val openMatchRef = sportRef.child(Constant.OPENMATCHES).child(matchRef.key.toString())
        openMatchRef.child(Constant.KEY).setValue(matchRef.key)
        openMatchRef.child(Constant.MODE).setValue(mode)
        openMatchRef.child(Constant.OPTION).setValue(option)

        //set match ref to user
        currentUserRef.child(Constant.SPORT_CHALLENGE).setValue(matchRef.key)

        Log.d("SportRepo", "created Match: $matchRef")

        waitForPlayer2()
    }

    private fun waitForPlayer2() {

        //show Waiting for Player

        matchRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.hasChild(Constant.PLAYER2)) {

                    matchRef.removeEventListener(this)
                    callbackEnemy.invoke(Constant.PLAYER2)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun createStepListener(enemy: String, callback: (result: Pair<String, String>) -> Unit) {

        matchRef.child(enemy).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val enemyStats = Pair(
                    snapshot.child(Constant.STEPS).value.toString(),
                    snapshot.child(Constant.METERS).value.toString()
                )

                Log.d("SportRepo", "listen $enemyStats")
                callback.invoke(enemyStats)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}