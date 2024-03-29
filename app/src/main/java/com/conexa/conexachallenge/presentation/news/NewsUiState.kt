package com.conexa.conexachallenge.presentation.news

import com.conexa.conexachallenge.data.api.model.response.posts.NewsByIdResponse
import com.conexa.conexachallenge.data.api.model.response.posts.NewsResponse
import com.conexa.conexachallenge.util.BindingString

data class NewsUiState(
    val news: List<NewsResponse> = emptyList(),
    val newsById: NewsByIdResponse? = null,
    val loading: Boolean = false,
    val userMessage: String? = null,
    val errorMessage: BindingString? = null,
)