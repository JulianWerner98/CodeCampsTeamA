package de.uniks.ws2122.cc.teamA.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.model.TicTacToe
import java.util.*

class TicTacToeRepository {

    //Constants
    private val TTTQ = "TicTacToeQ"
    private val GAMES = "Games"
    private val TTT = "TicTacToe"
    private val TTTFIELD = "Field"
    private val LASTTURN = "lastTurn"
    private val INGAME = "inGame"
    private val OPENMATCHES = "openMatches"
    private val PLAYER1 = "Player1"
    private val Player2 = "Player2"

    val BLANKFIELD = "_________"

    //References
    private val rootRef: DatabaseReference =
        FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference
    private var gamesRef: DatabaseReference
    private var tttQRef: DatabaseReference
    private var tttRef: DatabaseReference
    private lateinit var matchRef: DatabaseReference
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private val currentUserRef = rootRef.child(Constant.USERS_PATH).child(currentUser.uid).ref


    //TicTacToeData
    private var ticTacToeData: MutableLiveData<TicTacToe> = MutableLiveData()

    init {

        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (!snapshot.hasChild(TTTQ)) {

                    rootRef.child(GAMES).child(TTTQ)
                }
            }

            override fun onCancelled(error: DatabaseError) {

                TODO("Not yet implemented")
            }
        })

        gamesRef = rootRef.child(GAMES).ref
        tttQRef = gamesRef.child(TTTQ).ref
        tttRef = gamesRef.child(TTT).ref

        Log.d("TTTRepo", "Init: $tttQRef \n $tttRef")
    }

    fun startMatchMaking() {

        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (hasRunningGame(snapshot)) {

                    determineTicTacToeReference(snapshot)
                    loadRunningGame()

                } else {
                    if (!isInQueue(snapshot)) {

                        joinQueue()
                        waitingInQueue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //check if the user is already in the Queue
    private fun isInQueue(snapshot: DataSnapshot): Boolean {

        return snapshot.child(GAMES).child(TTTQ).hasChild(currentUser.uid)
    }

    //check if the user has a game running and
    private fun hasRunningGame(snapshot: DataSnapshot): Boolean {

        return snapshot.child(Constant.USERS_PATH).child(currentUser.uid).hasChild(INGAME)
    }

    private fun determineTicTacToeReference(snapshot: DataSnapshot) {

        val tttRefKey = snapshot.child(Constant.USERS_PATH).child(currentUser.uid)
            .child(INGAME).value.toString()
        matchRef = tttRef.child(tttRefKey)
    }

    //load the running game
    private fun loadRunningGame() {

        createTicTacToeDataChangesListener()
        Log.d("TTTRepo", "loaded game")
    }

    //add the User in the Queue
    private fun joinQueue() {

        tttQRef.child(currentUser.uid).setValue(System.currentTimeMillis())
        tttQRef.child(currentUser.uid).push()

        Log.d("TTTRepo", "joined Queue")
    }

    //waits till user is the first player in the Queue
    private fun waitingInQueue() {

        tttQRef.orderByKey().limitToFirst(1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (isFirstInQueue(snapshot)) {

                    searchMatch()
                    tttQRef.removeEventListener(this)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //check if the current user is the longest waiting user
    private fun isFirstInQueue(snapshot: DataSnapshot): Boolean {

        val firstUserRef: DatabaseReference = snapshot.children.iterator().next().ref
        Log.d("TTTRepo", "First user in Q: $firstUserRef")

        return firstUserRef == tttQRef.child(currentUser.uid).ref
    }

    //looks for match if not it creates one
    private fun searchMatch() {

        tttRef.child(OPENMATCHES).orderByKey().limitToFirst(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val iterator = snapshot.children.iterator()

                    if (iterator.hasNext()) {

                        val openMatchRefKey = snapshot.children.iterator().next().value.toString()
                        joinMatch(openMatchRefKey)
                    }
                    else {

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

        matchRef = tttRef.child(openMatchRefKey)
        matchRef.child(Player2).setValue(currentUser.uid)

        //set match ref to user
        currentUserRef.child("inGame").setValue(matchRef.key)

        //remove open match and user from the Q
        tttRef.child(OPENMATCHES).child(openMatchRefKey).removeValue()
        tttQRef.child(currentUser.uid).removeValue()

        firstTurn()
        Log.d("TTTRepo", "join Match: $matchRef")

        createTicTacToeDataChangesListener()
    }

    //Randomly chooses who gets to make the first move
    private fun firstTurn() {

        if (Random().nextBoolean()) {

            matchRef.child(LASTTURN).setValue(currentUser.uid)
        }
    }

    //create new Match
    private fun createNewMatch() {

        matchRef = tttRef.push()
        matchRef.child(PLAYER1).setValue(currentUser.uid)
        matchRef.child(TTTFIELD).setValue(BLANKFIELD)
        matchRef.child(LASTTURN).setValue(currentUser.uid)

        //set match ref to open matches and to user
        tttRef.child(OPENMATCHES).child(matchRef.key.toString()).setValue(matchRef.key)
        currentUserRef.child("inGame").setValue(matchRef.key)

        //remove user from the Q
        tttQRef.child(currentUser.uid).removeValue()

        Log.d("TTTRepo", "created Match: $matchRef")

        createTicTacToeDataChangesListener()
    }

    //add TicTacToe listener to get data changes
    private fun createTicTacToeDataChangesListener() {

        matchRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val ticTacToe = TicTacToe()
                ticTacToe.fields = snapshot.child(TTTFIELD).value.toString()

                if (snapshot.child(PLAYER1).value.toString() == currentUser.uid) {

                    ticTacToe.isCircle = true
                }

                if (snapshot.child(LASTTURN).value.toString() != currentUser.uid) {

                    ticTacToe.isMyTurn = true
                }

                Log.d("TTTRepo", "data listener: ${ticTacToe.fields}")
                ticTacToeData.value = ticTacToe
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //
    fun sendTurn(index: Int, icon: Char) {

        matchRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var field = snapshot.child(TTTFIELD).value.toString()
                field = field.substring(0, index) + icon + field.substring(index + 1)
                matchRef.child(TTTFIELD).setValue(field)
                matchRef.child(LASTTURN).setValue(currentUser.uid)

                Log.d("TTTRepo", "sent turn")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getTicTacToeData(): MutableLiveData<TicTacToe> {

        return ticTacToeData
    }

    //TODO("Win Game, Leave Game, Delete Game")
}
