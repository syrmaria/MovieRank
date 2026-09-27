package com.maria.movierank.feature.trending.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.AssistedFactory

@AssistedFactory
interface MovieDetailViewModelFactory {
    fun create(movieId: Int): MovieDetailViewModel
}

fun MovieDetailViewModelFactory.provideFactory(movieId: Int): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            create(movieId) as T
    }
