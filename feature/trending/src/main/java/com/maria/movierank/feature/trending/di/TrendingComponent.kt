package com.maria.movierank.feature.trending.di

import com.maria.movierank.feature.trending.presentation.viewmodel.MovieDetailViewModelFactory
import com.maria.movierank.feature.trending.presentation.viewmodel.MovieListViewModelFactory
import com.maria.movierank.network.di.NetworkDependencies
import dagger.Component

@TrendingScope
@Component(
    dependencies = [NetworkDependencies::class],
    modules = [TrendingModule::class]
)
interface TrendingComponent {

    fun movieListViewModelFactory(): MovieListViewModelFactory
    fun movieDetailViewModelFactory(): MovieDetailViewModelFactory

    @Component.Factory
    interface Factory {
        fun create(
            networkDependencies: NetworkDependencies
        ): TrendingComponent
    }
}