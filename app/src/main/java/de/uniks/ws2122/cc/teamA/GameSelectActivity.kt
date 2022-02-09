package de.uniks.ws2122.cc.teamA

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

        tttBtn.setOnClickListener(this)
        friendlistBtn.setOnClickListener(this)
        logoutBtn.setOnClickListener(this)

        viewModel = ViewModelProvider(this)[AppViewModel::class.java]

        viewModel.getLiveValueUser().observe(this, { user ->
            nicknameText.text = user.nickname
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            logoutBtn.id -> logout()
            tttBtn.id -> changeToTicTacToeScreen()
            friendlistBtn.id -> changeToFriendslist()
        }
    }

    private fun logout() {
        viewModel.logoutUser()
        val intent = Intent(this, MainActivity::class.java).apply { }
        startActivity(intent)
    }

    private fun changeToFriendslist() {
        val intent = Intent(this, FriendListActivity::class.java).apply { }
        startActivity(intent)
    }

    private fun changeToTicTacToeScreen() {
        TODO("Not yet implemented")
    }
}