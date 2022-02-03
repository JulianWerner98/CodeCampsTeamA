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
import de.uniks.ws2122.cc.teamA.databinding.ActivityFriendListBinding
import de.uniks.ws2122.cc.teamA.model.Friend

class FriendListActivity : AppCompatActivity(), MyFriendsAdapter.OnItemClickListener {
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private lateinit var binding: ActivityFriendListBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var friendsList: ArrayList<Friend>
    private lateinit var friendsAdapter: MyFriendsAdapter
    private lateinit var friendId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.RecyclerViewFriendList.layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewFriendList.setHasFixedSize(true)

        friendsList = arrayListOf()
        friendsAdapter = MyFriendsAdapter(friendsList, this)
        dbref = FirebaseDatabase.getInstance(Constants.FIREBASE_URL).reference

        // Get friends from database and show them on the recyclerview
        fetchFriendsList()

        binding.btnSendFriendRequest.setOnClickListener {
            if (binding.tvEnterFriendID.text.isNotEmpty()) {
                friendId = binding.tvEnterFriendID.text.toString()
                sendFriendRequest()
            }
        }

        binding.btnMainMenu.setOnClickListener {
            startActivity(Intent(this@FriendListActivity, MainActivity::class.java))
            finish()
        }
        binding.btnFriendRequestList.setOnClickListener {
            startActivity(Intent(this@FriendListActivity, FriendRequestActivity::class.java))
        }
    }

    private fun sendFriendRequest() {
        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val friend = snapshot.child(Constants.USERS_PATH).child(friendId)
                    .getValue(Friend::class.java)
                friend!!.id = friendId
                val user = snapshot.child(Constants.USERS_PATH).child(currentUser.uid)
                    .getValue(Friend::class.java)
                user!!.id = currentUser.uid
                // Check that you don't send yourself a friend request
                if (friendId != currentUser.uid) {
                    // Check that you not already friends
                    if (!snapshot.child(Constants.USERS_PATH).child(currentUser.uid)
                            .child(Constants.FRIENDS_PATH).exists()
                    ) {
                        // Check that you don't have a friend request from this user
                        if (!snapshot.child(Constants.FRIEND_REQUEST_PATH)
                                .child(Constants.RECEIVED_PATH).child(currentUser.uid)
                                .child(friendId).exists()
                        ) {
                            dbref.child(Constants.FRIEND_REQUEST_PATH).child(Constants.SEND_PATH)
                                .child(currentUser.uid).child(friendId).setValue(friend)
                            dbref.child(Constants.FRIEND_REQUEST_PATH)
                                .child(Constants.RECEIVED_PATH).child(friendId)
                                .child(currentUser.uid).setValue(user)
                            Toast.makeText(
                                this@FriendListActivity,
                                "You have send a friend request",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@FriendListActivity,
                                "You have received a friend request from this user",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@FriendListActivity,
                            "This user is already your friend",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@FriendListActivity,
                        "You can not send yourself a friend request",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun fetchFriendsList() {
        dbref.child(Constants.USERS_PATH).child(currentUser.uid).child(Constants.FRIENDS_PATH)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    friendsList.clear()

                    if (snapshot.exists()) {

                        snapshot.children.forEach {
                            val friend = it.getValue(Friend::class.java)
                            friend!!.id = it.key.toString()
                            friendsList.add(friend)
                        }
                    }
                    binding.RecyclerViewFriendList.adapter = friendsAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun onRemoveClick(position: Int) {
        // Remove friend from your list
        val friend = friendsList[position]
        dbref.child(Constants.USERS_PATH).child(currentUser.uid).child(Constants.FRIENDS_PATH)
            .child(friend.id.toString()).removeValue()
        dbref.child(Constants.USERS_PATH).child(friend.id.toString()).child(Constants.FRIENDS_PATH)
            .child(currentUser.uid).removeValue()
        Toast.makeText(this@FriendListActivity, "Unfriend ${friend.nickname}", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onInviteClick(position: Int) {
        Toast.makeText(
            this@FriendListActivity,
            "You have invited someone to a game...maybe?!",
            Toast.LENGTH_SHORT
        ).show()
    }
}