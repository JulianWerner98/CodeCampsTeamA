package de.uniks.ws2122.cc.teamA.gameInvite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.uniks.ws2122.cc.teamA.CompassActivity
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.TicTacToeActivity
import de.uniks.ws2122.cc.teamA.databinding.ActivityGameInviteListBinding
import de.uniks.ws2122.cc.teamA.mentalArithmetic.MentalArithmeticActivity

class GameInviteListActivity : AppCompatActivity(), MyInviteAdapter.OnItemClickListener {
    private lateinit var binding: ActivityGameInviteListBinding
    private lateinit var recyclerInviteView : RecyclerView
    private lateinit var myInviteAdapter: MyInviteAdapter
    private lateinit var viewModel: GameInviteViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameInviteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerInviteView = binding.rvGameInviteList
        recyclerInviteView.layoutManager = LinearLayoutManager(this)
        recyclerInviteView.setHasFixedSize(true)

        // Create ViewModel
        viewModel = ViewModelProvider(this)[GameInviteViewModel::class.java]

        // Create invite adapter
        myInviteAdapter = MyInviteAdapter(viewModel.getLiveGameInviteListData(), this)

        // Fetch invite list from database
        viewModel.fetchInvitesList()

        // Add observer on list
        viewModel.getLiveGameInviteListData().observe(this, Observer {
            recyclerInviteView.adapter = myInviteAdapter
        })
    }

    // If item is clicked, check game name and change to that game
    override fun onItemClicked(position: Int) {
        val gameInvite = viewModel.getLiveGameInviteListData().value!![position]
        val gameName = gameInvite.gameName
        val friendName = gameInvite.friendName
        val gameKey = gameInvite.gameKey
        viewModel.deleteInvite(gameName, friendName)
        when(gameName){
            Constant.MENTALARITHMETIC -> changeToArithmeticIntent(gameKey)
            Constant.COMPASS_GAME -> changeToCompassIntent(gameKey)
            Constant.TTT -> changeToTTTIntent(gameKey)
        }
    }

    // Change to TicTacToe
    private fun changeToTTTIntent(gameKey: String) {
        val intent = Intent(this, TicTacToeActivity::class.java).apply {
            this.putExtra(Constant.INVITEKEY, gameKey)
        }
        startActivity(intent)
    }

    // Change to CompassGame
    private fun changeToCompassIntent(gameKey: String) {
        val intent = Intent(this, CompassActivity::class.java).apply {
            this.putExtra(Constant.INVITEKEY, gameKey)
        }
        startActivity(intent)
    }

    // Change to MentalArithmetic
    private fun changeToArithmeticIntent(gameKey: String) {
        val intent = Intent(this, MentalArithmeticActivity::class.java).apply {
            this.putExtra(Constant.FRIENDID, Constant.DEFAULT)
            this.putExtra(Constant.MATCHTYP, Constant.PRIVATE)
            this.putExtra(Constant.INVITEKEY, gameKey)
        }
        startActivity(intent)
    }
}