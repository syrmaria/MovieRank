package com.maria.movierank.feature.trending.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import coil3.compose.AsyncImage
import com.maria.movierank.design.AppBarTitleTextStyle
import com.maria.movierank.design.CinemaBackground
import com.maria.movierank.design.CinemaCard
import com.maria.movierank.design.CinemaGold
import com.maria.movierank.design.CinemaSurface
import com.maria.movierank.design.MovieCardTitleTextStyle
import com.maria.movierank.design.StarColor
import com.maria.movierank.design.TextHint
import com.maria.movierank.design.TextPrimary
import com.maria.movierank.design.TextSecondary
import com.maria.movierank.feature.trending.presentation.viewmodel.MovieListEvent
import com.maria.movierank.feature.trending.presentation.viewmodel.MovieListState
import com.maria.movierank.feature.trending.presentation.viewmodel.SortDirection
import com.maria.movierank.feature.trending.presentation.viewmodel.SortField
import com.maria.movierank.feature.trending.presentation.viewmodel.TMDB_IMAGE_BASE_URL
import com.maria.movierank.feature.trending.presentation.viewmodel.defaultDirection
import com.maria.movierank.feature.trending.presentation.viewmodel.label
import com.maria.movierank.feature.trending.domain.model.Movie

@Composable
fun MovieListScreen(
    modifier: Modifier = Modifier,
    state: MovieListState,
    onEvent: (MovieListEvent) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                sortField = state.sortField,
                sortDirection = state.sortDirection,
                selectedGenre = state.selectedGenre,
                availableGenres = state.availableGenres,
                onSortChanged = { field, dir -> onEvent(MovieListEvent.SortChanged(field, dir)) },
                onGenreSelected = { onEvent(MovieListEvent.GenreFilterChanged(it)) }
            )
            when {
                state.isLoading -> LoadingContent()
                state.error != null -> ErrorContent(
                    error = state.error,
                    onRetry = { onEvent(MovieListEvent.Retry) }
                )
                else -> MovieList(
                    movies = state.displayedMovies,
                    sortField = state.sortField,
                    sortDirection = state.sortDirection,
                    selectedGenre = state.selectedGenre,
                    onMovieClick = { onEvent(MovieListEvent.MovieClicked(it)) }
                )
            }
        }

        state.detailState?.let { detailState ->
            MovieDetailBottomSheet(
                state = detailState,
                onEvent = { onEvent(MovieListEvent.DetailEvent(it)) },
                onDismiss = { onEvent(MovieListEvent.DismissMovieDetail) }
            )
        }
    }
}

@Composable
private fun TopBar(
    sortField: SortField,
    sortDirection: SortDirection,
    selectedGenre: String?,
    availableGenres: List<String>,
    onSortChanged: (SortField, SortDirection) -> Unit,
    onGenreSelected: (String?) -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }

    val isSortActive = sortField != SortField.POPULARITY || sortDirection != SortDirection.DESC

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CinemaSurface)
            .padding(start = 20.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "MOVIE",
            style = AppBarTitleTextStyle,
            color = CinemaGold
        )
        Text(
            text = "RANK",
            style = AppBarTitleTextStyle,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.weight(1f))

        // Sort button + dropdown
        Box {
            IconButton(onClick = { showSortMenu = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    contentDescription = "Sort",
                    tint = if (isSortActive) CinemaGold else TextSecondary
                )
            }
            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                SortField.entries.forEach { field ->
                    val isActive = field == sortField
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = field.label,
                                color = if (isActive) CinemaGold else TextPrimary
                            )
                        },
                        trailingIcon = {
                            if (isActive) {
                                Icon(
                                    imageVector = if (sortDirection == SortDirection.ASC)
                                        Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                    contentDescription = null,
                                    tint = CinemaGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        onClick = {
                            val newDirection = if (isActive) sortDirection.toggled()
                                               else field.defaultDirection
                            onSortChanged(field, newDirection)
                            showSortMenu = false
                        }
                    )
                }
            }
        }

        // Filter button + dropdown
        Box {
            IconButton(onClick = { showFilterMenu = true }) {
                Icon(
                    imageVector = Icons.Filled.FilterList,
                    contentDescription = "Filter by genre",
                    tint = if (selectedGenre != null) CinemaGold else TextSecondary
                )
            }
            DropdownMenu(
                expanded = showFilterMenu,
                onDismissRequest = { showFilterMenu = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "All Genres",
                            color = if (selectedGenre == null) CinemaGold else TextPrimary
                        )
                    },
                    trailingIcon = if (selectedGenre == null) {
                        { Icon(Icons.Filled.Check, contentDescription = null, tint = CinemaGold, modifier = Modifier.size(16.dp)) }
                    } else null,
                    onClick = {
                        onGenreSelected(null)
                        showFilterMenu = false
                    }
                )
                availableGenres.forEach { genre ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = genre,
                                color = if (genre == selectedGenre) CinemaGold else TextPrimary
                            )
                        },
                        trailingIcon = if (genre == selectedGenre) {
                            { Icon(Icons.Filled.Check, contentDescription = null, tint = CinemaGold, modifier = Modifier.size(16.dp)) }
                        } else null,
                        onClick = {
                            onGenreSelected(genre)
                            showFilterMenu = false
                        }
                    )
                }
            }
        }
    }
}

private fun SortDirection.toggled() = if (this == SortDirection.ASC) SortDirection.DESC else SortDirection.ASC

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = CinemaGold)
    }
}

@Composable
private fun ErrorContent(error: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = error, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onRetry) {
                Text("Retry", color = CinemaGold)
            }
        }
    }
}

@Composable
private fun MovieList(
    movies: List<Movie>,
    sortField: SortField,
    sortDirection: SortDirection,
    selectedGenre: String?,
    onMovieClick: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = movies, key = { it.id }) { movie ->
            MovieCard(
                movie = movie,
                onClick = { onMovieClick(movie.id) }
            )
        }
    }

    LaunchedEffect(sortField, sortDirection, selectedGenre) {
        listState.scrollToItem(0)
    }
}

@Composable
private fun MovieCard(movie: Movie, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CinemaCard)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        PosterImage(
            posterPath = movie.posterPath,
            modifier = Modifier
                .width(80.dp)
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = movie.title,
                style = MovieCardTitleTextStyle,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            movie.releaseDate?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextHint
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = movie.genreNames.take(2).joinToString(" · "),
                style = MaterialTheme.typography.labelSmall,
                color = CinemaGold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            PopularityBadge(popularity = movie.popularity)
        }
    }
}

@Composable
private fun PosterImage(posterPath: String?, modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(CinemaSurface, RoundedCornerShape(8.dp))) {
        if (posterPath != null) {
            AsyncImage(
                model = "${TMDB_IMAGE_BASE_URL}w185$posterPath",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.2f))
                    )
                )
        )
    }
}

@Composable
private fun PopularityBadge(popularity: Double) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(CinemaSurface)
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = StarColor,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = String.format("%.1f", popularity),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}
