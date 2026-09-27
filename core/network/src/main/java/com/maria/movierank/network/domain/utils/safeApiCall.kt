package com.maria.movierank.network.domain.utils

import com.maria.movierank.network.domain.model.NetworkException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

suspend fun <T> safeApiCall(call: suspend () -> T): T =
    try {
        call()
    } catch (e: NetworkException) {
        throw e
    } catch (e: HttpException) {
        throw NetworkException.HttpError(e.code(), e.response()?.errorBody()?.string())
    } catch (e: SocketTimeoutException) {
        throw NetworkException.TimeoutError(e)
    } catch (e: IOException) {
        throw NetworkException.ConnectionError(e)
    } catch (e: Exception) {
        throw NetworkException.UnknownError(e)
    }
