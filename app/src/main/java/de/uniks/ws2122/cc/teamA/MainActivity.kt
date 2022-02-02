package de.uniks.ws2122.cc.teamA

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import de.uniks.ws2122.cc.teamA.friendlist.FriendListActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val email = findViewById<TextView>(R.id.tvEmail)
        val password = findViewById<TextView>(R.id.tvPassword)

        val loginButton = findViewById<Button>(R.id.btnLogin)
        loginButton.setOnClickListener {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email.text.toString().trim(), password.text.toString().trim())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        Toast.makeText(
                            this@MainActivity,
                            "Du bist eingeloogt",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this, FriendListActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            task.exception!!.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}