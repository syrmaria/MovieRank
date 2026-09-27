package com.maria.movierank.network.data.model

import com.google.gson.annotations.SerializedName

data class GuestSessionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("guest_session_id") val guestSessionId: String,
    @SerializedName("expires_at") val expiresAt: String
)
