package de.uniks.ws2122.cc.teamA.statistic

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.R
import de.uniks.ws2122.cc.teamA.databinding.ActivityStatisticBinding

class StatisticActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatisticBinding
    private lateinit var pieChartArithmetic : PieChart
    private lateinit var pieChartTicTacToe: PieChart
    private lateinit var pieChartCompassGame: PieChart
    private lateinit var pieChartSortChallenge: PieChart
    private lateinit var viewModel: StatisticViewModel
    private lateinit var toHistorieBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pieChartTicTacToe = binding.pieChartTicTacToe
        pieChartArithmetic = binding.pieChartMentalArithmetic
        pieChartCompassGame = binding.pieChartCompassGame
        pieChartSortChallenge = binding.pieChartSportChallenge
        toHistorieBtn = binding.btnToHistorie

        // Create ViewModel
        viewModel = ViewModelProvider(this)[StatisticViewModel::class.java]

        // Change to history
        toHistorieBtn.setOnClickListener {
            val intent = Intent(this, HistorieActivity::class.java).apply {  }
            startActivity(intent)
        }

        // Set all game statistics
        setTicTacToeDataToPieChart()
        setArithmeticDataToPieChart()
        setCompassGameDataToPieChart()
        setSportChallengeDataToPieChart()
    }

    private fun setTicTacToeDataToPieChart() {
        // Fetch TicTacToe statistic from database
        viewModel.fetchTicTacToeStatistic(){ statistic ->
            if (statistic != null){
                // Init some values for your pie chart
                pieChartTicTacToe.setUsePercentValues(false)
                pieChartTicTacToe.isRotationEnabled = false
                pieChartTicTacToe.legend.orientation = Legend.LegendOrientation.VERTICAL
                pieChartTicTacToe.legend.isWordWrapEnabled = true
                pieChartTicTacToe.legend.textColor = Color.WHITE
                pieChartTicTacToe.description.text = Constant.TTT
                pieChartTicTacToe.description.textColor = Color.WHITE
                pieChartTicTacToe.setBackgroundResource(R.color.piechart)
                pieChartTicTacToe.isDrawHoleEnabled = false
                pieChartTicTacToe.setTouchEnabled(false)
                pieChartTicTacToe.setDrawEntryLabels(false)

                // Create data entries that you want show in your pie chart
                val dataEntries = ArrayList<PieEntry>()
                dataEntries.add(PieEntry(statistic.wins.toFloat(), Constant.WIN))
                dataEntries.add(PieEntry(statistic.loses.toFloat(), Constant.LOSE))
                dataEntries.add(PieEntry(statistic.draws.toFloat(), Constant.DRAW))

                // Choose color for your entries
                val colors: ArrayList<Int> = ArrayList()
                colors.add(Color.parseColor("#05930A"))
                colors.add(Color.parseColor("#E82C27"))
                colors.add(Color.parseColor("#4D4F51"))

                val dataSet = PieDataSet(dataEntries, "")
                val data = PieData(dataSet)

                // Value in integer format
                data.setValueFormatter(MyValueFormatter())
                dataSet.sliceSpace = 2f
                dataSet.colors = colors
                pieChartTicTacToe.data = data
                data.setValueTextSize(15f)
                pieChartTicTacToe.setExtraOffsets(5f, 10f, 5f, 5f)
                pieChartTicTacToe.animateY(1400, Easing.EaseInOutQuad)

                pieChartTicTacToe.invalidate()
            }
        }
    }

    private fun setArithmeticDataToPieChart() {
        // Fetch MentalArithmetic statistic from database
        viewModel.fetchMentalArithmeticStatistic(){ statistic ->
            if (statistic != null){
                // Init some values for your pie chart
                pieChartArithmetic.setUsePercentValues(false)
                pieChartArithmetic.isRotationEnabled = false
                pieChartArithmetic.legend.orientation = Legend.LegendOrientation.VERTICAL
                pieChartArithmetic.legend.isWordWrapEnabled = true
                pieChartArithmetic.legend.textColor = Color.WHITE
                pieChartArithmetic.description.text = Constant.MENTALARITHMETIC
                pieChartArithmetic.description.textColor = Color.WHITE
                pieChartArithmetic.setBackgroundResource(R.color.piechart)
                pieChartArithmetic.isDrawHoleEnabled = false
                pieChartArithmetic.setTouchEnabled(false)
                pieChartArithmetic.setDrawEntryLabels(false)

                // Create data entries that you want show in your pie chart
                val dataEntries = ArrayList<PieEntry>()
                dataEntries.add(PieEntry(statistic.wins.toFloat(), Constant.WIN))
                dataEntries.add(PieEntry(statistic.loses.toFloat(), Constant.LOSE))
                dataEntries.add(PieEntry(statistic.draws.toFloat(), Constant.DRAW))

                // Choose color for your entries
                val colors: ArrayList<Int> = ArrayList()
                colors.add(Color.parseColor("#05930A"))
                colors.add(Color.parseColor("#E82C27"))
                colors.add(Color.parseColor("#4D4F51"))

                val dataSet = PieDataSet(dataEntries, "")
                val data = PieData(dataSet)

                // Value in integer format
                data.setValueFormatter(MyValueFormatter())
                dataSet.sliceSpace = 2f
                dataSet.colors = colors
                pieChartArithmetic.data = data
                data.setValueTextSize(15f)
                pieChartArithmetic.setExtraOffsets(5f, 10f, 5f, 5f)
                pieChartArithmetic.animateY(1400, Easing.EaseInOutQuad)

                pieChartArithmetic.invalidate()
            }
        }
    }

    private fun setCompassGameDataToPieChart() {
        // Fetch CompassGame statistic from database
        viewModel.fetchCompassGameStatistic(){ statistic ->
            if (statistic != null){
                // Init some values for your pie chart
                pieChartCompassGame.setUsePercentValues(false)
                pieChartCompassGame.isRotationEnabled = false
                pieChartCompassGame.legend.orientation = Legend.LegendOrientation.VERTICAL
                pieChartCompassGame.legend.isWordWrapEnabled = true
                pieChartCompassGame.legend.textColor = Color.WHITE
                pieChartCompassGame.description.text = Constant.COMPASS_GAME
                pieChartCompassGame.description.textColor = Color.WHITE
                pieChartCompassGame.setBackgroundResource(R.color.piechart)
                pieChartCompassGame.isDrawHoleEnabled = false
                pieChartCompassGame.setTouchEnabled(false)
                pieChartCompassGame.setDrawEntryLabels(false)

                // Create data entries that you want show in your pie chart
                val dataEntries = ArrayList<PieEntry>()
                dataEntries.add(PieEntry(statistic.wins.toFloat(), Constant.WIN))
                dataEntries.add(PieEntry(statistic.loses.toFloat(), Constant.LOSE))
                dataEntries.add(PieEntry(statistic.draws.toFloat(), Constant.DRAW))

                // Choose color for your entries
                val colors: ArrayList<Int> = ArrayList()
                colors.add(Color.parseColor("#05930A"))
                colors.add(Color.parseColor("#E82C27"))
                colors.add(Color.parseColor("#4D4F51"))

                val dataSet = PieDataSet(dataEntries, "")
                val data = PieData(dataSet)

                // Value in integer format
                data.setValueFormatter(MyValueFormatter())
                dataSet.sliceSpace = 2f
                dataSet.colors = colors
                pieChartCompassGame.data = data
                data.setValueTextSize(15f)
                pieChartCompassGame.setExtraOffsets(5f, 10f, 5f, 5f)
                pieChartCompassGame.animateY(1400, Easing.EaseInOutQuad)

                pieChartCompassGame.invalidate()
            }
        }
    }

    private fun setSportChallengeDataToPieChart() {
        // Fetch SportChallenge statistic from database
        viewModel.fetchSportChallengeStatistic(){ statistic ->
            if (statistic != null){
                // Init some values for your pie chart
                pieChartSortChallenge.setUsePercentValues(false)
                pieChartSortChallenge.isRotationEnabled = false
                pieChartSortChallenge.legend.orientation = Legend.LegendOrientation.VERTICAL
                pieChartSortChallenge.legend.isWordWrapEnabled = true
                pieChartSortChallenge.legend.textColor = Color.WHITE
                pieChartSortChallenge.description.text = Constant.SPORT_CHALLENGE
                pieChartSortChallenge.description.textColor = Color.WHITE
                pieChartSortChallenge.setBackgroundResource(R.color.piechart)
                pieChartSortChallenge.isDrawHoleEnabled = false
                pieChartSortChallenge.setTouchEnabled(false)
                pieChartSortChallenge.setDrawEntryLabels(false)

                // Create data entries that you want show in your pie chart
                val dataEntries = ArrayList<PieEntry>()
                dataEntries.add(PieEntry(statistic.wins.toFloat(), Constant.WIN))
                dataEntries.add(PieEntry(statistic.loses.toFloat(), Constant.LOSE))
                dataEntries.add(PieEntry(statistic.draws.toFloat(), Constant.DRAW))

                // Choose color for your entries
                val colors: ArrayList<Int> = ArrayList()
                colors.add(Color.parseColor("#05930A"))
                colors.add(Color.parseColor("#E82C27"))
                colors.add(Color.parseColor("#4D4F51"))

                val dataSet = PieDataSet(dataEntries, "")
                val data = PieData(dataSet)

                // Value in integer format
                data.setValueFormatter(MyValueFormatter())
                dataSet.sliceSpace = 2f
                dataSet.colors = colors
                pieChartSortChallenge.data = data
                data.setValueTextSize(15f)
                pieChartSortChallenge.setExtraOffsets(5f, 10f, 5f, 5f)
                pieChartSortChallenge.animateY(1400, Easing.EaseInOutQuad)

                pieChartSortChallenge.invalidate()
            }
        }
    }
}