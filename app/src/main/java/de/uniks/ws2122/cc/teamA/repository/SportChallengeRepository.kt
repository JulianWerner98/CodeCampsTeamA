package de.uniks.ws2122.cc.teamA.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.model.MatchResult

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

    /** start match making +*/
    fun startMatchMaking(
        mode: String,
        option: String,
        callback: (enemy: String, user: String) -> Unit
    ) {

        this.callbackEnemy = callback
        this.mode = mode
        this.option = option

        //when mode is empty a match exists
        if (mode.isEmpty()) {

            loadRunningGame()
        } else {

            searchMatch()
        }
    }

    /** looks if the user already has a game running **/
    fun hasRunningGame(callback: (hasGame: Boolean) -> Unit) {

        //checks if a game is stored in the database for the user
        rootRef.child(Constant.USERS_PATH)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val hasGame = snapshot.child(currentUser.uid).hasChild(Constant.SPORT_CHALLENGE)
                    Log.d("SPORTRepo", "hasGame: $hasGame")
                    callback.invoke(hasGame)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("SportRepo", "hasRunningGame cancelled")
                }
            })
    }

    /** fetches the match reference in the database **/
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
                    Log.d("SportRepo", "determineSportChallengeReference cancelled")
                }
            })
    }

    /** load running game **/
    private fun loadRunningGame() {

        // get match reference
        determineSportChallengeReference() { foundRef ->

            if (foundRef) {

                matchRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        //is there an opponent?
                        if (snapshot.hasChild(Constant.PLAYER2)) {

                            //who is player 2?
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
                        Log.d("SportRepo",  "loadRunningGame cancelled")
                    }
                })
            }
        }

        Log.d("SPORTRepo", "loaded game")
    }

    /** looks for match if not it creates one **/
    private fun searchMatch() {

        sportRef.child(Constant.OPENMATCHES).orderByKey()
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val openMatches = snapshot.children.iterator()
                    var matchKey = ""

                    //check the open match list
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

                    //open match found?
                    if (matchKey.isNotEmpty()) {

                        joinMatch(matchKey)
                    } else {

                        createNewMatch()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("SportRepo",  "searchMatch cancelled")
                }
            })
    }

    /** join a match **/
    private fun joinMatch(openMatchRefKey: String) {

        matchRef = sportRef.child(openMatchRefKey)

        //get nickname
        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val nickname = snapshot.child(Constant.NICKNAME).value.toString()
                matchRef.child(Constant.PLAYER2).child(Constant.NICKNAME).setValue(nickname)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("SportRepo",  "joinMatch cancelled")
            }
        })

        //set user as player 2
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

    /** create new Match **/
    private fun createNewMatch() {

        matchRef = sportRef.push()

        //get nickname
        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val nickname = snapshot.child(Constant.NICKNAME).value.toString()
                matchRef.child(Constant.PLAYER1).child(Constant.NICKNAME).setValue(nickname)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("SportRepo",  "createNewMatch cancelled")
            }
        })

        //set user as player 1
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

    /** wait for second player **/
    private fun waitForPlayer2() {

        waitForPlayerListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.hasChild(Constant.PLAYER2)) {

                    matchRef.removeEventListener(this)
                    callbackEnemy.invoke(Constant.PLAYER2, Constant.PLAYER1)
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Log.d("SportRepo",  "waitForPlayer2 cancelled")
            }
        }

        matchRef.addValueEventListener(waitForPlayerListener)
    }

    /** create listener on the opponent steps in the database **/
    fun createStepListener(enemy: String, callback: (steps: Int, meters: Float) -> Unit) {

        stepListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    val steps = snapshot.child(Constant.STEPS).value.toString().toInt()
                    val meters = snapshot.child(Constant.METERS).value.toString().toFloat()

                    Log.d("SportRepo", "Steps: $steps")
                    //returns the steps and meters of the opponent when changed
                    callback.invoke(steps, meters)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("SportRepo",  "createStepListener cancelled")
            }
        }

        matchRef.child(enemy).addValueEventListener(stepListener)
    }

    /** send user steps and meters to the database **/
    fun sendData(user: String, steps: Int, meters: Float) {

        matchRef.child(user).child(Constant.STEPS).setValue(steps)
        matchRef.child(user).child(Constant.METERS).setValue(meters)
    }

    /** save the time of the user in the database **/
    fun saveTime(countedTime: Double, user: String) {

        matchRef.child(user).child(Constant.COUNTED_TIME).setValue(countedTime)
        matchRef.child(user).child(Constant.SYSTEM_TIME).setValue(System.currentTimeMillis())
    }

    /** load the time of the user from the database **/
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
                Log.d("SportRepo",  "loadTime cancelled")
            }
        })
    }

    /** get mode and option from a game **/
    fun getModeAndOption(callback: (mode: String, option: String) -> Unit) {

        //get match reference
        rootRef.child(Constant.USERS_PATH)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val sportRefKey = snapshot.child(currentUser.uid)
                        .child(Constant.SPORT_CHALLENGE).value.toString()
                    matchRef = sportRef.child(sportRefKey)

                    //get mode and option
                    matchRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            var mode = snapshot.child(Constant.MODE).value.toString()
                            var option = snapshot.child(Constant.OPTION).value.toString()

                            callback.invoke(mode, option)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("SportRepo",  "getModeAndOption mode/option cancelled")
                        }

                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("SportRepo",  "getModeAndOption matchref cancelled")
                }
            })
    }

    /** send final result from user **/
    fun sendResult(user: String, countedTime: Double, steps: Int, meters: Float) {

        sendData(user, steps, meters)
        matchRef.child(user).child(Constant.COUNTED_TIME).setValue(countedTime)
        matchRef.child(user).child(Constant.FINISHED).setValue(Constant.FINISHED)
    }

    /** get final result from opponent **/
    fun getEnemyResults(
        enemy: String,
        callback: (time: Double, steps: Int, meters: Float) -> Unit
    ) {

        matchRef.child(enemy).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                //opponent finished?
                if (snapshot.hasChild(Constant.FINISHED)) {

                    //opponent surrendered?
                    if (snapshot.child(Constant.FINISHED).value.toString() == Constant.SURRENDER) {

                        callback.invoke(0.0, 0, 0.0f)
                    } else {

                        val time = snapshot.child(Constant.COUNTED_TIME).value.toString().toDouble()
                        val steps = snapshot.child(Constant.STEPS).value.toString().toInt()
                        val meters = snapshot.child(Constant.METERS).value.toString().toFloat()

                        callback.invoke(time, steps, meters)
                    }

                    //remove listener
                    matchRef.child(enemy).removeEventListener(this)
                    matchRef.child(enemy).removeEventListener(stepListener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("SportRepo",  "getEnemyResults cancelled")
            }
        })
    }

    /** deletes the game from the database **/
    fun deleteMatch(enemy: String) {

        matchRef.child(enemy).removeEventListener(stepListener)
        currentUserRef.child(Constant.SPORT_CHALLENGE).removeValue()
        matchRef.removeValue()
    }

    /** removes the game from the match search **/
    fun cancelMatch() {

        matchRef.removeEventListener(waitForPlayerListener)
        matchRef.removeValue()
        currentUserRef.child(Constant.SPORT_CHALLENGE).removeValue()
        sportRef.child(Constant.OPENMATCHES).child(matchRef.key.toString()).removeValue()
    }

    /** surrender match **/
    fun surrenderMatch(user: String) {

        matchRef.child(user).child(Constant.FINISHED).setValue(Constant.SURRENDER)
        currentUserRef.child(Constant.SPORT_CHALLENGE).removeValue()
    }

    /** get opponent name **/
    fun getEnemyName(enemy: String, callback: (name: String) -> Unit) {

        matchRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val name = snapshot.child(enemy).child(Constant.NICKNAME).value.toString()
                callback.invoke(name)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("SportRepo",  "getEnemyName cancelled")
            }
        })
    }

    /** save match results for the match history **/
    fun saveMatchResult(matchResult: MatchResult) {

        rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.STATISTIC)
            .child(Constant.HISTORIE).child(matchRef.key.toString()).setValue(matchResult)
    }
}