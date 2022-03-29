package de.uniks.ws2122.cc.teamA

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.databinding.ActivityGameSelectBinding
import de.uniks.ws2122.cc.teamA.friendlist.FriendListActivity
import de.uniks.ws2122.cc.teamA.friendlist.FriendRequestActivity
import de.uniks.ws2122.cc.teamA.gameInvite.GameInviteListActivity
import de.uniks.ws2122.cc.teamA.mentalArithmetic.MentalArithmeticActivity
import de.uniks.ws2122.cc.teamA.model.AppViewModel

class GameSelectActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var viewModel: AppViewModel
    private lateinit var binding: ActivityGameSelectBinding
    private lateinit var logoutBtn: Button
    private lateinit var spinner: ProgressBar
    private lateinit var friendlistBtn: Button
    private lateinit var tttBtn: Button
    private lateinit var compassBtn: Button
    private lateinit var nicknameText: TextView
    private lateinit var mentalArithmeticBtn : Button
    private lateinit var gameInviteListBtn : Button
    private lateinit var sportBtn: Button

    private var notificationId : Int = 0
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logoutBtn = binding.btnLogout
        spinner = binding.spinner
        spinner.isVisible = false
        friendlistBtn = binding.btnFriedlist
        tttBtn = binding.tttBtn
        compassBtn = binding.kompassBtn
        nicknameText = binding.TVnickname
        sportBtn = binding.btnSportChallenges
        mentalArithmeticBtn = binding.btnMentalArithmetic
        gameInviteListBtn = binding.btnGameInviteList

        tttBtn.setOnClickListener(this)
        friendlistBtn.setOnClickListener(this)
        logoutBtn.setOnClickListener(this)
        sportBtn.setOnClickListener(this)
        compassBtn.setOnClickListener(this)
        mentalArithmeticBtn.setOnClickListener(this)
        gameInviteListBtn.setOnClickListener(this)

        viewModel = ViewModelProvider(this)[AppViewModel::class.java]

        createNotificationChannel()

        viewModel.getLiveValueUser().observe(this) { user ->
            nicknameText.text = user.nickname
        }

        viewModel.notificationRequestList(){ result, id, name ->
            if (result) {
                notificationId = id
                val intent = Intent(this, FriendRequestActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

                val builder = NotificationCompat.Builder(this, Constant.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Request notification")
                    .setContentText("$name has send you a friend request")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

                with(NotificationManagerCompat.from(this)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(notificationId, builder.build())
                }
            }
        }
        requestPermissions()
    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            logoutBtn.id -> logout()
            tttBtn.id -> changeToTicTacToeScreen()
            friendlistBtn.id -> changeToFriendslist()
            compassBtn.id -> changeToCompassScreen()
            mentalArithmeticBtn.id -> changeToMentalArithmetic()
            gameInviteListBtn.id -> changeToGameInviteList()
            sportBtn.id -> changeToSportChallenges()
        }
    }

    private fun changeToGameInviteList() {
        val intent = Intent(this, GameInviteListActivity::class.java).apply {  }
        startActivity(intent)
    }

    private fun changeToMentalArithmetic() {
        val intent = Intent(this, MentalArithmeticActivity::class.java).apply {
            this.putExtra(Constant.FRIENDID, Constant.DEFAULT)
            this.putExtra(Constant.MATCHTYP, Constant.DEFAULT)
            this.putExtra(Constant.INVITEKEY, Constant.DEFAULT)
        }
        startActivity(intent)
    }

    private fun logout() {
        viewModel.logoutUser()
        val intent = Intent(this, MainActivity::class.java).apply { }
        startActivity(intent)
    }

    private fun changeToFriendslist() {
        val intent = Intent(this, FriendListActivity::class.java).apply {
            this.putExtra("nickname", nicknameText.text)
        }
        startActivity(intent)
    }

    private fun changeToTicTacToeScreen() {
        val intent = Intent(this, TicTacToeActivity::class.java)
        startActivity(intent)
    }
    private fun changeToCompassScreen() {
        val intent = Intent(this, CompassActivity::class.java)
        startActivity(intent)
    }

    private fun changeToSportChallenges() {

        val intent = Intent(this, SelectSportModeActivity::class.java)
        startActivity(intent)
    }

    private fun requestPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("STEP", "Permission Request")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 1337)
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "CodeCampTeamA"
            val descriptionText = "CodeCampTeamA game app"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constant.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}