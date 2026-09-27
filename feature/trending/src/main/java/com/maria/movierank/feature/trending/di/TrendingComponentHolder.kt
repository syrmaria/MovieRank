package com.maria.movierank.feature.trending.di

import com.maria.movierank.network.di.NetworkDependencies

object TrendingComponentHolder {

    lateinit var component: TrendingComponent
        private set

    fun init(networkDependencies: NetworkDependencies) {
        component = DaggerTrendingComponent.factory()
            .create(networkDependencies)
    }
}