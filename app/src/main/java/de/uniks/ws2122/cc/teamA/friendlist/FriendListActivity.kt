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
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.MainActivity
import de.uniks.ws2122.cc.teamA.databinding.ActivityFriendListBinding
import de.uniks.ws2122.cc.teamA.model.Friend
import de.uniks.ws2122.cc.teamA.model.FriendListViewModel

class FriendListActivity : AppCompatActivity(), MyFriendsAdapter.OnItemClickListener {
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private lateinit var binding: ActivityFriendListBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var friendsList: ArrayList<Friend>
    private lateinit var friendsAdapter: MyFriendsAdapter
    private lateinit var friendId: String
    private lateinit var viewModel: FriendListViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var enterNickNameField : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.rvFriendList
        enterNickNameField = binding.editEnterNickName

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        viewModel = ViewModelProvider(this).get(FriendListViewModel::class.java)

        friendsAdapter = MyFriendsAdapter(viewModel.getLiveDataFriendList(), this)

        viewModel.fetchFriendList()

        viewModel.getLiveDataFriendList().observe(this, Observer {
            recyclerView.adapter = friendsAdapter
        })

        dbref = FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference

        binding.btnSendFriendRequest.setOnClickListener {
            if (enterNickNameField.text.isNotEmpty()) {
                val nickName = enterNickNameField.text.toString()
                viewModel.getFriendListController().sendFriendRequest(nickName) { result ->
                    when(result){
                        "self" -> Toast.makeText(
                            this@FriendListActivity,
                            "You can not send yourself a friend request",
                            Toast.LENGTH_SHORT
                        ).show()
                        "not_exist" -> Toast.makeText(
                            this@FriendListActivity,
                            "This user doesn't exist",
                            Toast.LENGTH_SHORT
                        ).show()
                        "friend" -> Toast.makeText(
                            this@FriendListActivity,
                            "This user is already your friend",
                            Toast.LENGTH_SHORT
                        ).show()
                        "received" -> Toast.makeText(
                            this@FriendListActivity,
                            "You have received a friend request from this user",
                            Toast.LENGTH_SHORT
                        ).show()
                        "success" -> Toast.makeText(
                            this@FriendListActivity,
                            "You have send a friend request",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                enterNickNameField.error = "Please enter a nickname"
                enterNickNameField.requestFocus()
            }
        }

        binding.btnBackToMain.setOnClickListener {
            startActivity(Intent(this@FriendListActivity, MainActivity::class.java))
            FirebaseAuth.getInstance().signOut()
            finish()
        }
        binding.btnRequestList.setOnClickListener {
            startActivity(Intent(this@FriendListActivity, FriendRequestActivity::class.java))
        }
    }

    override fun onItemClick(position: Int) {
        var friend = viewModel.getLiveDataFriendList().value!![position]
        // TODO: Change activity to friend profile
        Toast.makeText(this@FriendListActivity, "Next stop friend profile", Toast.LENGTH_SHORT).show()
    }
}