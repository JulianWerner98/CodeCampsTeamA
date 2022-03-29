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

    // Write for a friend request in database to trigger the request notification
    // and after that delete the notification path from database
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
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                rootRef.child(Constant.NOTIFICATION).child(Constant.NOTIFICATIONREQUEST).child(currentUser.uid).removeEventListener(this)
            }
        })
    }

    fun sendGameInviteNotification(callback: (result: Boolean, id: Int, name: String) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        rootRef.child(Constant.NOTIFICATION).child(Constant.NOTIFICATIONARITHMETIC).child(currentUser.uid).addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()){
                    val id = snapshot.child("id").value.toString()
                    val name = snapshot.child(Constant.NICKNAME).value.toString()
                    rootRef.child(Constant.NOTIFICATION).child(Constant.NOTIFICATIONARITHMETIC).child(currentUser.uid).child(id).removeValue()
                    callback.invoke(true, id.hashCode(), name)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                callback.invoke(false, 0, Constant.DEFAULT)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                rootRef.child(Constant.NOTIFICATION).child(Constant.NOTIFICATIONARITHMETIC).child(currentUser.uid).removeEventListener(this)
            }

        })
    }
}