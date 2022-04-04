package de.uniks.ws2122.cc.teamA

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.databinding.ActivityGameSelectBinding
import de.uniks.ws2122.cc.teamA.friendlist.FriendListActivity
import de.uniks.ws2122.cc.teamA.model.AppViewModel

class GameSelectActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var viewModel: AppViewModel
    private lateinit var binding: ActivityGameSelectBinding
    private lateinit var logoutBtn: Button
    private lateinit var spinner: ProgressBar
    private lateinit var friendlistBtn: Button
    private lateinit var tttBtn: Button
    private lateinit var nicknameText: TextView
    private lateinit var sportBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logoutBtn = binding.btnLogout
        spinner = binding.spinner
        spinner.isVisible = false
        friendlistBtn = binding.btnFriedlist
        tttBtn = binding.tttBtn
        nicknameText = binding.TVnickname
        sportBtn = binding.btnSportChallenges

        tttBtn.setOnClickListener(this)
        friendlistBtn.setOnClickListener(this)
        logoutBtn.setOnClickListener(this)
        sportBtn.setOnClickListener(this)

        viewModel = ViewModelProvider(this)[AppViewModel::class.java]

        viewModel.getLiveValueUser().observe(this) { user ->
            nicknameText.text = user.nickname
        }

        requestPermissions()
    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            logoutBtn.id -> logout()
            tttBtn.id -> changeToTicTacToeScreen()
            friendlistBtn.id -> changeToFriendslist()
            sportBtn.id -> changeToSportChallenges()
        }
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
}