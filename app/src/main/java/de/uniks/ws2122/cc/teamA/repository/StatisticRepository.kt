package de.uniks.ws2122.cc.teamA.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.model.GameInvites
import de.uniks.ws2122.cc.teamA.model.Highscore
import de.uniks.ws2122.cc.teamA.model.MatchResult

class StatisticRepository {
    private val currentUser = FirebaseAuth.getInstance().currentUser!!

    // Database References
    private val rootRef: DatabaseReference =
        FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference


    fun fetchHistorieList(callback: (result: ArrayList<MatchResult>) -> Unit) {
        rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.STATISTIC).child(Constant.HISTORIE).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val matchResultList = arrayListOf<MatchResult>()

                if (snapshot.exists()){
                    snapshot.children.forEach {
                        val matchResult = it.getValue(MatchResult::class.java)
                        matchResultList.add(matchResult!!)
                    }
                }
                callback.invoke(matchResultList)
            }

            override fun onCancelled(error: DatabaseError) { }

        })
    }

    fun fetchTicTacToeStatistic(callback: (result: Highscore?) -> Unit) {
        rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.STATISTIC).child(Constant.TTT).get().addOnSuccessListener {
            val statistic = it.getValue(Highscore::class.java)
            callback.invoke(statistic)
        }
    }

    fun fetchMentalArithmeticStatistic(callback: (result: Highscore?) -> Unit) {
        rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.STATISTIC).child(Constant.MENTALARITHMETIC).get().addOnSuccessListener {
            val statistic = it.getValue(Highscore::class.java)
            callback.invoke(statistic)
        }
    }

    fun fetchCompassGameStatistic(callback: (result: Highscore?) -> Unit) {
        rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.STATISTIC).child(Constant.COMPASS_GAME).get().addOnSuccessListener {
            val statistic = it.getValue(Highscore::class.java)
            callback.invoke(statistic)
        }
    }

    fun fetchSportChallengeStatistic(callback: (result: Highscore?) -> Unit) {
        rootRef.child(Constant.USERS_PATH).child(currentUser.uid).child(Constant.STATISTIC).child(Constant.SPORT_CHALLENGE).get().addOnSuccessListener {
            val statistic = it.getValue(Highscore::class.java)
            callback.invoke(statistic)
        }
    }
}