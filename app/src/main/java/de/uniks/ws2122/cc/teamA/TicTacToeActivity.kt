package de.uniks.ws2122.cc.teamA

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.databinding.ActivityTicTacToeBinding
import de.uniks.ws2122.cc.teamA.model.AppViewModel
import de.uniks.ws2122.cc.teamA.model.ticTacToe.TicTacToeViewModel

class TicTacToeActivity : AppCompatActivity() {

    private var friendId: String? = null
    private lateinit var appViewModel: AppViewModel
    private lateinit var binding: ActivityTicTacToeBinding
    private lateinit var viewModel: TicTacToeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicTacToeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        friendId = intent.extras?.get(Constant.FRIENDID)?.toString()
        if (friendId == "null") friendId = null

        //view model
        viewModel = ViewModelProvider(this)[TicTacToeViewModel::class.java]
        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]

        viewModel.getOrCreateGame(friendId)

        val buttons = initButtons()
        createTicTacToeDataObserver(buttons)

        binding.surrenderBtn.setOnClickListener { viewModel.surrenderGame(appViewModel) }
        binding.surrenderBtn.isEnabled = false

        binding.smiley.isVisible = false
        binding.symbole.isVisible = false
    }

    override fun onBackPressed() {
        if (viewModel.getTicTacToeData().value!!.winner.isNotEmpty()) {
            exitGame()
        }
        super.onBackPressed()
    }

    private fun exitGame() {
        viewModel.exitGame()
        val intent = Intent(this, GameSelectActivity::class.java).apply { }
        startActivity(intent)
    }

    private fun initButtons(): List<ImageButton> {

        val buttons: List<ImageButton> = listOf(
            binding.ibtnField1,
            binding.ibtnField2,
            binding.ibtnField3,
            binding.ibtnField4,
            binding.ibtnField5,
            binding.ibtnField6,
            binding.ibtnField7,
            binding.ibtnField8,
            binding.ibtnField9
        )

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                viewModel.turn(index)
            }
        }
        return buttons
    }

    private fun createTicTacToeDataObserver(buttons: List<ImageButton>) {

        viewModel.getTicTacToeData().observe(this) { tictactoe ->

            if (tictactoe.players.size < 2) {
                binding.tvTurnMessage.text = "Waiting for Player"
                buttons.forEach { button ->
                    button.isVisible = false
                }
                binding.spinner.isVisible = true

            } else {
                binding.spinner.isVisible = false
                binding.surrenderBtn.isEnabled = true
                binding.symbole.isVisible = true
                if (viewModel.isCircle()) {
                    binding.symbole.setImageResource(R.drawable.circle)
                } else {
                    binding.symbole.setImageResource(R.drawable.cross)
                }
                if (tictactoe.winner.isNotEmpty()) {
                    binding.surrenderBtn.isEnabled = true
                    binding.surrenderBtn.text = "Exit Game"
                    binding.surrenderBtn.setOnClickListener { exitGame() }

                    binding.symbole.isVisible = false
                    if (tictactoe.winner == "draw") {
                        binding.tvTurnMessage.text = "Draw"
                    } else {
                        binding.smiley.isVisible = true
                        if (tictactoe.winner == appViewModel.getUID()) {
                            binding.tvTurnMessage.text = "You won"
                            binding.smiley.setImageResource(R.drawable.happy)
                        } else {
                            viewModel.getNameById(appViewModel) {
                                binding.tvTurnMessage.text = "${it} won"
                                binding.smiley.setImageResource(R.drawable.lame)
                            }
                        }
                    }
                    buttons.forEach { button ->
                        button.isVisible = false
                    }

                } else {
                    if (tictactoe.turn == appViewModel.getUID()) {
                        binding.tvTurnMessage.text = "Your turn"
                        buttons.forEach { button ->
                            button.isEnabled = true
                            button.isVisible = true
                        }
                    } else {
                        viewModel.getNameById(appViewModel) {
                            binding.tvTurnMessage.text = "${it}'s turn"
                        }
                        buttons.forEach { button ->
                            button.isEnabled = false
                            button.isVisible = true
                        }
                    }
                }

            }
            buttons.forEachIndexed { index, button ->
                when (tictactoe.fields[index]) {
                    "o" -> button.setImageResource(R.drawable.circle)
                    "x" -> button.setImageResource(R.drawable.cross)
                }
            }
        }
    }
}

