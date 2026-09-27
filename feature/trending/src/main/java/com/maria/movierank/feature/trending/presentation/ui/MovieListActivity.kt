package com.maria.movierank.feature.trending.presentation.ui


import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.maria.movierank.design.MovieRankTheme
import com.maria.movierank.feature.trending.di.TrendingComponentHolder
import com.maria.movierank.feature.trending.presentation.viewmodel.MovieListViewModel

class MovieListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val trendingComponent = TrendingComponentHolder.component

        val movieListViewModel = ViewModelProvider(
            this,
            trendingComponent.movieListViewModelFactory()
        )[MovieListViewModel::class.java]

        setContent {
            MovieRankTheme {
                val state by movieListViewModel.state.collectAsStateWithLifecycle()
                MovieListScreen(
                    state = state,
                    onEvent = movieListViewModel::onEvent
                )
            }
        }
    }
}