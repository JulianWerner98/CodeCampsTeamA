package de.uniks.ws2122.cc.teamA.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant

class TicTacToeRepository {

    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference
    private var tttRef: DatabaseReference
    private lateinit var matchRef: DatabaseReference

    init {

        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (!snapshot.exists()) {

                    rootRef.child("TicTacToe").setValue(1)
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

                if (snapshot.exists()) {

                    createMatch()
                }
                else {

                }
            }

            override fun onCancelled(error: DatabaseError) {

                TODO("Not yet implemented")
            }

        })

        Log.d("TAG", matchRef.toString())
    }

    private fun createMatch() {

        tttRef.child("Match").setValue("looking for players")
        matchRef = tttRef.child("Match").ref
        matchRef.child(FirebaseAuth.getInstance().uid.toString()).setValue(0)
    }
}
