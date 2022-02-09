package de.uniks.ws2122.cc.teamA.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.model.TicTacToe
import java.lang.reflect.Field
import java.time.format.DateTimeFormatter
import java.util.*

class TicTacToeRepository {

    //Constants
    val TTTQ = "TicTacToeQ"
    val GAMES = "Games"
    val TTT = "TicTacToe"
    val TTTSTATUS = "Status"
    val TTTSTATUSLOOK = "looking for players"
    val TTTSTATUSFULL = "full"
    val TTTFIELD = "Field"
    val BLANKFIELD = "_________"
    val LASTTURN = "Last turn"

    //References
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference
    private var gamesRef: DatabaseReference
    private var tttQRef: DatabaseReference
    private lateinit var tttRef: DatabaseReference
    private val currentUser = FirebaseAuth.getInstance().currentUser!!

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

        Log.d("TAG", tttQRef.toString())
    }

    fun joinQueue() {

        tttQRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                //check if the user already in the Q
                if (!snapshot.hasChild(currentUser.uid)) {

                    tttQRef.child(currentUser.uid).setValue(System.currentTimeMillis())
                    tttQRef.child(currentUser.uid).push()
                    findMatch()
                }
            }

            override fun onCancelled(error: DatabaseError) {

                TODO("Not yet implemented")
            }
        })
    }

    private fun findMatch() {

        tttQRef.orderByKey().limitToFirst(1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                createMatch(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }

    private fun createMatch(snapshot: DataSnapshot) {

        //check if the current user the longest waiting user is

        var firstUserRef: DatabaseReference? = null

        snapshot.children.forEach {

            firstUserRef = it.ref
        }

        Log.d("TAG1", firstUserRef.toString())
        Log.d("TAG2", tttQRef.child(currentUser.uid).ref.toString())

        if (firstUserRef == tttQRef.child(currentUser.uid).ref) {

            //create new Match
            tttRef = gamesRef.child(TTT).push()
            tttRef.child(TTTSTATUS).setValue(TTTSTATUSLOOK)
            tttRef.child(currentUser.uid).setValue(0)
            tttRef.child(TTTFIELD).setValue(BLANKFIELD)
            tttRef.child(LASTTURN).setValue(currentUser.uid)

            //remove user from the Q
            tttQRef.child(currentUser.uid).setValue(null)

            //remove listener

            //add TicTacToe listener
            tttRef.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    var ticTacToe = TicTacToe()
                    ticTacToe.fields = snapshot.child(TTTFIELD).value.toString()

                    if (snapshot.child(currentUser.uid).value == 1) {

                        ticTacToe.isCircle = true
                    }

                    if (snapshot.child(LASTTURN).value.toString() != currentUser.uid) {

                        ticTacToe.isMyTurn = true
                    }

                    ticTacToeData.value = ticTacToe
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            } )

            Log.d("TAG", tttRef.toString())
        }
    }

    fun sendTurn(index: Int, icon: Char) {

        tttRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var field = snapshot.child(TTTFIELD).value.toString()
                field = field.substring(0, index) + icon + field.substring(index + 1)
                tttRef.child(TTTFIELD).setValue(field)
                tttRef.child(LASTTURN).setValue(currentUser.uid)
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

