package de.uniks.ws2122.cc.teamA.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant

class SportChallengeRepository {

    private var mode: String = ""
    private var option: String = ""
    private lateinit var callbackEnemy: (enemy: String, user: String) -> Unit

    //References
    private val rootRef = FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference
    private var gamesRef = rootRef.child(Constant.GAMES).ref
    private var sportRef = gamesRef.child(Constant.SPORT_CHALLENGE).ref
    private lateinit var matchRef: DatabaseReference
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private val currentUserRef = rootRef.child(Constant.USERS_PATH).child(currentUser.uid).ref
    private lateinit var stepListener: ValueEventListener
    private lateinit var waitForPlayerListener: ValueEventListener

    fun startMatchMaking(
        mode: String,
        option: String,
        callback: (enemy: String, user: String) -> Unit
    ) {

        this.callbackEnemy = callback
        this.mode = mode
        this.option = option

        if (mode.isEmpty()) {

            loadRunningGame()
        } else {

            searchMatch()
        }
    }

    fun hasRunningGame(callback: (hasGame: Boolean) -> Unit) {

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

                matchRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.hasChild(Constant.PLAYER2)) {

                            if (snapshot.child(Constant.PLAYER1)
                                    .child(Constant.ID).value.toString() == currentUser.uid
                            ) {

                                callbackEnemy.invoke(Constant.PLAYER2, Constant.PLAYER1)
                            } else {

                                callbackEnemy.invoke(Constant.PLAYER1, Constant.PLAYER2)
                            }
                        } else {

                            waitForPlayer2()
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

        callbackEnemy.invoke(Constant.PLAYER1, Constant.PLAYER2)
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

       waitForPlayerListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.hasChild(Constant.PLAYER2)) {

                    matchRef.removeEventListener(this)
                    callbackEnemy.invoke(Constant.PLAYER2, Constant.PLAYER1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }

        matchRef.addValueEventListener(waitForPlayerListener)
    }

    fun createStepListener(enemy: String, callback: (steps: Int, meters: Float) -> Unit) {

        stepListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    val steps = snapshot.child(Constant.STEPS).value.toString().toInt()
                    val meters = snapshot.child(Constant.METERS).value.toString().toFloat()

                    Log.d("SportRepo", "Steps: $steps")
                    callback.invoke(steps, meters)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        matchRef.child(enemy).addValueEventListener(stepListener)
    }

    fun sendData(user: String, steps: Int, meters: Float) {

        matchRef.child(user).child(Constant.STEPS).setValue(steps)
        matchRef.child(user).child(Constant.METERS).setValue(meters)
    }

    fun saveTime(countedTime: Double, user: String) {

        matchRef.child(user).child(Constant.COUNTED_TIME).setValue(countedTime)
        matchRef.child(user).child(Constant.SYSTEM_TIME).setValue(System.currentTimeMillis())
    }

    fun loadTime(user: String, callback: (countedTime: Double, oldSystemTime: Long) -> Unit) {

        matchRef.child(user).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.child(Constant.COUNTED_TIME)
                        .exists() && snapshot.child(Constant.SYSTEM_TIME).exists()
                ) {

                    val countedTime =
                        snapshot.child(Constant.COUNTED_TIME).value.toString().toDouble()
                    val oldSystemTime =
                        snapshot.child(Constant.SYSTEM_TIME).value.toString().toLong()

                    callback.invoke(countedTime, oldSystemTime)
                    Log.d("SPORTRepo", "Time: $countedTime + $oldSystemTime")
                } else {

                    callback.invoke(0.0, 0)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getModeAndOption(callback: (mode: String, option: String) -> Unit) {

        rootRef.child(Constant.USERS_PATH)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val sportRefKey = snapshot.child(currentUser.uid)
                        .child(Constant.SPORT_CHALLENGE).value.toString()
                    matchRef = sportRef.child(sportRefKey)

                    matchRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            var mode = snapshot.child(Constant.MODE).value.toString()
                            var option = snapshot.child(Constant.OPTION).value.toString()

                            callback.invoke(mode, option)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    fun sendResult(user: String, countedTime: Double, steps: Int, meters: Float) {

        sendData(user, steps, meters)
        matchRef.child(user).child(Constant.COUNTED_TIME).setValue(countedTime)
        matchRef.child(user).child(Constant.FINISHED).setValue(Constant.FINISHED)
    }

    fun getEnemyResults(enemy: String, callback: (time: Double, steps: Int, meters: Float) -> Unit) {

        matchRef.child(enemy).addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.hasChild(Constant.FINISHED)){

                    if (snapshot.child(Constant.FINISHED).value.toString() == Constant.SURRENDER) {

                        callback.invoke(0.0, 0, 0.0f)
                    }
                    else {

                        val time = snapshot.child(Constant.COUNTED_TIME).value.toString().toDouble()
                        val steps = snapshot.child(Constant.STEPS).value.toString().toInt()
                        val meters = snapshot.child(Constant.METERS).value.toString().toFloat()

                        callback.invoke(time, steps, meters)
                    }

                    matchRef.child(enemy).removeEventListener(this)
                    matchRef.child(enemy).removeEventListener(stepListener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun deleteMatch(enemy: String) {

        matchRef.child(enemy).removeEventListener(stepListener)
        currentUserRef.child(Constant.SPORT_CHALLENGE).removeValue()
        matchRef.removeValue()
    }

    fun cancelMatch() {

        matchRef.removeEventListener(waitForPlayerListener)
        matchRef.removeValue()
        currentUserRef.child(Constant.SPORT_CHALLENGE).removeValue()
        sportRef.child(Constant.OPENMATCHES).child(matchRef.key.toString()).removeValue()
    }

    fun surrenderMatch(user: String) {

        matchRef.child(user).child(Constant.FINISHED).setValue(Constant.SURRENDER)
        currentUserRef.child(Constant.SPORT_CHALLENGE).removeValue()
    }
}