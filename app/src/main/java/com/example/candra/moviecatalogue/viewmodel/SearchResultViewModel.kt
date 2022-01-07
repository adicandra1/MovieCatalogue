package com.example.candra.moviecatalogue.viewmodel

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.candra.data.api.ApiRepository
import kotlinx.coroutines.launch

class SearchResultViewModel : ViewModel() {

    private val apiRepository = ApiRepository()
    val searchResult = Transformations.map(apiRepository.getSearchResultLiveData()) { it }

    fun loadSearchResult(dataType: Int, query: String) {
        viewModelScope.launch { apiRepository.loadSearchResult(dataType, query) }
    }
}