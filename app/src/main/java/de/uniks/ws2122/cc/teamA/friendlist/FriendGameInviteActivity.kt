package de.uniks.ws2122.cc.teamA.friendlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import de.uniks.ws2122.cc.teamA.*
import de.uniks.ws2122.cc.teamA.databinding.ActivityFriendGameInviteBinding
import de.uniks.ws2122.cc.teamA.mentalArithmetic.MentalArithmeticActivity

class FriendGameInviteActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFriendGameInviteBinding
    private lateinit var friendId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendGameInviteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        friendId = intent.extras?.getString("friendId").toString()

        // Change to MentalArithmetic
        binding.btnInviteArithmetic.setOnClickListener {
            val intent = Intent(this, MentalArithmeticActivity::class.java).apply {
                this.putExtra(Constant.FRIENDID, friendId)
                this.putExtra(Constant.MATCHTYP, Constant.PRIVATE)
                this.putExtra(Constant.INVITEKEY, Constant.DEFAULT)
            }
            startActivity(intent)
        }

        // Change to TicTacToe
        binding.btnInviteTTT.setOnClickListener(){
            val intent = Intent(this, TicTacToeActivity::class.java).apply {
                this.putExtra(Constant.FRIENDID, friendId)
            }
            startActivity(intent)
        }

        // Change to CompassGame
        binding.btnInviteCompass.setOnClickListener {
            val intent = Intent(this, CompassActivity::class.java).apply {
                this.putExtra(Constant.FRIENDID, friendId)
            }
            startActivity(intent)
        }

        // Change to SportChallenge
        binding.btnInviteSport.setOnClickListener {
            val intent = Intent(this, SportChallengesActivity::class.java).apply {
                this.putExtra(Constant.FRIENDID, friendId)
            }
            startActivity(intent)
        }



    }
}