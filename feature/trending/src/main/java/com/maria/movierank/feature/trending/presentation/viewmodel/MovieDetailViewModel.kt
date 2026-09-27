package com.maria.movierank.feature.trending.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maria.movierank.feature.trending.domain.usecase.GetMovieDetailUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieDetailViewModel @AssistedInject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    @Assisted private val movieId: Int
) : ViewModel() {

    private val _state = MutableStateFlow(MovieDetailState())
    val state: StateFlow<MovieDetailState> = _state.asStateFlow()

    init {
        loadDetail()
    }

    fun onEvent(event: MovieDetailEvent) {
        when (event) {
            is MovieDetailEvent.Retry -> loadDetail()
        }
    }

    private fun loadDetail() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val detail = getMovieDetailUseCase(movieId)
                _state.update { it.copy(isLoading = false, movieDetail = detail) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Failed to load details") }
            }
        }
    }
}
