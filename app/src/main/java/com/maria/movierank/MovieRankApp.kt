package com.maria.movierank

import android.app.Application
import com.maria.movierank.feature.trending.di.TrendingComponentHolder
import com.maria.movierank.network.di.DaggerNetworkComponent
import com.maria.movierank.network.di.NetworkComponent

class MovieRankApp : Application() {

    lateinit var networkComponent: NetworkComponent
        private set

    override fun onCreate() {
        super.onCreate()

        networkComponent = DaggerNetworkComponent.factory().create(
            this,
            apiKey = BuildConfig.TMDB_API_KEY,
            baseUrl = BuildConfig.TMDB_BASE_URL
        )

        TrendingComponentHolder.init(networkComponent)
    }
}
