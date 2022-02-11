package de.uniks.ws2122.cc.teamA.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant

class FriendInviteRepository {

    //References
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private val currentUserRef = rootRef.child(Constant.USERS_PATH).child(currentUser.uid).ref
    private var toastString = ""
    private lateinit var game: String
    private lateinit var friendID: String
    private lateinit var friendNickname: String

    fun privateMatchRequest(game: String, friendID: String, friendNickname: String) {

        this.friendID = friendID
        this.game = game
        this.friendNickname = friendNickname

        rootRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (isInGame(snapshot)) {

                    toastString = Constant.ALREADY_INGAME_ERROR
                }
                else {

                    if (isInvited(snapshot)) {

                        acceptRequest()
                    }
                    else {

                        createMatchRequest()
                        when(game) {

                            "TicTacToe" -> createPrivateTicTacToeMatch()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                toastString = "ERROR"
            }
        } )
    }

    private fun isInGame(snapshot: DataSnapshot): Boolean {

        return snapshot.child(Constant.USERS_PATH).child(currentUser.uid).hasChild(Constant.INGAME)
    }

    private fun isInvited(snapshot: DataSnapshot): Boolean {

        var isInvited = false

        if (snapshot.child(Constant.GAMES).child(Constant.MATCH_REQUEST).hasChild(currentUser.uid)) {

            isInvited = snapshot.child(Constant.GAMES).child(Constant.MATCH_REQUEST).child(currentUser.uid).child(Constant.FROM).value.toString() == friendID
        }

        Log.d("FIRepo", "is invited: $isInvited")
        return isInvited
    }

    private fun acceptRequest() {

        val friendRef = rootRef.child(Constant.USERS_PATH).child(friendID).ref
        var matchRefString: String

        friendRef.child(Constant.INGAME).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                matchRefString = snapshot.value.toString()
                currentUserRef.child(Constant.INGAME).setValue(matchRefString)

                val matchRef = rootRef.child(Constant.GAMES).child(Constant.TTT).child(matchRefString)
                matchRef.child(Constant.PLAYER2).child(Constant.ID).setValue(currentUser.uid)

                val matchRequestRef = rootRef.child(Constant.GAMES).child(Constant.MATCH_REQUEST).child(currentUser.uid).ref
                matchRequestRef.removeValue()
                Log.d("FIRepo", "accept: $matchRefString")
            }

            override fun onCancelled(error: DatabaseError) {
                toastString = "ERROR"
            }
        })
    }

    private fun createMatchRequest() {

        val matchRequestRef = rootRef.child(Constant.GAMES).child(Constant.MATCH_REQUEST).child(friendID).ref
        matchRequestRef.child(Constant.FROM).setValue(currentUser.uid)
        matchRequestRef.child(Constant.GAME).setValue(game)

        Log.d("FIRepo", "create: $matchRequestRef")
    }

    private fun createPrivateTicTacToeMatch() {

        val matchRef = rootRef.child(Constant.GAMES).child(Constant.TTT).push()

        //set match ref to user
        currentUserRef.child(Constant.INGAME).setValue(matchRef.key)

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

        matchRef.child(Constant.PLAYER2).child(Constant.NICKNAME).setValue(friendNickname)
        matchRef.child(Constant.TTTFIELD).setValue(Constant.BLANKFIELD)
        matchRef.child(Constant.LASTTURN).setValue(currentUser.uid)

        Log.d("TTTRepo", "created private match: $matchRef")
    }
}