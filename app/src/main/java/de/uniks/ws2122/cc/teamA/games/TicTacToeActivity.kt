package de.uniks.ws2122.cc.teamA.games

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import de.uniks.ws2122.cc.teamA.MainActivity
import de.uniks.ws2122.cc.teamA.R
import kotlin.collections.ArrayList

class TicTacToeActivity : AppCompatActivity() {
    private var fields: ArrayList<TextView> = ArrayList()
    private lateinit var statusText: TextView

    private var currentPlayer = "X"
    private var phase = "playing"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tictactoe)

        statusText = findViewById(R.id.statusText)
        statusText.text = "Spieler X ist an der Reihe"

        fields.add(findViewById(R.id.field00))
        fields.add(findViewById(R.id.field01))
        fields.add(findViewById(R.id.field02))

        fields.add(findViewById(R.id.field10))
        fields.add(findViewById(R.id.field11))
        fields.add(findViewById(R.id.field12))

        fields.add(findViewById(R.id.field20))
        fields.add(findViewById(R.id.field21))
        fields.add(findViewById(R.id.field22))

        for (i in 0..8) {
            fields[i].setOnClickListener {
                onFieldClicked(fields[i])
            }
        }
    }

    private fun onFieldClicked(field: TextView) {
        if (field.text.isEmpty() and phase.equals("playing")) {
            field.text = currentPlayer
            currentPlayer = if (currentPlayer.equals("X")) "O" else "X"
            statusText.text = "Spieler " + currentPlayer + " ist an der Reihe"
            checkWinner()
        }
        if (!phase.equals("playing")) {
            resetGame()
            return
        }
        if (fields.all { it.text.isNotEmpty() }) {
            phase = "draw"
            statusText.text = "Unentschieden"
        }
    }

    private fun checkWinner() {
        //Schauen, ob field leer ist
        var winner = ""
        for (i in 0..2) {
            //Row
            if (fields[0 + (3 * i)].text.equals(fields[1 + (3 * i)].text) and
                fields[1 + (3 * i)].text.equals(fields[2 + (3 * i)].text)
            )
                winner += fields[0 + (3 * i)].text.toString()
            //Col
            if (fields[0 + i].text.equals(fields[3 + i].text) and
                fields[3 + i].text.equals(fields[6 + i].text)
            ) winner += fields[0 + i].text.toString()
        }
        //Diagonalen
        if (fields[0].text.equals(fields[4].text) and fields[4].text.equals(fields[8].text)
        ) winner += fields[4].text.toString()
        if (fields[2].text.equals(fields[4].text) and fields[4].text.equals(fields[6].text)
        ) winner += fields[4].text.toString()

        if (winner.isNotEmpty()) {
            statusText.text = "Spieler " + winner + " hat gewonnen"
            phase = "win"
        }
    }


    private fun resetGame() {
        phase = "playing"
        for (i in 0..8) {
            fields[i].text = ""
        }
        statusText.text = "Spieler X ist an der Reihe"
        currentPlayer = "X"
    }

}