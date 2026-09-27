package com.maria.movierank.network.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [
    NetworkModule::class,
    LoggingModule::class
])
interface NetworkComponent : NetworkDependencies {

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance context: Context,
            @BindsInstance @Named("api_key") apiKey: String,
            @BindsInstance @Named("base_url") baseUrl: String
        ): NetworkComponent
    }
}