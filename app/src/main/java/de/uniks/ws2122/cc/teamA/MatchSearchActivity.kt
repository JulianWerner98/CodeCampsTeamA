package de.uniks.ws2122.cc.teamA

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.uniks.ws2122.cc.teamA.databinding.ActivityMatchSearchBinding
import de.uniks.ws2122.cc.teamA.repository.TicTacToeRepository

class MatchSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMatchSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSearch.setOnClickListener {

            val intent = Intent(this, TicTacToeActivity::class.java)
            startActivity(intent)
        }
    }
}