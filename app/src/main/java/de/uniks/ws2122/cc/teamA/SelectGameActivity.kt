package de.uniks.ws2122.cc.teamA

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import de.uniks.ws2122.cc.teamA.games.TicTacToeActivity

class SelectGameActivity : AppCompatActivity() {
    private lateinit var startTTTButton: Button
    private lateinit var logoutButton: Button
    private lateinit var nickname: TextView

    private lateinit var user: FirebaseUser
    private lateinit var reference: DatabaseReference
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_game)
        startTTTButton = findViewById(R.id.startTTTButton)
        logoutButton = findViewById(R.id.logoutButton)
        nickname = findViewById(R.id.nicknameSelectionActivity)

        startTTTButton.setOnClickListener { startTTT() }
        logoutButton.setOnClickListener { logoutUser() }

        //Get Data from Database
        user = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance(firebaseDatabaseURL).getReference("Users")
        userID = user.uid
        nickname.text = "Some Data"

        reference.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                val userProfile = dataSnapshot.getValue<User>()
                println("User")
                if (userProfile != null) {
                    nickname.text = userProfile.nickname
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun startTTT() {
        val intent = Intent(this, TicTacToeActivity::class.java).apply { }
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        logoutUser()
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(
            applicationContext,
            "Logout successfully!",
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(this, MainActivity::class.java).apply { }
        startActivity(intent)
    }


}