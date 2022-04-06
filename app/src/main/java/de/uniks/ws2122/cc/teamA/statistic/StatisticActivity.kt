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
import com.github.mikephil.charting.formatter.PercentFormatter
import de.uniks.ws2122.cc.teamA.Constant
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

        viewModel = ViewModelProvider(this)[StatisticViewModel::class.java]

        toHistorieBtn.setOnClickListener {
            val intent = Intent(this, HistorieActivity::class.java).apply {  }
            startActivity(intent)
        }
        
        setTicTacToeDataToPieChart()
        setArithmeticDataToPieChart()
        setCompassGameDataToPieChart()
        setSportChallengeDataToPieChart()
    }

    private fun setTicTacToeDataToPieChart() {
        viewModel.fetchTicTacToeStatistic(){ statistic ->
            if (statistic != null){
                // Init some values for your pie chart
                pieChartTicTacToe.setUsePercentValues(false)
                pieChartTicTacToe.isRotationEnabled = false
                pieChartTicTacToe.legend.orientation = Legend.LegendOrientation.VERTICAL
                pieChartTicTacToe.legend.isWordWrapEnabled = true
                pieChartTicTacToe.description.text = ""

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

                // In Percentage
                data.setValueFormatter(PercentFormatter())
                dataSet.sliceSpace = 2f
                dataSet.colors = colors
                pieChartTicTacToe.data = data
                data.setValueTextSize(15f)
                pieChartTicTacToe.setExtraOffsets(5f, 10f, 5f, 5f)
                pieChartTicTacToe.animateY(1400, Easing.EaseInOutQuad)

                //create hole in center
                pieChartTicTacToe.holeRadius = 58f
                pieChartTicTacToe.transparentCircleRadius = 61f
                pieChartTicTacToe.isDrawHoleEnabled = true
                pieChartArithmetic.setTouchEnabled(false)
                pieChartArithmetic.setDrawEntryLabels(false)
                pieChartTicTacToe.setHoleColor(Color.WHITE)


                //add text in center
                pieChartTicTacToe.setDrawCenterText(true);
                pieChartTicTacToe.centerText = Constant.TTT

                pieChartTicTacToe.invalidate()
            }
        }
    }

    private fun setArithmeticDataToPieChart() {
        viewModel.fetchMentalArithmeticStatistic(){ statistic ->
            if (statistic != null){
                // Init some values for your pie chart
                pieChartArithmetic.setUsePercentValues(false)
                pieChartArithmetic.isRotationEnabled = false
                pieChartArithmetic.legend.orientation = Legend.LegendOrientation.VERTICAL
                pieChartArithmetic.legend.isWordWrapEnabled = true
                pieChartArithmetic.description.text = ""

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

                // In Percentage
                data.setValueFormatter(PercentFormatter())
                dataSet.sliceSpace = 2f
                dataSet.colors = colors
                pieChartArithmetic.data = data
                data.setValueTextSize(15f)
                pieChartArithmetic.setExtraOffsets(5f, 10f, 5f, 5f)
                pieChartArithmetic.animateY(1400, Easing.EaseInOutQuad)

                //create hole in center
                pieChartArithmetic.holeRadius = 58f
                pieChartArithmetic.transparentCircleRadius = 61f
                pieChartArithmetic.isDrawHoleEnabled = true
                pieChartArithmetic.setTouchEnabled(false)
                pieChartArithmetic.setDrawEntryLabels(false)
                pieChartArithmetic.setHoleColor(Color.WHITE)


                //add text in center
                pieChartArithmetic.setDrawCenterText(true);
                pieChartArithmetic.centerText = Constant.MENTALARITHMETIC

                pieChartArithmetic.invalidate()
            }
        }
    }

    private fun setCompassGameDataToPieChart() {
        viewModel.fetchCompassGameStatistic(){ statistic ->
            if (statistic != null){
                // Init some values for your pie chart
                pieChartCompassGame.setUsePercentValues(false)
                pieChartCompassGame.isRotationEnabled = false
                pieChartCompassGame.legend.orientation = Legend.LegendOrientation.VERTICAL
                pieChartCompassGame.legend.isWordWrapEnabled = true
                pieChartCompassGame.description.text = ""

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

                // In Percentage
                data.setValueFormatter(PercentFormatter())
                dataSet.sliceSpace = 2f
                dataSet.colors = colors
                pieChartCompassGame.data = data
                data.setValueTextSize(15f)
                pieChartCompassGame.setExtraOffsets(5f, 10f, 5f, 5f)
                pieChartCompassGame.animateY(1400, Easing.EaseInOutQuad)

                //create hole in center
                pieChartCompassGame.holeRadius = 58f
                pieChartCompassGame.transparentCircleRadius = 61f
                pieChartCompassGame.isDrawHoleEnabled = true
                pieChartArithmetic.setTouchEnabled(false)
                pieChartArithmetic.setDrawEntryLabels(false)
                pieChartCompassGame.setHoleColor(Color.WHITE)


                //add text in center
                pieChartCompassGame.setDrawCenterText(true);
                pieChartCompassGame.centerText = Constant.COMPASS_GAME

                pieChartCompassGame.invalidate()
            }
        }
    }

    private fun setSportChallengeDataToPieChart() {
        viewModel.fetchSportChallengeStatistic(){ statistic ->
            if (statistic != null){
                // Init some values for your pie chart
                pieChartSortChallenge.setUsePercentValues(false)
                pieChartSortChallenge.isRotationEnabled = false
                pieChartSortChallenge.legend.orientation = Legend.LegendOrientation.VERTICAL
                pieChartSortChallenge.legend.isWordWrapEnabled = true
                pieChartSortChallenge.description.text = ""

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

                // In Percentage
                data.setValueFormatter(PercentFormatter())
                dataSet.sliceSpace = 2f
                dataSet.colors = colors
                pieChartSortChallenge.data = data
                data.setValueTextSize(15f)
                pieChartSortChallenge.setExtraOffsets(5f, 10f, 5f, 5f)
                pieChartSortChallenge.animateY(1400, Easing.EaseInOutQuad)

                //create hole in center
                pieChartSortChallenge.holeRadius = 58f
                pieChartSortChallenge.transparentCircleRadius = 61f
                pieChartSortChallenge.isDrawHoleEnabled = true
                pieChartArithmetic.setTouchEnabled(false)
                pieChartArithmetic.setDrawEntryLabels(false)
                pieChartSortChallenge.setHoleColor(Color.WHITE)


                //add text in center
                pieChartSortChallenge.setDrawCenterText(true);
                pieChartSortChallenge.centerText = Constant.SPORT_CHALLENGE

                pieChartSortChallenge.invalidate()
            }
        }
    }
}