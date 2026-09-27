package com.maria.movierank.feature.trending.presentation.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.maria.movierank.design.BorderColor
import com.maria.movierank.design.CinemaBackground
import com.maria.movierank.design.CinemaGold
import com.maria.movierank.design.CinemaSurface
import com.maria.movierank.design.DetailMovieTitleTextStyle
import com.maria.movierank.design.GenreChipTextStyle
import com.maria.movierank.design.InfoLabelTextStyle
import com.maria.movierank.design.InfoValueTextStyle
import com.maria.movierank.design.LinkTextStyle
import com.maria.movierank.design.OverviewTextStyle
import com.maria.movierank.design.StarColor
import com.maria.movierank.design.TaglineTextStyle
import com.maria.movierank.design.TextHint
import com.maria.movierank.design.TextPrimary
import com.maria.movierank.design.TextSecondary
import com.maria.movierank.design.VoteCountTextStyle
import com.maria.movierank.design.VoteScoreTextStyle
import androidx.core.net.toUri
import com.maria.movierank.feature.trending.presentation.viewmodel.MovieDetailEvent
import com.maria.movierank.feature.trending.presentation.viewmodel.MovieDetailState
import com.maria.movierank.feature.trending.domain.model.MovieDetails

private const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
private const val IMDB_BASE_URL = "https://www.imdb.com/title/"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailBottomSheet(
    state: MovieDetailState,
    onEvent: (MovieDetailEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val blockUpwardOverscroll = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset = if (available.y < 0) available else Offset.Zero

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity =
                if (available.y < 0) available else Velocity.Zero
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CinemaBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .background(BorderColor, RoundedCornerShape(2.dp))
            )
        }
    ) {
        Box(modifier = Modifier.nestedScroll(blockUpwardOverscroll)) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CinemaGold)
                    }
                }
                state.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.error, style = OverviewTextStyle, color = TextSecondary)
                            TextButton(onClick = { onEvent(MovieDetailEvent.Retry) }) {
                                Text("Retry", style = LinkTextStyle, color = CinemaGold)
                            }
                        }
                    }
                }
                state.movieDetail != null -> DetailContent(detail = state.movieDetail)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailContent(detail: MovieDetails) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        detail.backdropPath?.let { path ->
            AsyncImage(
                model = "${TMDB_IMAGE_BASE_URL}w780$path",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(
                text = detail.title,
                style = DetailMovieTitleTextStyle,
                color = TextPrimary
            )

            if (!detail.tagline.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"${detail.tagline}\"",
                    style = TaglineTextStyle,
                    color = CinemaGold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = StarColor, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format("%.1f", detail.voteAverage),
                    style = VoteScoreTextStyle,
                    color = TextPrimary
                )
                Text(
                    text = "  ·  ${detail.voteCount} votes",
                    style = VoteCountTextStyle,
                    color = TextHint
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (detail.genres.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    detail.genres.forEach { genre ->
                        Text(
                            text = genre.name,
                            style = GenreChipTextStyle,
                            color = CinemaGold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CinemaSurface)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (!detail.overview.isNullOrBlank()) {
                Text(
                    text = detail.overview,
                    style = OverviewTextStyle,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Divider()
            detail.status?.let {
                InfoRow(label = "Status", value = it)
            }
            detail.releaseDate?.let {
                InfoRow(label = "Release Date", value = it)
            }
            detail.runtime?.let {
                InfoRow(label = "Runtime", value = if (it > 0) "${it} min" else "—")
            }
            InfoRow(label = "Budget", value = if (detail.budget > 0) formatMoney(detail.budget) else "—")
            InfoRow(label = "Revenue", value = if (detail.revenue > 0) formatMoney(detail.revenue) else "—")

            if (!detail.imdbId.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, "$IMDB_BASE_URL${detail.imdbId}".toUri())
                        context.startActivity(intent)
                    }
                ) {
                    Text("Open on IMDB", style = LinkTextStyle, color = CinemaGold)
                }
            }
        }
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BorderColor)
            .padding(vertical = 8.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = InfoLabelTextStyle, color = TextHint)
        Text(
            text = value,
            style = InfoValueTextStyle,
            color = TextPrimary
        )
    }
}

private fun formatMoney(amount: Long): String {
    return when {
        amount >= 1_000_000_000 -> "$${String.format("%.2f", amount / 1_000_000_000.0)}B"
        amount >= 1_000_000 -> "$${String.format("%.1f", amount / 1_000_000.0)}M"
        else -> "$$amount"
    }
}

