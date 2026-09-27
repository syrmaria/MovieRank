package com.maria.movierank.network.data.api

import com.maria.movierank.network.data.model.GuestSessionResponse
import retrofit2.http.GET

interface SessionApiService {

    @GET("authentication/guest_session/new")
    suspend fun createGuestSession(): GuestSessionResponse
}
