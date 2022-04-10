package de.uniks.ws2122.cc.teamA.friendlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import de.uniks.ws2122.cc.teamA.*
import de.uniks.ws2122.cc.teamA.databinding.ActivityFriendGameInviteBinding
import de.uniks.ws2122.cc.teamA.mentalArithmetic.MentalArithmeticActivity
import de.uniks.ws2122.cc.teamA.model.MentalArithmetic
import de.uniks.ws2122.cc.teamA.model.compassGame.CompassGame
import de.uniks.ws2122.cc.teamA.repository.FriendInviteRepository

class FriendGameInviteActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFriendGameInviteBinding
    private lateinit var friendId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendGameInviteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        friendId = intent.extras?.getString("friendId").toString()
        binding.btnInviteArithmetic.setOnClickListener {
            val intent = Intent(this, MentalArithmeticActivity::class.java).apply {
                this.putExtra(Constant.FRIENDID, friendId)
                this.putExtra(Constant.MATCHTYP, Constant.PRIVATE)
                this.putExtra(Constant.INVITEKEY, Constant.DEFAULT)
            }
            startActivity(intent)
        }
        binding.btnInviteTTT.setOnClickListener(){
            FriendInviteRepository().privateMatchRequest("TicTacToe", friendId)
            val intent = Intent(this, GameSelectActivity::class.java)
            startActivity(intent)
        }

        binding.btnInviteCompass.setOnClickListener {
            val intent = Intent(this, CompassActivity::class.java).apply {
                this.putExtra(Constant.FRIENDID, friendId)
            }
            startActivity(intent)
        }
        binding.btnInviteSport.setOnClickListener {
            val intent = Intent(this, SportChallengesActivity::class.java).apply {
                this.putExtra(Constant.FRIENDID, friendId)
            }
            startActivity(intent)
        }



    }
}