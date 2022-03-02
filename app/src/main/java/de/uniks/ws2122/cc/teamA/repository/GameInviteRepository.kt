package de.uniks.ws2122.cc.teamA.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.model.GameInvites

class GameInviteRepository {
    private val currentUser = FirebaseAuth.getInstance().currentUser!!

    // Database References
    private val rootRef: DatabaseReference =
        FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference

    fun deleteInvite(gameName: String, friendName: String) {
        rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.INVITES).child(gameName).child(friendName).removeValue()
    }

    fun fetchInvitesList(callback: (result: ArrayList<GameInvites>) -> Unit ) {
        rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.INVITES).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val invitesList = arrayListOf<GameInvites>()

                if (snapshot.exists()){
                    snapshot.children.forEach {
                        val gameInvites = GameInvites()
                        gameInvites.gameName = it.key.toString()
                        it.children.forEach{ name ->
                            gameInvites.friendName = name.key.toString()
                            gameInvites.gameKey = name.value.toString()
                        }
                        invitesList.add(gameInvites)
                    }
                }
                callback.invoke(invitesList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}