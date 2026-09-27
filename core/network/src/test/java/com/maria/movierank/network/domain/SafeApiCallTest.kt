package com.maria.movierank.network.domain

import com.maria.movierank.network.domain.model.NetworkException
import com.maria.movierank.network.domain.utils.safeApiCall
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class SafeApiCallTest {

    @Test
    fun `returns result when call succeeds`() = runTest {
        val result = safeApiCall {
            "success"
        }

        assertEquals("success", result)
    }

    @Test
    fun `rethrows NetworkException unchanged`() = runTest {
        val original = NetworkException.ConnectionError(
            IOException("offline")
        )

        val caught = runCatching {
            safeApiCall<String> {
                throw original
            }
        }.exceptionOrNull()

        assertSame(original, caught)
    }

    @Test
    fun `wraps HttpException as HttpError`() = runTest {
        val response = Response.error<String>(503, ResponseBody.create(null, "unavailable"))
        val original = HttpException(response)

        val caught = runCatching {
            safeApiCall<String> {
                throw original
            }
        }.exceptionOrNull()

        assertTrue(caught is NetworkException.HttpError)

        val error = caught as NetworkException.HttpError
        assertEquals(503, error.code)
        assertEquals("unavailable", error.body)
    }

    @Test
    fun `wraps SocketTimeoutException as TimeoutError`() = runTest {
        val caught = runCatching {
            safeApiCall<String> {
                throw SocketTimeoutException("timed out")
            }
        }.exceptionOrNull()

        assertTrue(caught is NetworkException.TimeoutError)
        assertTrue(caught?.cause is SocketTimeoutException)
    }

    @Test
    fun `wraps IOException as ConnectionError`() = runTest {
        val caught = runCatching {
            safeApiCall<String> {
                throw IOException("No network")
            }
        }.exceptionOrNull()

        assertTrue(caught is NetworkException.ConnectionError)
    }

    @Test
    fun `wraps unknown exception as UnknownError`() = runTest {
        val caught = runCatching {
            safeApiCall<String> {
                throw IllegalStateException("Something went wrong")
            }
        }.exceptionOrNull()

        assertTrue(caught is NetworkException.UnknownError)
        assertEquals("Something went wrong", caught?.message)
    }
}