package de.uniks.ws2122.cc.teamA.friendlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.R
import de.uniks.ws2122.cc.teamA.databinding.ActivityFriendGameInviteBinding
import de.uniks.ws2122.cc.teamA.mentalArithmetic.MentalArithmeticActivity
import de.uniks.ws2122.cc.teamA.model.MentalArithmetic

class FriendGameInviteActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFriendGameInviteBinding
    private lateinit var inviteArithmeticBtn : Button
    private lateinit var friendId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendGameInviteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inviteArithmeticBtn = binding.btnInviteArithmetic

        friendId = intent.extras?.getString("friendId").toString()
        inviteArithmeticBtn.setOnClickListener {
            val intent = Intent(this, MentalArithmeticActivity::class.java).apply {
                this.putExtra(Constant.FRIENDID, friendId)
                this.putExtra(Constant.MATCHTYP, Constant.PRIVATE)
                this.putExtra(Constant.INVITEKEY, Constant.DEFAULT)
            }
            startActivity(intent)
        }
    }
}