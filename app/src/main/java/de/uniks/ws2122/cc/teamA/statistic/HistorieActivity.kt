package de.uniks.ws2122.cc.teamA.statistic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.uniks.ws2122.cc.teamA.databinding.ActivityHistorieBinding

class HistorieActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistorieBinding
    private lateinit var rvHistorie: RecyclerView
    private lateinit var myHistorieAdapter: MyHistorieAdapter
    private lateinit var viewModel: HistorieViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistorieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvHistorie = binding.rvHistoreList
        rvHistorie.layoutManager = LinearLayoutManager(this)
        rvHistorie.setHasFixedSize(true)

        viewModel = ViewModelProvider(this)[HistorieViewModel::class.java]

        myHistorieAdapter = MyHistorieAdapter(viewModel.getLiveMatchResultListData())

        viewModel.fetchHistorieList()

        viewModel.getLiveMatchResultListData().observe(this, Observer {
            rvHistorie.adapter = myHistorieAdapter
        })
    }
}