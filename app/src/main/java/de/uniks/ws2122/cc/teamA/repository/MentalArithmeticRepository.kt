package de.uniks.ws2122.cc.teamA.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.model.MentalArithmetic

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

    fun lookForGame(arithmeticTasks: MutableList<String>, arithmeticAnswers: MutableList<String>) {
        maRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()){
                    snapshot.children.forEach {
                        if (it.hasChild(Constant.MENTALARITHMETICQUEUE)){
                            val matchId = it.key
                            rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.MENTALARITHMETIC).setValue(matchId.toString())
                            maRef.child(matchId.toString()).child(currentUser.uid).setValue("")
                            maRef.child(matchId.toString()).child(Constant.READY).child(currentUser.uid).setValue(false)
                            maRef.child(matchId.toString()).child(Constant.MENTALARITHMETICQUEUE).removeValue()
                            return
                        }
                    }
                    createNewGame(arithmeticTasks, arithmeticAnswers)
                }
                createNewGame(arithmeticTasks, arithmeticAnswers)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun createNewGame(arithmeticTasks: MutableList<String>, arithmeticAnswers: MutableList<String>) {
        maRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val gameKey = maRef.push().key
                maRef.child(gameKey.toString()).child(Constant.ARITHMETICTASKS).setValue(arithmeticTasks)
                maRef.child(gameKey.toString()).child(Constant.ARITHMETICANSWERS).setValue(arithmeticAnswers)
                maRef.child(gameKey.toString()).child(currentUser.uid).setValue("")
                maRef.child(gameKey.toString()).child(Constant.MENTALARITHMETICQUEUE).setValue(currentUser.uid)
                maRef.child(gameKey.toString()).child(Constant.READY).child(currentUser.uid).setValue(false)
                rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.MENTALARITHMETIC).setValue(gameKey.toString())
                listenerOnReady(gameKey.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun listenerOnReady(gameKey: String) {
        maRef.child(gameKey).child(Constant.READY).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var counter = 0
                snapshot.children.forEach {
                    if (it.value as Boolean){
                        counter += 1
                    }
                }
                if (counter == 2){
                    startGame()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun startGame() {
        TODO("Not yet implemented")
    }

}