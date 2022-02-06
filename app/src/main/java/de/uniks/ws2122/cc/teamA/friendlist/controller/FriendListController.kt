package de.uniks.ws2122.cc.teamA.friendlist.controller

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.model.Friend
import de.uniks.ws2122.cc.teamA.model.User

class FriendListController {
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private var dbref: DatabaseReference
    private var friendsList = arrayListOf<User>()

    constructor() {
        dbref = FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference
    }

    fun getFriendList(callback: (result: ArrayList<User>) -> Unit) {
        dbref.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.FRIENDS_PATH)
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
                    TODO("Not yet implemented")
                }
            })
    }

    fun sendFriendRequest(nickName: String, callback: (result: String) -> Unit) {
        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.child(Constant.USERS_PATH).child(currentUser.uid)
                    .getValue(User::class.java)
                //user!!.id = currentUser.uid

                // Check that you don't send yourself a friend request
                if (nickName != user!!.nickname){
                    snapshot.child(Constant.USERS_PATH).children.forEach{
                        // Check that user exist
                        if (it.child("nickname").value == nickName){
                            val friend = it.getValue(User::class.java)
                            //friend!!.id = it.key.toString()

                            // Check that you not already friends
                            if (!snapshot.child(Constant.USERS_PATH).child(currentUser.uid)
                                    .child(Constant.FRIENDS_PATH).child(friend!!.id).exists()) {

                                // Check that you don't have a friend request from this user
                                if (!snapshot.child(Constant.FRIEND_REQUEST_PATH)
                                        .child(Constant.RECEIVED_PATH).child(currentUser.uid)
                                        .child(friend.id).exists()) {
                                            dbref.child(Constant.FRIEND_REQUEST_PATH)
                                                .child(Constant.SEND_PATH)
                                                .child(currentUser.uid).child(friend.id).setValue(friend)
                                            dbref.child(Constant.FRIEND_REQUEST_PATH)
                                                .child(Constant.RECEIVED_PATH).child(friend.id)
                                                .child(currentUser.uid).setValue(user)
                                    callback.invoke("success")
                                } else {
                                    callback.invoke("received")
                                }
                            } else{
                                callback.invoke("friend")
                            }
                        }
                    }
                    //callback.invoke("not_exist")
                } else {
                    callback.invoke("self")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}