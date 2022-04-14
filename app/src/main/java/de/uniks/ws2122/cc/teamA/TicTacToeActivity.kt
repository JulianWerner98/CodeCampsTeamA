package de.uniks.ws2122.cc.teamA

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.Constant.DRAW
import de.uniks.ws2122.cc.teamA.Constant.FRIENDID
import de.uniks.ws2122.cc.teamA.Constant.INVITEKEY
import de.uniks.ws2122.cc.teamA.databinding.ActivityTicTacToeBinding
import de.uniks.ws2122.cc.teamA.model.AppViewModel
import de.uniks.ws2122.cc.teamA.model.ticTacToe.TicTacToeViewModel
import de.uniks.ws2122.cc.teamA.model.util.Notifications

class TicTacToeActivity : AppCompatActivity() {

    private var inviteId: String? = null
    private var friendId: String? = null
    private lateinit var appViewModel: AppViewModel
    private lateinit var binding: ActivityTicTacToeBinding
    private lateinit var viewModel: TicTacToeViewModel
    private val notificationId = 6541968

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicTacToeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Get Intent Extras
        friendId = intent.extras?.get(FRIENDID)?.toString()
        if (friendId == "null") friendId = null
        inviteId = intent.extras?.get(INVITEKEY)?.toString()
        if (inviteId == "null") inviteId = null

        //view model
        viewModel = ViewModelProvider(this)[TicTacToeViewModel::class.java]
        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]

        //Load or Create Game
        viewModel.getOrCreateGame(friendId, inviteId) { text ->
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }

        //Init Buttons
        val buttons = initButtons()
        createTicTacToeDataObserver(buttons)

        //Set Listeners and Visibility
        binding.surrenderBtn.setOnClickListener { viewModel.surrenderGame(appViewModel) }
        binding.surrenderBtn.isEnabled = false

        binding.smiley.isVisible = false
        binding.symbole.isVisible = false
    }

    /**
     * Override the onBackPressed Function
     * Used to save statics and history and delete private game
     * */
    override fun onBackPressed() {
        if (viewModel.getTicTacToeData().value!!.winner.isNotEmpty()) {
            exitGame()
        }
        if (friendId != null && viewModel.getTicTacToeData().value!!.players.size < 2) {
            viewModel.deleteGame() {
                viewModel.deleteInvite(friendId!!) {
                    Toast.makeText(this, "Delete Game and Request", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onBackPressed()
    }

    /** Save statics and history
     *  Change to Game Select Screen
     * */
    private fun exitGame() {
        viewModel.exitGame()
        val intent = Intent(this, GameSelectActivity::class.java).apply { }
        startActivity(intent)
    }

    /** Bind Buttons to Variables and Set Listeners
     **/
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
        // Set Listeners to all Field buttons
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                viewModel.turn(index)
            }
        }
        return buttons
    }

    /** Listen to the current TTT Object
     * */
    private fun createTicTacToeDataObserver(buttons: List<ImageButton>) {
        //Setup for Notification
        val intent = Intent(this, TicTacToeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notifications = Notifications()

        //Set observer to Livedata
        viewModel.getTicTacToeData().observe(this) { tictactoe ->

            //Waiting for opponent
            if (tictactoe.players.size < 2) {
                binding.tvTurnMessage.text = "Waiting for Player"
                buttons.forEach { button ->
                    button.isVisible = false
                }
                binding.spinner.isVisible = true

            } else { //In game
                //Set showing elements visibility
                binding.spinner.isVisible = false
                binding.surrenderBtn.isEnabled = true
                binding.symbole.isVisible = true
                if (viewModel.isCircle()) {
                    binding.symbole.setImageResource(R.drawable.circle)
                } else {
                    binding.symbole.setImageResource(R.drawable.cross)
                }
                //There is a winner
                if (tictactoe.winner.isNotEmpty()) {
                    //Change Button listener and visibilities
                    binding.surrenderBtn.isEnabled = true
                    binding.surrenderBtn.text = "Exit Game"
                    binding.surrenderBtn.setOnClickListener { exitGame() }
                    binding.symbole.isVisible = false
                    binding.smiley.isVisible = true
                    when (tictactoe.winner) {
                        //The current player won
                        appViewModel.getUID() -> {
                            notifications.sendNotification(
                                notificationId, Constant.TTT, "You won", this, pendingIntent
                            )
                            binding.tvTurnMessage.text = "You won"
                            binding.smiley.setImageResource(R.drawable.happy)
                        }
                        //It´s a draw
                        DRAW -> {
                            viewModel.getNameById(appViewModel) {
                                notifications.sendNotification(
                                    notificationId, Constant.TTT, "Draw", this, pendingIntent
                                )
                                binding.tvTurnMessage.text = "Draw"
                                binding.smiley.setImageResource(R.drawable.neutral)
                            }
                        }
                        //Opponent won
                        else -> {
                            viewModel.getNameById(appViewModel) {
                                notifications.sendNotification(
                                    notificationId, Constant.TTT, "You lose", this, pendingIntent
                                )
                                binding.tvTurnMessage.text = "${it} won"
                                binding.smiley.setImageResource(R.drawable.lame)
                            }
                        }
                    }
                    //Hide Buttons
                    buttons.forEach { button ->
                        button.isVisible = false
                    }
                // No winner
                } else {
                    // Set Labels and button status
                    if (tictactoe.turn == appViewModel.getUID()) {
                        notifications.sendNotification(
                            notificationId, Constant.TTT, "Your turn", this, pendingIntent
                        )
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
            //Show the correct symbol in button
            buttons.forEachIndexed { index, button ->
                when (tictactoe.fields[index]) {
                    "o" -> button.setImageResource(R.drawable.circle)
                    "x" -> button.setImageResource(R.drawable.cross)
                }
            }
        }
    }
}

