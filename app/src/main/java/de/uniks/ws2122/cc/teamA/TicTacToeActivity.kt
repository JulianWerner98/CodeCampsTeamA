package de.uniks.ws2122.cc.teamA

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.databinding.ActivityTicTacToeBinding
import de.uniks.ws2122.cc.teamA.model.TicTacToe
import de.uniks.ws2122.cc.teamA.model.TicTacToeViewModel

class TicTacToeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicTacToeBinding
    private lateinit var viewModel: TicTacToeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicTacToeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //view model
        viewModel = ViewModelProvider(this)[TicTacToeViewModel::class.java]
        viewModel.setTicTacToeData(TicTacToe())

        val buttons = initButtons()
        createTicTacToeDataObserver(buttons)

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

                if(data!!.fields[index] == '_'){

                    viewModel.endTurn(index)
                }
            }
        }

        return buttons
    }

    private fun createTicTacToeDataObserver(buttons: List<ImageButton>) {

        Log.d("TAG", "ping")

        viewModel.getTicTacToeData().observe(this, { tictactoe ->

            if (tictactoe.isMyTurn) {

                binding.tvTurnMessage.text = "Your turn"
            } else {

                binding.tvTurnMessage.text = "Enemy turn"

                buttons.forEach { button ->
                    button.isClickable = false
                }
            }

            val myIcon: Int
            val enemyIcon: Int

            if (tictactoe.isCircle) {

                myIcon = R.drawable.common_full_open_on_phone
                enemyIcon = R.drawable.ic_launcher_background
            } else {

                myIcon = R.drawable.ic_launcher_background
                enemyIcon = R.drawable.common_full_open_on_phone
            }

            var counter: Int = 0
            buttons.forEach { button ->

                when (tictactoe.fields[counter]) {

                    'o' -> button.setImageResource(myIcon)
                    'x' -> button.setImageResource(enemyIcon)
                }

                counter++
            }
        })
    }
}

