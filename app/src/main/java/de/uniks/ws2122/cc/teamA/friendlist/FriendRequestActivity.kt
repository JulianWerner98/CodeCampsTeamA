package de.uniks.ws2122.cc.teamA.friendlist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constants
import de.uniks.ws2122.cc.teamA.MainActivity
import de.uniks.ws2122.cc.teamA.databinding.ActivityFriendRequestBinding
import de.uniks.ws2122.cc.teamA.model.Friend
import de.uniks.ws2122.cc.teamA.model.User

class FriendRequestActivity : AppCompatActivity(), MyRequestAdapter.OnItemClickListener {

    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private lateinit var binding: ActivityFriendRequestBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var requestList: ArrayList<Friend>
    private lateinit var myRequestAdapter: MyRequestAdapter
    private lateinit var currentUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbref = FirebaseDatabase.getInstance(Constants.FIREBASE_URL).reference

        binding.RecyclerViewRequestList.layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewRequestList.setHasFixedSize(true)

        requestList = arrayListOf()
        myRequestAdapter = MyRequestAdapter(requestList, this)

        binding.btnMainMenu.setOnClickListener {
            startActivity(Intent(this@FriendRequestActivity, MainActivity::class.java))
            finish()
        }
        binding.btnFriendList.setOnClickListener {
            startActivity(Intent(this@FriendRequestActivity, FriendListActivity::class.java))
            finish()
        }

        fetchFriendRequest()
        getCurrentUserName()
    }

    private fun getCurrentUserName() {
        dbref.child(Constants.USERS_PATH)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        currentUserName =
                            snapshot.child(currentUser.uid).child("nickname").value.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun fetchFriendRequest() {
        dbref.child(Constants.FRIEND_REQUEST_PATH).child(Constants.RECEIVED_PATH)
            .child(currentUser.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    requestList.clear()

                    if (snapshot.exists()) {
                        snapshot.children.forEach {
                            val friendRequest = it.getValue(Friend::class.java)
                            requestList.add(friendRequest!!)
                        }
                    }
                    binding.RecyclerViewRequestList.adapter = myRequestAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    override fun onAcceptClick(position: Int) {
        val friend = requestList[position]
        // Set friends between users
        dbref.child(Constants.USERS_PATH).child(currentUser.uid).child(Constants.FRIENDS_PATH)
            .child(friend.id.toString()).setValue(User(friend.email, friend.nickname))
        dbref.child(Constants.USERS_PATH).child(friend.id.toString()).child(Constants.FRIENDS_PATH)
            .child(currentUser.uid).setValue(User(currentUser.email, currentUserName))

        // Delete friend request in database
        dbref.child(Constants.FRIEND_REQUEST_PATH).child(Constants.RECEIVED_PATH)
            .child(currentUser.uid)
            .child(friend.id.toString()).removeValue()
        dbref.child(Constants.FRIEND_REQUEST_PATH).child(Constants.SEND_PATH)
            .child(friend.id.toString())
            .child(currentUser.uid).removeValue()

        Toast.makeText(
            this@FriendRequestActivity,
            "${friend.nickname} is now your friend",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDeclineClick(position: Int) {
        val friend = requestList[position]
        // Decline friend request and delete it in database
        dbref.child(Constants.FRIEND_REQUEST_PATH).child(Constants.RECEIVED_PATH)
            .child(currentUser.uid)
            .child(friend.id.toString()).removeValue()
        dbref.child(Constants.FRIEND_REQUEST_PATH).child(Constants.SEND_PATH)
            .child(friend.id.toString())
            .child(currentUser.uid).removeValue()

        Toast.makeText(
            this@FriendRequestActivity,
            "you have declined the friend request from ${friend.nickname}",
            Toast.LENGTH_SHORT
        ).show()
    }
}