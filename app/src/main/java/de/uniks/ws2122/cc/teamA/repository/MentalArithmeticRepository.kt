package de.uniks.ws2122.cc.teamA.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.model.Highscore
import de.uniks.ws2122.cc.teamA.model.MatchResult
import de.uniks.ws2122.cc.teamA.model.Notification

class MentalArithmeticRepository {
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private var currentUserName = String()
    private var opponentId = String()

    // Database References
    private val rootRef: DatabaseReference =
        FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference
    private var maRef : DatabaseReference
    private var currentUserRef : DatabaseReference

    init {
        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                currentUserName = snapshot.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.NICKNAME).value as String

                if (!snapshot.child(Constant.GAMES).hasChild(Constant.MENTALARITHMETIC)) {

                    rootRef.child(Constant.GAMES).child(Constant.MENTALARITHMETIC)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        maRef = rootRef.child(Constant.GAMES).child(Constant.MENTALARITHMETIC).ref
        currentUserRef = rootRef.child(Constant.USERS_PATH).child(currentUser.uid).ref
    }

    // ---- MentalArithmetic ViewModel ----
    fun lookForGame(
        arithmeticTasks: MutableList<String>,
        arithmeticAnswers: MutableList<String>,
        matchTyp: String,
        inviteKey: String,
        friendId: String,
        callback: (result: String) -> Unit
    ) {
        if (matchTyp == Constant.DEFAULT) {
            // Look for a game if you find one join else create a new game
            maRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()){
                        snapshot.children.forEach {
                            if (it.hasChild(Constant.MENTALARITHMETICQUEUE)) {
                                val matchId = it.key
                                callback.invoke(matchId.toString())
                                rootRef.child(Constant.USERS_PATH).child(currentUser.uid)
                                    .child(Constant.MENTALARITHMETIC).setValue(matchId.toString())
                                maRef.child(matchId.toString()).child(Constant.PLAYERS).child(currentUser.uid).setValue("")
                                maRef.child(matchId.toString()).child(Constant.READY)
                                    .child(currentUser.uid).setValue(false)
                                maRef.child(matchId.toString()).child(Constant.MENTALARITHMETICQUEUE)
                                    .removeValue()
                                return
                            }
                        }
                    }
                    createNewGame(arithmeticTasks, arithmeticAnswers) { key ->
                        callback.invoke(key)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        } else {
            if (inviteKey == Constant.DEFAULT){
                // You have invited someone and create a new private game
                maRef.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        createNewPrivateGame(arithmeticTasks, arithmeticAnswers, friendId) { key ->
                            callback.invoke(key)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
            } else {
                // Join a private game with your invite key
                maRef.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        callback.invoke(inviteKey)
                        rootRef.child(Constant.USERS_PATH).child(currentUser.uid)
                            .child(Constant.MENTALARITHMETIC).setValue(inviteKey)
                        maRef.child(inviteKey).child(Constant.PLAYERS).child(currentUser.uid).setValue("")
                        maRef.child(inviteKey).child(Constant.READY)
                            .child(currentUser.uid).setValue(false)
                        maRef.child(inviteKey).child(Constant.MENTALARITHMETICPRIVATEQUEUE)
                            .removeValue()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
            }
        }
    }

    private fun createNewPrivateGame(
        arithmeticTasks: MutableList<String>,
        arithmeticAnswers: MutableList<String>,
        friendId: String,
        callback: (result: String) -> Unit
    ) {
        maRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val gameKey = maRef.push().key
                callback.invoke(gameKey.toString())
                maRef.child(gameKey.toString()).child(Constant.ARITHMETICTASKS).setValue(arithmeticTasks)
                maRef.child(gameKey.toString()).child(Constant.ARITHMETICANSWERS).setValue(arithmeticAnswers)
                maRef.child(gameKey.toString()).child(Constant.PLAYERS).child(currentUser.uid).setValue("")
                maRef.child(gameKey.toString()).child(Constant.MENTALARITHMETICPRIVATEQUEUE).setValue(currentUser.uid)
                maRef.child(gameKey.toString()).child(Constant.READY).child(currentUser.uid).setValue(false)
                rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.MENTALARITHMETIC).setValue(gameKey.toString())
                rootRef.child(Constant.USERS_PATH).child(friendId).child(Constant.INVITES).child(Constant.MENTALARITHMETIC).child(currentUserName).setValue(gameKey.toString())

                // Write in database to send a notification to opponent that you have him invite to a game
                val notficationId =  gameKey
                val notification = Notification(notficationId.toString(), currentUserName)
                rootRef.child(Constant.NOTIFICATION).child(Constant.NOTIFICATIONARITHMETIC).child(friendId).child(notficationId.toString()).setValue(notification)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun createNewGame(arithmeticTasks: MutableList<String>, arithmeticAnswers: MutableList<String>, callback: (result: String) -> Unit) {
        maRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val gameKey = maRef.push().key
                callback.invoke(gameKey.toString())
                maRef.child(gameKey.toString()).child(Constant.ARITHMETICTASKS).setValue(arithmeticTasks)
                maRef.child(gameKey.toString()).child(Constant.ARITHMETICANSWERS).setValue(arithmeticAnswers)
                maRef.child(gameKey.toString()).child(Constant.PLAYERS).child(currentUser.uid).setValue("")
                maRef.child(gameKey.toString()).child(Constant.MENTALARITHMETICQUEUE).setValue(currentUser.uid)
                maRef.child(gameKey.toString()).child(Constant.READY).child(currentUser.uid).setValue(false)
                rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.MENTALARITHMETIC).setValue(gameKey.toString())
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    // Get the current game key
    fun fetchGameKey(callback: (result: String) -> Unit){
        rootRef.child(Constant.USERS_PATH).child(currentUser.uid)
            .child(Constant.MENTALARITHMETIC)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback.invoke(snapshot.value.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    // Check if both players are ready to start the game
    fun readyUpToStartGame(gameKey: String, callback: (result: Boolean) -> Unit) {
        maRef.child(gameKey).child(Constant.READY).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var counter = 0
                snapshot.children.forEach {
                    if (it.key.toString() == currentUser.uid){
                        maRef.child(gameKey).child(Constant.READY).child(currentUser.uid).setValue(true)
                        counter += 1
                    } else {
                        opponentId = it.key.toString()
                        if (it.value as Boolean){
                            counter += 1
                        }
                    }
                }
                if (counter == 2){
                    callback.invoke(true)
                    maRef.child(gameKey).child(Constant.READY).removeEventListener(this)
                } else {
                    callback.invoke(false)
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    // Get a list of tasks from database
    fun fetchArithmeticTasks(gameKey: String, callback: (result: ArrayList<String>) -> Unit) {
        maRef.child(gameKey).child(Constant.ARITHMETICTASKS).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasks = arrayListOf<String>()
                snapshot.children.forEach {
                    tasks.add(it.value.toString())
                }
                callback.invoke(tasks)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    // Get a list of answers from database
    fun fetchArithmeticAnswers(gameKey: String, callback: (result: ArrayList<String>) -> Unit) {
        maRef.child(gameKey).child(Constant.ARITHMETICANSWERS).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val answers = arrayListOf<String>()
                snapshot.children.forEach {
                    answers.add(it.value.toString())
                }
                callback.invoke(answers)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    // Send your answers to database
    fun sendTaskAnswer(taskAnswer: Boolean, gameKey: String, taskNumber: String) {
        maRef.child(gameKey).child(Constant.PLAYERS).child(currentUser.uid).child(taskNumber).setValue(taskAnswer)
    }

    // Write your result in database and wait for other player
    // If both are finished change to result activity
    fun goToResultActivity(
        gameKey: String,
        currentUserAnswers: MutableList<Boolean>,
        time: String, callback: (result: Boolean) -> Unit
    ) {
        maRef.child(gameKey).child(Constant.FINISHED).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount.toInt() == 2){
                    callback.invoke(true)
                    maRef.child(gameKey).child(Constant.FINISHED).removeEventListener(this)
                } else {
                    maRef.child(gameKey).child(Constant.FINISHED).child(currentUser.uid).child(Constant.GAMEFINISHEDANSWERS).setValue(currentUserAnswers)
                    maRef.child(gameKey).child(Constant.FINISHED).child(currentUser.uid).child(Constant.FINISHEDTIME).setValue(time)
                    maRef.child(gameKey).child(Constant.RESULT).child(currentUser.uid).child(Constant.NICKNAME).setValue(currentUserName)
                    // Write false so that you don't delete your game if you changed to result activity
                    maRef.child(gameKey).child(Constant.DELETEGAME).child(currentUser.uid).setValue(false)
                    callback.invoke(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    // When called OnDestroyed: If both player leave the game then it will deleted the game from database
    // If it's a private game the invite will get delete if the game doesn't had started yet
    fun destroyDefaultGame(gameKey: String) {
        maRef.child(gameKey).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(Constant.MENTALARITHMETICQUEUE).exists()){
                    maRef.child(gameKey).removeValue()
                } else if (!snapshot.child(Constant.DELETEGAME).child(currentUser.uid).exists()){
                    val currentUserAnswers = mutableListOf<Boolean>()
                    currentUserAnswers.add(false)
                    val time = "99:54"
                    maRef.child(gameKey).child(Constant.FINISHED).child(currentUser.uid).child(Constant.GAMEFINISHEDANSWERS).setValue(currentUserAnswers)
                    maRef.child(gameKey).child(Constant.FINISHED).child(currentUser.uid).child(Constant.FINISHEDTIME).setValue(time)
                    maRef.child(gameKey).child(Constant.DELETEGAME).child(currentUser.uid).setValue(true)
                    maRef.child(gameKey).child(Constant.READY).child(currentUser.uid).setValue(true)

                    snapshot.child(Constant.DELETEGAME).children.forEach{
                        if (it.key.toString() != currentUser.uid){
                            if (it.value as Boolean){
                                // Delete game if both player have given up
                                maRef.child(gameKey).removeValue()
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


    fun destroyPrivateGame(gameKey: String, friendId: String) {
        maRef.child(gameKey).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(Constant.MENTALARITHMETICPRIVATEQUEUE).exists()){
                    maRef.child(gameKey).removeValue()
                    rootRef.child(Constant.USERS_PATH).child(friendId).child(Constant.INVITES).child(Constant.MENTALARITHMETIC).child(currentUserName).removeValue()
                } else if (!snapshot.child(Constant.DELETEGAME).child(currentUser.uid).exists()){
                    val currentUserAnswers = mutableListOf<Boolean>()
                    currentUserAnswers.add(false)
                    val time = "99:54"
                    maRef.child(gameKey).child(Constant.FINISHED).child(currentUser.uid).child(Constant.GAMEFINISHEDANSWERS).setValue(currentUserAnswers)
                    maRef.child(gameKey).child(Constant.FINISHED).child(currentUser.uid).child(Constant.FINISHEDTIME).setValue(time)
                    maRef.child(gameKey).child(Constant.DELETEGAME).child(currentUser.uid).setValue(true)
                    maRef.child(gameKey).child(Constant.READY).child(currentUser.uid).setValue(true)

                    snapshot.child(Constant.DELETEGAME).children.forEach{
                        if (it.key.toString() != currentUser.uid){
                            if (it.value as Boolean){
                                // Delete game if both player have given up
                                maRef.child(gameKey).removeValue()
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    // ---- Result ViewModel ----
    // Get your answers from database
    fun fetchCurrentUserAnswers(gameKey: String, callback: (result: MutableList<Boolean>) -> Unit) {
        maRef.child(gameKey).child(Constant.FINISHED).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentAnswers = mutableListOf<Boolean>()
                snapshot.children.forEach {
                    if (it.key.toString() == currentUser.uid){
                        it.child(Constant.GAMEFINISHEDANSWERS).children.forEach { answer ->
                            currentAnswers.add(answer.value as Boolean)
                        }
                    callback.invoke(currentAnswers)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    // Get your opponents answers from database
    fun fetchOpponentAnswers(gameKey: String, callback: (result: MutableList<Boolean>) -> Unit) {
        maRef.child(gameKey).child(Constant.FINISHED).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentAnswers = mutableListOf<Boolean>()
                snapshot.children.forEach {
                    if (it.key.toString() != currentUser.uid){
                        it.child(Constant.GAMEFINISHEDANSWERS).children.forEach { answer ->
                            currentAnswers.add(answer.value as Boolean)
                        }
                    callback.invoke(currentAnswers)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    // Get your used time from database
    fun fetchTime(gameKey: String, callback: (result: List<String>) -> Unit) {
        maRef.child(gameKey).child(Constant.FINISHED).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val timeList = arrayListOf<String>()
                timeList.add(snapshot.child(currentUser.uid).child(Constant.FINISHEDTIME).value.toString())
                snapshot.children.forEach {
                    if (it.key.toString() != currentUser.uid){
                        timeList.add(it.child(Constant.FINISHEDTIME).value.toString())
                    }
                }
                callback.invoke(timeList)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    // Set in your historie that you have won
    fun setMentalArithmeticWin(currentUserCorrectAnswers: Int, gameKey: String) {
        fetchOpponentName(gameKey) { name ->
            val points = 100 * currentUserCorrectAnswers
            val matchResult = MatchResult(Constant.WIN, "You", name, Constant.MENTALARITHMETIC, points)
            currentUserRef.child(Constant.STATISTIC).child(Constant.HISTORIE).child(gameKey).setValue(matchResult).addOnSuccessListener {
                // Remove game key from user in database
                currentUserRef.child(Constant.MENTALARITHMETIC).removeValue()
                // Write highscore and database and check if the current points are higher then the highscore
                // when yes, then update it
                rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.STATISTIC)
                    .child(Constant.MENTALARITHMETIC).get().addOnSuccessListener { dataSnapshot ->
                        var highscore =
                            dataSnapshot.getValue(de.uniks.ws2122.cc.teamA.model.Highscore::class.java)
                        if (highscore == null) highscore = Highscore()
                        if (highscore.points < matchResult.points) highscore.points =
                            matchResult.points
                        highscore.wins += 1
                        rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.STATISTIC)
                            .child(Constant.MENTALARITHMETIC).setValue(highscore)
                    }
            }
        }
    }

    private fun fetchOpponentName(gameKey: String, callback: (result: String) -> Unit) {
        maRef.child(gameKey).child(Constant.RESULT).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    if (it.key != currentUser.uid){
                        val name = it.child(Constant.NICKNAME).value
                        callback.invoke(name as String)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    // Set in your historie that you have lost
    fun setMentalArithmeticLose(gameKey: String) {
        fetchOpponentName(gameKey) { name ->
            val matchResult =
                MatchResult(Constant.LOSE, "You", name, Constant.MENTALARITHMETIC, 0)
            currentUserRef.child(Constant.STATISTIC).child(Constant.HISTORIE).child(gameKey)
                .setValue(matchResult).addOnSuccessListener {
                // Remove game key from user in database
                currentUserRef.child(Constant.MENTALARITHMETIC).removeValue()
                // Write highscore and database and check if the current points are higher then the highscore
                // when yes, then update it
                rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.STATISTIC)
                    .child(Constant.MENTALARITHMETIC).get().addOnSuccessListener { dataSnapshot ->
                        var highscore =
                            dataSnapshot.getValue(de.uniks.ws2122.cc.teamA.model.Highscore::class.java)
                        if (highscore == null) highscore = Highscore()
                        if (highscore.points < matchResult.points) highscore.points =
                            matchResult.points
                        highscore.loses += 1
                        rootRef.child(Constant.USERS_PATH).child(currentUser.uid)
                            .child(Constant.STATISTIC)
                            .child(Constant.MENTALARITHMETIC).setValue(highscore)
                    }
            }
        }
    }

    // Set in your historie that it is a draw
    fun setMentalArithmeticDraw(gameKey: String) {
        // Set your draw in match historie
        fetchOpponentName(gameKey) { name ->
            val matchResult =
                MatchResult(Constant.DRAW, "You", name, Constant.MENTALARITHMETIC, 0)
            currentUserRef.child(Constant.STATISTIC).child(Constant.HISTORIE).child(gameKey)
                .setValue(matchResult).addOnSuccessListener {
                    // Remove game key from user in database
                    currentUserRef.child(Constant.MENTALARITHMETIC).removeValue()
                    // Write highscore and database and check if the current points are higher then the highscore
                    // when yes, then update it
                    rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.STATISTIC)
                        .child(Constant.MENTALARITHMETIC).get().addOnSuccessListener { dataSnapshot ->
                            var highscore =
                                dataSnapshot.getValue(de.uniks.ws2122.cc.teamA.model.Highscore::class.java)
                            if (highscore == null) highscore = Highscore()
                            if (highscore.points < matchResult.points) highscore.points =
                                matchResult.points
                            highscore.draws += 1
                            rootRef.child(Constant.USERS_PATH).child(currentUser.uid)
                                .child(Constant.STATISTIC)
                                .child(Constant.MENTALARITHMETIC).setValue(highscore)
                        }
                }
        }
    }

    // Check if both player have finished the game and then delete it
    fun finishedGame(gameKey: String) {
        maRef.child(gameKey).child(Constant.DELETEGAME).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    var counter = 0
                    snapshot.children.forEach {
                        if (it.key.toString() == currentUser.uid){
                            maRef.child(gameKey).child(Constant.DELETEGAME).child(currentUser.uid).setValue(true)
                            counter += 1
                        } else if (it.value as Boolean){
                            counter += 1
                        }
                    }
                    if (counter == 2){
                        maRef.child(gameKey).removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

}