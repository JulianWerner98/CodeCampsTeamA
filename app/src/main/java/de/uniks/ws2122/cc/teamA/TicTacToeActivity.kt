package de.uniks.ws2122.cc.teamA

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.databinding.ActivityTicTacToeBinding
import de.uniks.ws2122.cc.teamA.model.AppViewModel
import de.uniks.ws2122.cc.teamA.model.TicTacToeViewModel

class TicTacToeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var appViewModel: AppViewModel
    private lateinit var binding: ActivityTicTacToeBinding
    private lateinit var viewModel: TicTacToeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicTacToeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //view model
        viewModel = ViewModelProvider(this)[TicTacToeViewModel::class.java]
        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]

        val buttons = initButtons()
        createTicTacToeDataObserver(buttons)

        binding.surrenderBtn.setOnClickListener(this)
        binding.surrenderBtn.isEnabled = false

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

                val data = viewModel.getTicTacToeData().value

                if (data!!.fields[index] == '_') {

                    viewModel.endTurn(index)
                }
            }
        }

        return buttons
    }

    private fun createTicTacToeDataObserver(buttons: List<ImageButton>) {

        viewModel.getTicTacToeData().observe(this, { tictactoe ->

            Log.d("TTTActivity", tictactoe.players.toString())

            if (tictactoe.players.size < 2) {

                binding.tvTurnMessage.text = "Waiting for Player"
                buttons.forEach { button ->
                    button.isClickable = false
                }

            } else {
                binding.surrenderBtn.isEnabled = true
                if (tictactoe.isMyTurn) {

                    binding.tvTurnMessage.text = "Your turn"

                    buttons.forEach { button ->
                        button.isClickable = true
                    }

                } else {
                    binding.tvTurnMessage.text = "${tictactoe.players[1]} turn"

                    buttons.forEach { button ->
                        button.isClickable = false
                    }
                }

                if (tictactoe.winner.isNotEmpty()) {
                    if(tictactoe.fields.equals("xxxxxxxxx") && !tictactoe.isCircle ||
                        tictactoe.fields.equals("ooooooooo") && tictactoe.isCircle){
                        binding.tvTurnMessage.text = "Enemy surrender"
                    }
                    else if(tictactoe.fields.equals("ooooooooo") && !tictactoe.isCircle ||
                        tictactoe.fields.equals("xxxxxxxxx") && tictactoe.isCircle) {
                        binding.tvTurnMessage.text = "You surrender"
                    } else {
                        binding.tvTurnMessage.text = "${tictactoe.players[1]} won"
                    }

                    buttons.forEach { button ->
                        button.isClickable = false
                    }
                }
            }

            val myIcon: Int
            val enemyIcon: Int

            if (tictactoe.isCircle) {

                myIcon = R.drawable.circle
                enemyIcon = R.drawable.cross
            } else {

                myIcon = R.drawable.cross
                enemyIcon = R.drawable.circle
            }

            var counter = 0
            buttons.forEach { button ->

                when (tictactoe.fields[counter]) {
                    'o' -> button.setImageResource(myIcon)
                    'x' -> button.setImageResource(enemyIcon)
                }

                counter++
            }
        })
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id){
                binding.surrenderBtn.id -> viewModel.surrenderGame()
            }
        }

    }
}

