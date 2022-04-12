package de.uniks.ws2122.cc.teamA.friendlist

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.GameSelectActivity
import de.uniks.ws2122.cc.teamA.databinding.ActivityFriendListBinding
import de.uniks.ws2122.cc.teamA.model.FriendListViewModel

class FriendListActivity : AppCompatActivity(), MyFriendsAdapter.OnItemClickListener {
    private lateinit var binding: ActivityFriendListBinding
    private lateinit var friendsAdapter: MyFriendsAdapter
    private lateinit var viewModel: FriendListViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var enterNickNameField: EditText
    private lateinit var shareId : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enterNickNameField = binding.editEnterNickName
        shareId = binding.ivShareId
        recyclerView = binding.rvFriendList
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        viewModel = ViewModelProvider(this)[FriendListViewModel::class.java]

        friendsAdapter = MyFriendsAdapter(viewModel.getLiveDataFriendList(), this)

        viewModel.fetchFriendList()

        viewModel.getLiveDataFriendList().observe(this, Observer {
            recyclerView.adapter = friendsAdapter
        })

        binding.btnSendFriendRequest.setOnClickListener {
            if (enterNickNameField.text.isNotEmpty()) {
                val nickName = enterNickNameField.text.toString()
                viewModel.sendFriendRequest(nickName) { msg ->
                    Toast.makeText(this@FriendListActivity, msg, Toast.LENGTH_SHORT).show()
                }
                enterNickNameField.text.clear()
            } else {
                enterNickNameField.error = "Please enter a nickname"
                enterNickNameField.requestFocus()
            }
        }

        binding.btnBackToMain.setOnClickListener {
            val intent = Intent(this, GameSelectActivity::class.java).apply { }
            startActivity(intent)
        }
        binding.btnRequestList.setOnClickListener {
            startActivity(Intent(this@FriendListActivity, FriendRequestActivity::class.java))
        }

        shareId.setOnClickListener {
            // Share nickname via messengers
            val shareIntent = Intent().apply {
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, "This is my nickname: " + intent.extras?.get("nickname"))
                this.type = "text/plain"
            }
            startActivity(shareIntent)
        }
    }

    override fun onItemClick(position: Int) {
        val friend = viewModel.getLiveDataFriendList().value!![position]
        val intent = Intent(this@FriendListActivity, FriendProfileActivity::class.java)
        intent.putExtra(Constant.FRIENDID, friend.id)
        intent.putExtra(Constant.NICKNAME, friend.nickname)
        startActivity(intent)
    }
}