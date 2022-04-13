package de.uniks.ws2122.cc.teamA.statistic

import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.model.Highscore
import de.uniks.ws2122.cc.teamA.repository.StatisticRepository

class StatisticViewModel: ViewModel() {
    // Repo
    private var statisticRepository = StatisticRepository()

    fun fetchTicTacToeStatistic(callback: (result: Highscore?) -> Unit) {
        statisticRepository.fetchTicTacToeStatistic(){ statistic ->
            callback.invoke(statistic)
        }
    }

    fun fetchMentalArithmeticStatistic(callback: (result: Highscore?) -> Unit) {
        statisticRepository.fetchMentalArithmeticStatistic(){ statistic ->
            callback.invoke(statistic)
        }
    }

    fun fetchCompassGameStatistic(callback: (result: Highscore?) -> Unit) {
        statisticRepository.fetchCompassGameStatistic(){ statistic ->
            callback.invoke(statistic)
        }
    }

    fun fetchSportChallengeStatistic(callback: (result: Highscore?) -> Unit) {
        statisticRepository.fetchSportChallengeStatistic { statistic ->
            callback.invoke(statistic)
        }
    }
}