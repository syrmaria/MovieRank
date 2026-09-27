package com.maria.movierank.feature.trending.domain.repo

import com.maria.movierank.feature.trending.data.api.MoviesApiService
import com.maria.movierank.feature.trending.data.model.GenreResponse
import com.maria.movierank.feature.trending.data.model.MovieListResponse
import com.maria.movierank.feature.trending.domain.model.Genre
import com.maria.movierank.feature.trending.domain.model.Movie
import com.maria.movierank.feature.trending.domain.model.MovieDetails
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class MovieRepositoryImplTest {

    private lateinit var mockApiService: MoviesApiService
    private lateinit var repo: MovieRepositoryImpl

    private val movie1 = Movie(id = 1, title = "Movie One", posterPath = null,
        releaseDate = "2024-01-01", genreIds = listOf(28), popularity = 100.0,
        voteAverage = 7.0, voteCount = 500)
    private val movie2 = Movie(id = 2, title = "Movie Two", posterPath = null,
        releaseDate = "2023-01-01", genreIds = listOf(18), popularity = 50.0,
        voteAverage = 8.0, voteCount = 1000)

    @Before
    fun setUp() {
        mockApiService = mock()
        repo = MovieRepositoryImpl(mockApiService)
    }

    @Test
    fun `getTrendingMovies returns merged results from all pages`() = runTest {
        whenever(mockApiService.discoverMovies(any(), any()))
            .thenReturn(MovieListResponse(1, listOf(movie1, movie2), 5, 10))
        val result = repo.getTrendingMovies(pages = 1)
        assertEquals(listOf(movie1, movie2), result)
    }

    @Test
    fun `getTrendingMovies deduplicates movies with same id across pages`() = runTest {
        whenever(mockApiService.discoverMovies(any(), any()))
            .thenReturn(MovieListResponse(1, listOf(movie1), 5, 5))
        val result = repo.getTrendingMovies(pages = 3)
        assertEquals(1, result.size)
        assertEquals(movie1.id, result[0].id)
    }

    @Test
    fun `getMovieDetail returns detail from API`() = runTest {
        val detail = MovieDetails(
            id = 1, title = "Movie One", tagline = null, overview = "An overview",
            budget = 0L, revenue = 0L, runtime = 120,
            genres = listOf(Genre(28, "Action")), imdbId = "tt001",
            voteAverage = 7.0, voteCount = 100, status = "Released",
            releaseDate = "2024-01-01", posterPath = null, backdropPath = null
        )
        whenever(mockApiService.getMovieDetail(any())).thenReturn(detail)
        assertEquals(detail, repo.getMovieDetail(1))
    }

    @Test
    fun `getGenres returns genre response from API`() = runTest {
        val response = GenreResponse(listOf(Genre(28, "Action"), Genre(18, "Drama")))
        whenever(mockApiService.getGenres()).thenReturn(response)
        assertEquals(response, repo.getGenres())
    }
}
