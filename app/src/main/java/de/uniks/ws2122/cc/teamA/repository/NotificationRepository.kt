package de.uniks.ws2122.cc.teamA.repository


import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.model.Notification

class NotificationRepository {
    // Database References
    private val rootRef: DatabaseReference =
        FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference
    init {
    }

    // Add a listener to your friend request in database and if there is a new child
    // make a callback and than delete notification from database
    fun notificationRequestList(callback: (result: Boolean, id: Int, name: String) -> Unit){
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        rootRef.child(Constant.NOTIFICATION).child(Constant.NOTIFICATIONREQUEST).child(currentUser.uid).addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()){
                    val id = snapshot.child("id").value.toString()
                    val name = snapshot.child(Constant.NICKNAME).value.toString()
                    rootRef.child(Constant.NOTIFICATION).child(Constant.NOTIFICATIONREQUEST).child(currentUser.uid).child(id).removeValue()
                    callback.invoke(true, id.hashCode(), name)

                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    callback.invoke(false, 0, Constant.DEFAULT)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
                rootRef.child(Constant.NOTIFICATION).child(Constant.NOTIFICATIONREQUEST).child(currentUser.uid).removeEventListener(this)
            }
        })
    }

    // Add a listener to your game invite in database and if there is a new child
    // make a callback and than delete notification from database
    fun sendGameInviteNotification(callback: (result: Boolean, notification: Notification) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        rootRef.child(Constant.NOTIFICATION).child(Constant.NOTIFICATIONGAMEINVITE).child(currentUser.uid).addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()){
                    val notification = snapshot.getValue(Notification::class.java)
                    rootRef.child(Constant.NOTIFICATION).child(Constant.NOTIFICATIONGAMEINVITE).child(currentUser.uid).child(notification!!.id).removeValue()
                    callback.invoke(true, notification)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val notification = Notification()
                callback.invoke(false, notification)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
                rootRef.child(Constant.NOTIFICATION).child(Constant.NOTIFICATIONGAMEINVITE).child(currentUser.uid).removeEventListener(this)
            }

        })
    }
}