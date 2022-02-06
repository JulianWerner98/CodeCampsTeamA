package de.uniks.ws2122.cc.teamA.friendlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.databinding.ActivityFriendProfileBinding
import de.uniks.ws2122.cc.teamA.model.FriendListViewModel

class FriendProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendProfileBinding
    private lateinit var friendNickName: TextView
    private lateinit var viewModel: FriendListViewModel
    private lateinit var friendId: String
    private lateinit var nickName: String
    private lateinit var btnUnfriend: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        friendNickName = binding.tvFriendProfileNickName
        btnUnfriend = binding.btnUnfriend
        friendId = intent.extras?.get("FriendId").toString()
        nickName = intent.extras?.get("nickname").toString()
        friendNickName.text = nickName

        viewModel = ViewModelProvider(this).get(FriendListViewModel::class.java)

        binding.btnGameInvite.setOnClickListener {
            // TODO: Game invite
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
    }
}