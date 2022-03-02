package de.uniks.ws2122.cc.teamA.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant

class MentalArithmeticRepository {
    private val currentUser = FirebaseAuth.getInstance().currentUser!!

    // Database References
    private val rootRef: DatabaseReference =
        FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference
    private var maRef : DatabaseReference
    private var currentUserRef : DatabaseReference

    init {
        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (!snapshot.child(Constant.GAMES).hasChild(Constant.MENTALARITHMETIC)) {

                    rootRef.child(Constant.GAMES).child(Constant.MENTALARITHMETIC)
                }
            }

            override fun onCancelled(error: DatabaseError) {

                TODO("Not yet implemented")
            }
        })

        maRef = rootRef.child(Constant.GAMES).child(Constant.MENTALARITHMETIC).ref
        currentUserRef = rootRef.child(Constant.USERS_PATH).child(currentUser.uid).ref
    }

    fun lookForGame(arithmeticTasks: MutableList<String>, arithmeticAnswers: MutableList<String>, callback: (result: String) -> Unit) {
        maRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()){
                    snapshot.children.forEach {
                        if (it.hasChild(Constant.MENTALARITHMETICQUEUE)) {
                            val matchId = it.key
                            callback.invoke(matchId.toString())
                            rootRef.child(Constant.USERS_PATH).child(currentUser.uid)
                                .child(Constant.MENTALARITHMETIC).setValue(matchId.toString())
                            maRef.child(matchId.toString()).child(currentUser.uid).setValue("")
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
                TODO("Not yet implemented")
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
                maRef.child(gameKey.toString()).child(currentUser.uid).setValue("")
                maRef.child(gameKey.toString()).child(Constant.MENTALARITHMETICQUEUE).setValue(currentUser.uid)
                maRef.child(gameKey.toString()).child(Constant.READY).child(currentUser.uid).setValue(false)
                rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.MENTALARITHMETIC).setValue(gameKey.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun fetchGameKey(callback: (result: String) -> Unit){
        rootRef.child(Constant.USERS_PATH).child(currentUser.uid)
            .child(Constant.MENTALARITHMETIC)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback.invoke(snapshot.value.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    fun readyUpToStartGame(gameKey: String, callback: (result: Boolean) -> Unit) {
        maRef.child(gameKey).child(Constant.READY).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var counter = 0
                snapshot.children.forEach {
                    if (it.key.toString() == currentUser.uid){
                        maRef.child(gameKey).child(Constant.READY).child(currentUser.uid).setValue(true)
                        counter += 1
                    } else {
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
                TODO("Not yet implemented")
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
                TODO("Not yet implemented")
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
                TODO("Not yet implemented")
            }

        })
    }

    fun sendTaskAnswer(taskAnswer: Boolean, gameKey: String, taskNumber: String) {
        maRef.child(gameKey).child(currentUser.uid).child(taskNumber).setValue(taskAnswer)
    }

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
                    callback.invoke(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    // Result ViewModel
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
                TODO("Not yet implemented")
            }

        })
    }

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
                TODO("Not yet implemented")
            }

        })
    }

    fun fetchTime(gameKey: String, callback: (result: String) -> Unit) {
        maRef.child(gameKey).child(Constant.FINISHED).child(currentUser.uid).child(Constant.FINISHEDTIME).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val time = snapshot.value.toString()
                callback.invoke(time)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


}