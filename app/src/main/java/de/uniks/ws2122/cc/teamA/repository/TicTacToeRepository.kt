package de.uniks.ws2122.cc.teamA.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.model.TicTacToe
import java.util.*

class TicTacToeRepository {

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

                if (!snapshot.hasChild(Constant.TTTQ)) {

                    rootRef.child(Constant.GAMES).child(Constant.TTTQ)
                }
            }

            override fun onCancelled(error: DatabaseError) {

                TODO("Not yet implemented")
            }
        })

        gamesRef = rootRef.child(Constant.GAMES).ref
        tttQRef = gamesRef.child(Constant.TTTQ).ref
        tttRef = gamesRef.child(Constant.TTT).ref

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

        return snapshot.child(Constant.GAMES).child(Constant.TTTQ).hasChild(currentUser.uid)
    }

    //check if the user has a game running and
    private fun hasRunningGame(snapshot: DataSnapshot): Boolean {

        var hasGame = false

        if (snapshot.child(Constant.USERS_PATH).child(currentUser.uid).hasChild(Constant.INGAME)) {

            val matchRefString = snapshot.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.INGAME).value.toString()
            hasGame = snapshot.child(Constant.GAMES).child(Constant.TTT).hasChild(matchRefString)
            Log.d("TTTRepo", "hasGame: $hasGame")
        }

        return hasGame
    }

    private fun determineTicTacToeReference(snapshot: DataSnapshot) {

        val tttRefKey = snapshot.child(Constant.USERS_PATH).child(currentUser.uid)
            .child(Constant.INGAME).value.toString()
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

        tttRef.child(Constant.OPENMATCHES).orderByKey().limitToFirst(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val iterator = snapshot.children.iterator()

                    if (iterator.hasNext()) {

                        val openMatchRefKey = snapshot.children.iterator().next().value.toString()
                        joinMatch(openMatchRefKey)
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

        matchRef = tttRef.child(openMatchRefKey)

        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                matchRef.child(Constant.PLAYER2).child(Constant.ID).setValue(currentUser.uid)
                val nickname = snapshot.child(Constant.NICKNAME).value.toString()
                matchRef.child(Constant.PLAYER2).child(Constant.NICKNAME).setValue(nickname)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //set match ref to user
        currentUserRef.child(Constant.INGAME).setValue(matchRef.key)

        //remove open match and user from the Q
        tttRef.child(Constant.OPENMATCHES).child(openMatchRefKey).removeValue()
        tttQRef.child(currentUser.uid).removeValue()

        firstTurn()
        Log.d("TTTRepo", "join Match: $matchRef")

        createTicTacToeDataChangesListener()
    }

    //Randomly chooses who gets to make the first move
    private fun firstTurn() {

        if (Random().nextBoolean()) {

            matchRef.child(Constant.LASTTURN).setValue(currentUser.uid)
        }
    }

    //create new Match
    private fun createNewMatch() {

        matchRef = tttRef.push()

        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                matchRef.child(Constant.PLAYER1).child(Constant.ID).setValue(currentUser.uid)
                val nickname = snapshot.child(Constant.NICKNAME).value.toString()
                matchRef.child(Constant.PLAYER1).child(Constant.NICKNAME).setValue(nickname)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        matchRef.child(Constant.TTTFIELD).setValue(Constant.BLANKFIELD)
        matchRef.child(Constant.LASTTURN).setValue(currentUser.uid)

        //set match ref to open matches and to user
        tttRef.child(Constant.OPENMATCHES).child(matchRef.key.toString()).setValue(matchRef.key)
        currentUserRef.child(Constant.INGAME).setValue(matchRef.key)

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
                ticTacToe.fields = snapshot.child(Constant.TTTFIELD).value.toString()

                if (snapshot.child(Constant.PLAYER1).child(Constant.ID).value.toString() == currentUser.uid) {

                    ticTacToe.isCircle = true
                }

                if (snapshot.child(Constant.LASTTURN).value.toString() != currentUser.uid) {

                    ticTacToe.isMyTurn = true
                }

                if (ticTacToe.players.size < 2 && snapshot.child(Constant.PLAYER2).child(Constant.ID).exists()) {

                    val nicknamePlayer1 = snapshot.child(Constant.PLAYER1).child(Constant.NICKNAME).value.toString()
                    val nicknamePlayer2 = snapshot.child(Constant.PLAYER2).child(Constant.NICKNAME).value.toString()

                    if (snapshot.child(Constant.PLAYER1).child(Constant.ID).value == currentUser.uid) {

                        ticTacToe.players.add(nicknamePlayer1)
                        ticTacToe.players.add(nicknamePlayer2)

                    } else {

                        ticTacToe.players.add(nicknamePlayer2)
                        ticTacToe.players.add(nicknamePlayer1)
                    }
                }

                if (snapshot.child(Constant.WINNER).exists()) {

                    if(snapshot.child(Constant.WINNER).value == Constant.DRAW) {

                       ticTacToe.winner = Constant.DRAW
                    } else {

                        if (snapshot.child(Constant.WINNER).value == snapshot.child(Constant.PLAYER1).child(Constant.ID).value) {

                            ticTacToe.winner = snapshot.child(Constant.PLAYER1).child(Constant.NICKNAME).value.toString()
                        } else {

                            ticTacToe.winner = snapshot.child(Constant.PLAYER2).child(Constant.NICKNAME).value.toString()
                        }

                    }

                    Log.d("TTTRepo", "Winner: ${ticTacToe.winner}")
                    matchRef.removeEventListener(this)
                    deleteMatch(snapshot.child(Constant.PLAYER1).child(Constant.ID).value.toString(), snapshot.child(Constant.PLAYER2).child(Constant.ID).value.toString())
                }

                Log.d("TTTRepo", "data listener: ${ticTacToe.fields}")
                ticTacToeData.value = ticTacToe
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    //delete current match
    private fun deleteMatch(player1ID: String, player2ID: String) {

        matchRef.removeValue()
        currentUserRef.child(Constant.INGAME).removeValue()
        rootRef.child(Constant.USERS_PATH).child(player1ID).child(Constant.INGAME).removeValue()
        rootRef.child(Constant.USERS_PATH).child(player2ID).child(Constant.INGAME).removeValue()
    }

    //sends the current move
    fun sendTurn(index: Int, icon: Char, won: Boolean) {

        matchRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var field = snapshot.child(Constant.TTTFIELD).value.toString()
                field = field.substring(0, index) + icon + field.substring(index + 1)
                matchRef.child(Constant.TTTFIELD).setValue(field)
                matchRef.child(Constant.LASTTURN).setValue(currentUser.uid)

                if (won) {

                    matchRef.child(Constant.WINNER).setValue(currentUser.uid)
                } else {

                    if (!field.contains('_')) {

                        matchRef.child(Constant.WINNER).setValue(Constant.DRAW)
                    }
                }

                Log.d("TTTRepo", "sent turn")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun surrender(icon: String, otherPlayer: MutableList<String>) {

        matchRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val field = icon + icon + icon + icon + icon + icon + icon + icon + icon
                matchRef.child(Constant.TTTFIELD).setValue(field)
                val otherPLayerId =
                    if (otherPlayer.get(0).equals(currentUser.uid))
                        otherPlayer.get(0)
                    else otherPlayer.get(1)
                matchRef.child(Constant.LASTTURN).setValue(otherPLayerId)
                matchRef.child(Constant.WINNER).setValue(otherPLayerId)

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
}

