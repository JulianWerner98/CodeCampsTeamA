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
    private lateinit var nicknameText: TextView
    private lateinit var mentalArithmeticBtn : Button
    private lateinit var gameInviteListBtn : Button

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
        mentalArithmeticBtn = binding.btnMentalArithmetic
        gameInviteListBtn = binding.btnGameInviteList

        tttBtn.setOnClickListener(this)
        friendlistBtn.setOnClickListener(this)
        logoutBtn.setOnClickListener(this)
        mentalArithmeticBtn.setOnClickListener(this)
        gameInviteListBtn.setOnClickListener(this)

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
            mentalArithmeticBtn.id -> changeToMentalArithmetic()
            gameInviteListBtn.id -> changeToGameInviteList()
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
}