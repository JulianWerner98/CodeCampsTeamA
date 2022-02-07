package de.uniks.ws2122.cc.teamA.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant

class TicTacToeRepository {

    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference
    private var tttRef: DatabaseReference
    private lateinit var matchRef: DatabaseReference
    private val currentUser = FirebaseAuth.getInstance().currentUser!!

    init {

        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (!snapshot.hasChild("TocTacToe")) {

                    rootRef.child("TicTacToe")
                }
            }

            override fun onCancelled(error: DatabaseError) {

                TODO("Not yet implemented")
            }
        })

        tttRef = rootRef.child("TicTacToe").ref

        Log.d("TAG", tttRef.toString())
    }

    fun searchMatch() {

        tttRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (!snapshot.hasChild("Match")) {

                    createMatch()
                }
                else {

                    if (!snapshot.child("Match").hasChild(currentUser.uid)){

                        matchRef = tttRef.child("Match").ref
                        matchRef.child("Status").setValue("full")
                        matchRef.child(currentUser.uid).setValue(1)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

                TODO("Not yet implemented")
            }
        })
    }

    private fun createMatch() {

        matchRef = tttRef.child("Match").ref
        matchRef.child("Status").setValue("looking for player")
        matchRef.child(currentUser.uid).setValue(0)
    }
}
