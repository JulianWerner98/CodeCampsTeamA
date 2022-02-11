package de.uniks.ws2122.cc.teamA.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.model.TicTacToe
import java.util.*

//Constants
const val TTTQ = "TicTacToeQ"
const val GAMES = "Games"
const val TTT = "TicTacToe"
const val TTTFIELD = "Field"
const val LASTTURN = "lastTurn"
const val INGAME = "inGame"
const val OPENMATCHES = "openMatches"
const val PLAYER1 = "Player1"
const val PLAYER2 = "Player2"
const val NICKNAME = "nickname"
const val WINNER = "winner"
const val ID = "id"
const val DRAW = "Draw"
const val BLANKFIELD = "_________"

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

        var hasGame = false

        if (snapshot.child(Constant.USERS_PATH).child(currentUser.uid).hasChild(INGAME)) {

            val matchRefString = snapshot.child(Constant.USERS_PATH).child(currentUser.uid).child(INGAME).value.toString()
            Log.d("TTTRepo", snapshot.child(Constant.USERS_PATH).child(currentUser.uid).child(INGAME).ref.toString())
            hasGame = snapshot.child(GAMES).child(TTT).hasChild(matchRefString)
            Log.d("TTTRepo", "hasGame: $hasGame")
        }

        return hasGame
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

                matchRef.child(PLAYER2).child(ID).setValue(currentUser.uid)
                val nickname = snapshot.child(NICKNAME).value.toString()
                matchRef.child(PLAYER2).child(NICKNAME).setValue(nickname)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //set match ref to user
        currentUserRef.child(INGAME).setValue(matchRef.key)

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

        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                matchRef.child(PLAYER1).child(ID).setValue(currentUser.uid)
                val nickname = snapshot.child(NICKNAME).value.toString()
                matchRef.child(PLAYER1).child(NICKNAME).setValue(nickname)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        matchRef.child(TTTFIELD).setValue(BLANKFIELD)
        matchRef.child(LASTTURN).setValue(currentUser.uid)

        //set match ref to open matches and to user
        tttRef.child(OPENMATCHES).child(matchRef.key.toString()).setValue(matchRef.key)
        currentUserRef.child(INGAME).setValue(matchRef.key)

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


                if (snapshot.child(PLAYER1).child(ID).value.toString() == currentUser.uid) {

                    ticTacToe.isCircle = true
                }

                if (snapshot.child(LASTTURN).value.toString() != currentUser.uid) {

                    ticTacToe.isMyTurn = true
                }

                if (ticTacToe.players.size < 2 && snapshot.child(PLAYER2).exists()) {

                    val nicknamePlayer1 = snapshot.child(PLAYER1).child(NICKNAME).value.toString()
                    val nicknamePlayer2 = snapshot.child(PLAYER2).child(NICKNAME).value.toString()

                    if (snapshot.child(PLAYER1).child(ID).value == currentUser.uid) {

                        ticTacToe.players.add(nicknamePlayer1)
                        ticTacToe.players.add(nicknamePlayer2)

                    } else {

                        ticTacToe.players.add(nicknamePlayer2)
                        ticTacToe.players.add(nicknamePlayer1)
                    }
                }

                if (snapshot.child(WINNER).exists()) {

                    if(snapshot.child(WINNER).value == DRAW) {

                       ticTacToe.winner = DRAW
                    }

                    if (snapshot.child(WINNER).value == snapshot.child(PLAYER1).child(ID).value) {

                        ticTacToe.winner = snapshot.child(PLAYER1).child(NICKNAME).value.toString()
                    } else {

                        ticTacToe.winner = snapshot.child(PLAYER2).child(NICKNAME).value.toString()
                    }

                    matchRef.removeEventListener(this)
                    deleteMatch()
                }

                Log.d("TTTRepo", "data listener: ${ticTacToe.fields}")
                ticTacToeData.value = ticTacToe
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    //delete current match
    private fun deleteMatch() {

        matchRef.removeValue()
        currentUserRef.child(INGAME).removeValue()
    }

    //sends the current move
    fun sendTurn(index: Int, icon: Char, won: Boolean) {

        matchRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var field = snapshot.child(TTTFIELD).value.toString()
                field = field.substring(0, index) + icon + field.substring(index + 1)
                matchRef.child(TTTFIELD).setValue(field)
                matchRef.child(LASTTURN).setValue(currentUser.uid)

                if (won) {

                    matchRef.child(WINNER).setValue(currentUser.uid)
                } else {

                    if (!field.contains('_')) {

                        matchRef.child(WINNER).setValue(DRAW)
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
                matchRef.child(TTTFIELD).setValue(field)
                val otherPLayerId =
                    if (otherPlayer.get(0).equals(currentUser.uid))
                        otherPlayer.get(0)
                    else otherPlayer.get(1)
                matchRef.child(LASTTURN).setValue(currentUser.uid)
                matchRef.child(WINNER).setValue(otherPLayerId)

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

