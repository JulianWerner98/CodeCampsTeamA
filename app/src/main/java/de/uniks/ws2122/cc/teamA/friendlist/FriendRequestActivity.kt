package de.uniks.ws2122.cc.teamA.friendlist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.MainActivity
import de.uniks.ws2122.cc.teamA.databinding.ActivityFriendRequestBinding
import de.uniks.ws2122.cc.teamA.model.Friend
import de.uniks.ws2122.cc.teamA.model.FriendRequestViewModel
import de.uniks.ws2122.cc.teamA.model.User

class FriendRequestActivity : AppCompatActivity(), MyRequestAdapter.OnItemClickListener {

    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private lateinit var binding: ActivityFriendRequestBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var requestList: ArrayList<Friend>
    private lateinit var myRequestAdapter: MyRequestAdapter
    private lateinit var currentUserName: String
    private lateinit var viewModel: FriendRequestViewModel

    private lateinit var recyclerViewRequestList : RecyclerView
    private lateinit var recyclerViewSendList : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbref = FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference

        recyclerViewRequestList.layoutManager = LinearLayoutManager(this)
        recyclerViewRequestList.setHasFixedSize(true)

        viewModel = ViewModelProvider(this).get(FriendRequestViewModel::class.java)

        requestList = arrayListOf()
        myRequestAdapter = MyRequestAdapter(requestList, this)


        fetchFriendRequest()
        getCurrentUserName()
    }

    private fun getCurrentUserName() {
        dbref.child(Constant.USERS_PATH)
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
        dbref.child(Constant.FRIEND_REQUEST_PATH).child(Constant.RECEIVED_PATH)
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
                    recyclerViewRequestList.adapter = myRequestAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    override fun onAcceptClick(position: Int) {
        val friend = requestList[position]
        // Set friends between users
        dbref.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.FRIENDS_PATH)
            .child(friend.id.toString()).setValue(User(friend.email.toString(), friend.nickname.toString()))
        dbref.child(Constant.USERS_PATH).child(friend.id.toString()).child(Constant.FRIENDS_PATH)
            .child(currentUser.uid).setValue(User(currentUser.email.toString(), currentUserName))

        // Delete friend request in database
        dbref.child(Constant.FRIEND_REQUEST_PATH).child(Constant.RECEIVED_PATH)
            .child(currentUser.uid)
            .child(friend.id.toString()).removeValue()
        dbref.child(Constant.FRIEND_REQUEST_PATH).child(Constant.SEND_PATH)
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
        dbref.child(Constant.FRIEND_REQUEST_PATH).child(Constant.RECEIVED_PATH)
            .child(currentUser.uid)
            .child(friend.id.toString()).removeValue()
        dbref.child(Constant.FRIEND_REQUEST_PATH).child(Constant.SEND_PATH)
            .child(friend.id.toString())
            .child(currentUser.uid).removeValue()

        Toast.makeText(
            this@FriendRequestActivity,
            "you have declined the friend request from ${friend.nickname}",
            Toast.LENGTH_SHORT
        ).show()
    }
}