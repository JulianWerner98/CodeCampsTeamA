package de.uniks.ws2122.cc.teamA.friendlist

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import de.uniks.ws2122.cc.teamA.MainActivity
import de.uniks.ws2122.cc.teamA.databinding.ActivityFriendListBinding
import de.uniks.ws2122.cc.teamA.model.Friend
import de.uniks.ws2122.cc.teamA.model.FriendListViewModel

class FriendListActivity : AppCompatActivity(), MyFriendsAdapter.OnItemClickListener {
    private lateinit var binding: ActivityFriendListBinding
    private lateinit var friendsAdapter: MyFriendsAdapter
    private lateinit var viewModel: FriendListViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var enterNickNameField: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enterNickNameField = binding.editEnterNickName
        recyclerView = binding.rvFriendList
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        viewModel = ViewModelProvider(this).get(FriendListViewModel::class.java)

        friendsAdapter = MyFriendsAdapter(viewModel.getLiveDataFriendList(), this)

        viewModel.fetchFriendList()

        viewModel.getLiveDataFriendList().observe(this, Observer {
            recyclerView.adapter = friendsAdapter
        })

        binding.btnSendFriendRequest.setOnClickListener {
            if (enterNickNameField.text.isNotEmpty()) {
                val nickName = enterNickNameField.text.toString()
                viewModel.getFriendListController().sendFriendRequest(nickName) { msg ->
                    Toast.makeText(this@FriendListActivity, msg, Toast.LENGTH_SHORT).show()
                }
            } else {
                enterNickNameField.error = "Please enter a nickname"
                enterNickNameField.requestFocus()
            }
        }

        binding.btnBackToMain.setOnClickListener {
           // TODO: back to Main Menu
        }
        binding.btnRequestList.setOnClickListener {
            startActivity(Intent(this@FriendListActivity, FriendRequestActivity::class.java))
        }
    }

    override fun onItemClick(position: Int) {
        val friend = viewModel.getLiveDataFriendList().value!![position]
        val intent = Intent(this@FriendListActivity, FriendProfileActivity::class.java)
        intent.putExtra("FriendId", friend.id)
        intent.putExtra("nickname", friend.nickname)
        startActivity(intent)
    }
}