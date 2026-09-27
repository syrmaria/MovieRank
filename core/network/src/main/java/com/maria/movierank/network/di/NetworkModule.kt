package com.maria.movierank.network.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.maria.movierank.core.network.BuildConfig
import com.maria.movierank.network.data.api.SessionApiService
import com.maria.movierank.network.domain.repo.GuestSessionRepository
import com.maria.movierank.network.domain.repo.GuestSessionRepositoryImpl
import com.maria.movierank.network.domain.utils.SecurePreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
abstract class NetworkModule {

    @Binds
    abstract fun bindGuestSessionRepository(
        impl: GuestSessionRepositoryImpl
    ): GuestSessionRepository

    companion object {

        @Provides
        @Singleton
        fun provideApiKeyInterceptor(
            @Named("api_key") apiKey: String
        ): Interceptor =
            Interceptor { chain ->
                val url = chain.request().url.newBuilder()
                    .addQueryParameter("api_key", apiKey)
                    .build()

                chain.proceed(
                    chain.request()
                        .newBuilder()
                        .url(url)
                        .build()
                )
            }

        @Provides
        @Singleton
        fun provideLoggingInterceptor(): HttpLoggingInterceptor =
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                        else HttpLoggingInterceptor.Level.NONE
            }

        @Provides
        @Singleton
        fun provideOkHttpClient(
            apiKeyInterceptor: Interceptor,
            loggingInterceptor: HttpLoggingInterceptor
        ): OkHttpClient =
            OkHttpClient.Builder()
                .addInterceptor(apiKeyInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()

        @Provides
        @Singleton
        fun provideRetrofit(
            @Named("base_url") baseUrl: String,
            client: OkHttpClient
        ): Retrofit =
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        @Provides
        @Singleton
        fun provideSessionApiService(
            retrofit: Retrofit
        ): SessionApiService =
            retrofit.create(SessionApiService::class.java)

        @Provides
        @Singleton
        fun provideEncryptedSharedPreferences(context: Context): SharedPreferences =
            SecurePreferences(context)

        @Provides
        @Singleton
        fun provideFirebaseCrashlytics(): FirebaseCrashlytics {
            return FirebaseCrashlytics.getInstance()
        }
    }
}
