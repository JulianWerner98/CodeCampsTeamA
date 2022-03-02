package de.uniks.ws2122.cc.teamA.friendlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.GameSelectActivity
import de.uniks.ws2122.cc.teamA.TicTacToeActivity
import de.uniks.ws2122.cc.teamA.databinding.ActivityFriendProfileBinding
import de.uniks.ws2122.cc.teamA.model.FriendListViewModel
import de.uniks.ws2122.cc.teamA.repository.FriendInviteRepository
import kotlinx.coroutines.*

class FriendProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendProfileBinding
    private lateinit var friendNickName: TextView
    private lateinit var viewModel: FriendListViewModel
    private lateinit var friendId: String
    private lateinit var nickName: String
    private lateinit var btnUnfriend: Button
    private lateinit var gameListBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        friendNickName = binding.tvFriendProfileNickName
        btnUnfriend = binding.btnUnfriend
        gameListBtn = binding.btnInviteGameList
        friendId = intent.extras?.get(Constant.FRIENDID).toString()
        nickName = intent.extras?.get(Constant.NICKNAME).toString()
        friendNickName.text = nickName


        viewModel = ViewModelProvider(this).get(FriendListViewModel::class.java)

        binding.btnGameInvite.setOnClickListener {

            //TODO Eine bessere Lösung finden

            FriendInviteRepository().privateMatchRequest("TicTacToe", friendId, nickName)

            val intent = Intent(this, GameSelectActivity::class.java)
            startActivity(intent)
        }

        btnUnfriend.setOnClickListener {
            viewModel.getFriendListController().removeFriend(friendId) { result ->
                if (result) {
                    Toast.makeText(
                        this@FriendProfileActivity,
                        "Unfriend $nickName",
                        Toast.LENGTH_SHORT
                    ).show()
                    btnUnfriend.isEnabled = false
                } else {
                    Toast.makeText(
                        this@FriendProfileActivity, Constant.ERROR_MSG, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        gameListBtn.setOnClickListener {
            val intent = Intent(this, FriendGameInviteActivity::class.java).apply {
                this.putExtra(Constant.FRIENDID, friendId)
            }
            startActivity(intent)
        }
    }
}