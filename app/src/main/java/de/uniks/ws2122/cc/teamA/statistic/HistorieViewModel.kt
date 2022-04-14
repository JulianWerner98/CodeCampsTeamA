package de.uniks.ws2122.cc.teamA.statistic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.model.MatchResult
import de.uniks.ws2122.cc.teamA.repository.StatisticRepository

class HistorieViewModel: ViewModel() {
    private var matchResultList = mutableListOf<MatchResult>()

    // Livedata
    private var matchResultLiveData = MutableLiveData<List<MatchResult>>()

    // Repo
    private var statisticRepository = StatisticRepository()

    init {
        matchResultLiveData.value = matchResultList
    }

    // Setter
    fun setLiveMatchResultListData(){
        matchResultLiveData.value = matchResultList
    }

    // Getter
    fun getLiveMatchResultListData() : MutableLiveData<List<MatchResult>>{
        return matchResultLiveData
    }

    // Fetch history list
    fun fetchHistorieList() {
        statisticRepository.fetchHistorieList(){ matchList ->
            matchResultList = matchList
            setLiveMatchResultListData()
        }
    }
}