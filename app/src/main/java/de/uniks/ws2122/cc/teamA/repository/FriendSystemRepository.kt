package de.uniks.ws2122.cc.teamA.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.model.User

class FriendSystemRepository {
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private var friendsList = arrayListOf<User>()
    private var receivedList = arrayListOf<User>()
    private var sendList = arrayListOf<User>()

    // Database References
    private val rootRef: DatabaseReference =
        FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference


    // Friend list View model
    // Fetch friend list
    fun fetchFriendList(callback: (result: ArrayList<User>) -> Unit) {
        rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.FRIENDS_PATH)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    friendsList.clear()

                    if (snapshot.exists()) {

                        snapshot.children.forEach {
                            val friend = it.getValue(User::class.java)
                            friendsList.add(friend!!)
                        }
                    }
                    callback.invoke(friendsList)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    // Send your friend request
    fun sendFriendRequest(nickName: String, callback: (result: String) -> Unit) {
        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var exists = false
                val user = snapshot.child(Constant.USERS_PATH).child(currentUser.uid)
                    .getValue(User::class.java)

                // Check that you don't send yourself a friend request
                if (nickName != user!!.nickname) {
                    checkUserExist(snapshot, nickName) { friend ->
                        // Check that you not already friends
                        exists = true
                        if (!snapshot.child(Constant.USERS_PATH).child(currentUser.uid)
                                .child(Constant.FRIENDS_PATH).child(friend.id).exists()
                        ) {
                            // Check that you don't have a friend request from this user
                            if (!snapshot.child(Constant.FRIEND_REQUEST_PATH)
                                    .child(Constant.RECEIVED_PATH).child(currentUser.uid)
                                    .child(friend.id).exists()
                            ) {
                                rootRef.child(Constant.FRIEND_REQUEST_PATH)
                                    .child(Constant.SEND_PATH)
                                    .child(currentUser.uid).child(friend.id).setValue(friend)
                                rootRef.child(Constant.FRIEND_REQUEST_PATH)
                                    .child(Constant.RECEIVED_PATH).child(friend.id)
                                    .child(currentUser.uid).setValue(user)
                                val notikey = rootRef.child(Constant.NOTIFICATION).child(Constant.NOTIFICATIONREQUEST).push().key.toString()
                                user.id = notikey
                                rootRef.child(Constant.NOTIFICATION).child(Constant.NOTIFICATIONREQUEST)
                                    .child(friend.id).child(notikey).setValue(user)
                                callback.invoke("You have send a friend request")
                            } else {
                                callback.invoke("You have received a friend request from this user")
                            }
                        } else {
                            callback.invoke("This user is already your friend")
                        }
                    }
                    if (!exists) {
                        callback.invoke("User doesn't exist")
                    }
                } else {
                    callback.invoke("You can't send yourself a friend request")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun checkUserExist(
        snapshot: DataSnapshot,
        nickName: String,
        callback: (result: User) -> Unit
    ) {
        snapshot.child(Constant.USERS_PATH).children.forEach {
            // Check that user exist
            if (it.child("nickname").value == nickName) {
                val friend = it.getValue(User::class.java)
                callback.invoke(friend!!)
            }
        }
    }

    // Remove friend
    fun removeFriend(friendId: String, callback: (result: Boolean) -> Unit) {
        rootRef.child(Constant.USERS_PATH).child(currentUser.uid)
            .child(Constant.FRIENDS_PATH).child(friendId).removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    rootRef.child(Constant.USERS_PATH).child(friendId)
                        .child(Constant.FRIENDS_PATH).child(currentUser.uid).removeValue()
                    callback.invoke(true)
                } else {
                    callback.invoke(false)
                }
            }

    }

    // Friend request view model
    // Fetch received list
    fun fetchReceivedList(callback: (result: ArrayList<User>) -> Unit) {
        rootRef.child(Constant.FRIEND_REQUEST_PATH).child(Constant.RECEIVED_PATH)
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
                }

            })
    }

    // Fetch send list
    fun fetchSendRequestList(callback: (result: ArrayList<User>) -> Unit) {
        rootRef.child(Constant.FRIEND_REQUEST_PATH).child(Constant.SEND_PATH)
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
                }

            })
    }

    // Fetch current user
    fun fetchCurrentUser(callback: (result: User) -> Unit) {
        rootRef.child(Constant.USERS_PATH)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var currenUser = snapshot.child(currentUser.uid).getValue(User::class.java)
                        callback.invoke(currenUser!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    // Accept friend request
    fun acceptFriendRequest(friend: User, callback: (result: String) -> Unit) {
        fetchCurrentUser { user ->
            // Set friends in database
            rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.FRIENDS_PATH)
                .child(friend.id).setValue(friend).addOnCompleteListener {
                    if (it.isSuccessful) {
                        rootRef.child(Constant.USERS_PATH).child(friend.id)
                            .child(Constant.FRIENDS_PATH)
                            .child(currentUser.uid).setValue(user)

                        // Delete friend request in database
                        rootRef.child(Constant.FRIEND_REQUEST_PATH).child(Constant.RECEIVED_PATH)
                            .child(currentUser.uid)
                            .child(friend.id).removeValue()
                        rootRef.child(Constant.FRIEND_REQUEST_PATH).child(Constant.SEND_PATH)
                            .child(friend.id)
                            .child(currentUser.uid).removeValue()
                        callback.invoke("${friend.nickname} is now your friend")
                    } else {
                        callback.invoke(Constant.ERROR_MSG)
                    }
                }
        }

    }

    // Decline friend request
    fun declineFriendRequest(friend: User, callback: (result: String) -> Unit) {
        // Delete friend request in your received and friend send path
        rootRef.child(Constant.FRIEND_REQUEST_PATH).child(Constant.RECEIVED_PATH)
            .child(currentUser.uid)
            .child(friend.id).removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    rootRef.child(Constant.FRIEND_REQUEST_PATH).child(Constant.SEND_PATH)
                        .child(friend.id)
                        .child(currentUser.uid).removeValue()
                    callback.invoke("you have declined the friend request from ${friend.nickname}")
                } else {
                    callback.invoke(Constant.ERROR_MSG)
                }
            }
    }

    // Cancel your friend request
    fun cancelSendFriendRequest(friend: User, callback: (result: String) -> Unit) {
        // Delete friend request in friend received and your send path
        rootRef.child(Constant.FRIEND_REQUEST_PATH).child(Constant.RECEIVED_PATH)
            .child(friend.id)
            .child(currentUser.uid).removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    rootRef.child(Constant.FRIEND_REQUEST_PATH).child(Constant.SEND_PATH)
                        .child(currentUser.uid)
                        .child(friend.id).removeValue()
                    callback.invoke("you have cancelled the friend request to ${friend.nickname}")
                } else {
                    callback.invoke(Constant.ERROR_MSG)
                }
            }
    }
}