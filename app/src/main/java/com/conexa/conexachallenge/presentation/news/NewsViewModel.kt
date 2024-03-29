package com.conexa.conexachallenge.presentation.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conexa.conexachallenge.data.api.model.response.posts.NewsResponse
import com.conexa.conexachallenge.domain.usecase.report.GetNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.conexa.conexachallenge.domain.model.ResultNews
import com.conexa.conexachallenge.domain.usecase.report.GetNewsByIdUseCase
import com.conexa.conexachallenge.util.errorMessage
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase,
    private val getNewsByIdUseCase: GetNewsByIdUseCase
) : ViewModel() {
    private val _news = MutableStateFlow<List<NewsResponse>>(emptyList())
    val news: StateFlow<List<NewsResponse>> get() = _news
    private var fetchJob: Job? = null
    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    fun fetchNews() {
        setLoading(true)
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val result = getNewsUseCase()
                when (result) {
                    is ResultNews.Success -> {
                        _uiState.update {
                            it.copy(news = result.data)
                        }
                    }

                    is ResultNews.Error -> {
                        _uiState.update {
                            it.copy(errorMessage = result.exception.errorMessage())
                        }
                        _uiState.value = NewsUiState(errorMessage = result.exception.errorMessage())
                    }
                }
            } catch (e: Exception) {
                _uiState.value = NewsUiState(userMessage = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(loading = false)
            }
        }
    }

    fun getNewById(newsId: Int) {
        setLoading(true)
        viewModelScope.launch {
            try {
                val result = getNewsByIdUseCase(newsId)
                when (result) {
                    is ResultNews.Success -> {
                        _uiState.value = NewsUiState(newsById = result.data)
                    }
                    is ResultNews.Error -> {
                        _uiState.value = NewsUiState(errorMessage = result.exception.errorMessage())
                    }
                }
            } catch (e: Exception) {
                _uiState.value = NewsUiState(userMessage = e.message)
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        _uiState.update {
            it.copy(loading = loading)
        }
    }

}
