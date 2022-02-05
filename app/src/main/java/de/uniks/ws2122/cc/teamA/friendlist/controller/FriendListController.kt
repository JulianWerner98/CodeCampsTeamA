package de.uniks.ws2122.cc.teamA.friendlist.controller

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import de.uniks.ws2122.cc.teamA.Constant

class FriendListController {
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private var dbref: DatabaseReference

    constructor() {
        dbref = FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference
    }
}