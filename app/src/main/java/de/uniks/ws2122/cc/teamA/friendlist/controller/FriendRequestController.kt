package de.uniks.ws2122.cc.teamA.friendlist.controller

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.model.User

class FriendRequestController {
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private var dbref: DatabaseReference
    private var receivedList = arrayListOf<User>()
    private var sendList = arrayListOf<User>()

    constructor() {
        dbref = FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference
    }

    fun getReceivedList(callback: (result: ArrayList<User>) -> Unit){
        dbref.child(Constant.FRIEND_REQUEST_PATH).child(Constant.RECEIVED_PATH)
            .child(currentUser.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    receivedList.clear()

                    if (snapshot.exists()) {
                        snapshot.children.forEach {
                            val friendRequest = it.getValue(User::class.java)
                            receivedList.add(friendRequest!!)
                        }
                    }
                    callback.invoke(receivedList)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    fun getSendRequestList(callback: (result: ArrayList<User>) -> Unit) {
        dbref.child(Constant.FRIEND_REQUEST_PATH).child(Constant.SEND_PATH)
            .child(currentUser.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    sendList.clear()

                    if (snapshot.exists()) {
                        snapshot.children.forEach {
                            val friendRequest = it.getValue(User::class.java)
                            sendList.add(friendRequest!!)
                        }
                    }
                    callback.invoke(sendList)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    fun getCurrentUser(callback: (result: User) -> Unit){
        dbref.child(Constant.USERS_PATH)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var currenUser = snapshot.child(currentUser.uid).getValue(User::class.java)
                        callback.invoke(currenUser!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    fun acceptFriendRequest(friend: User, callback: (result: String) -> Unit) {
        getCurrentUser { user ->
            // Set friends in database
            dbref.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.FRIENDS_PATH)
                .child(friend.id).setValue(friend)
            dbref.child(Constant.USERS_PATH).child(friend.id).child(Constant.FRIENDS_PATH)
                .child(currentUser.uid).setValue(user)

            // Delete friend request in database
            dbref.child(Constant.FRIEND_REQUEST_PATH).child(Constant.RECEIVED_PATH)
                .child(currentUser.uid)
                .child(friend.id).removeValue()
            dbref.child(Constant.FRIEND_REQUEST_PATH).child(Constant.SEND_PATH)
                .child(friend.id)
                .child(currentUser.uid).removeValue()
            callback.invoke("${friend.nickname} is now your friend")
        }

    }

    fun declineFriendRequest(friend: User, callback: (result: String) -> Unit) {
        // Delete friend request in your received and friend send path
        dbref.child(Constant.FRIEND_REQUEST_PATH).child(Constant.RECEIVED_PATH)
            .child(currentUser.uid)
            .child(friend.id).removeValue()
        dbref.child(Constant.FRIEND_REQUEST_PATH).child(Constant.SEND_PATH)
            .child(friend.id)
            .child(currentUser.uid).removeValue()
        callback.invoke("you have declined the friend request from ${friend.nickname}")
    }

    fun cancelSendFriendRequest(friend: User, callback: (result: String) -> Unit) {
        // Delete friend request in friend received and your send path
        dbref.child(Constant.FRIEND_REQUEST_PATH).child(Constant.RECEIVED_PATH)
            .child(friend.id)
            .child(currentUser.uid).removeValue()
        dbref.child(Constant.FRIEND_REQUEST_PATH).child(Constant.SEND_PATH)
            .child(currentUser.uid)
            .child(friend.id).removeValue()
        callback.invoke("you have cancelled the friend request to ${friend.nickname}")
    }

}