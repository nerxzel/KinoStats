package com.mooncowpines.kinostats.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mooncowpines.kinostats.domain.model.MovieCard
import com.mooncowpines.kinostats.ui.theme.KinoSpacing
import com.mooncowpines.kinostats.ui.theme.KinoWhite
import com.mooncowpines.kinostats.ui.theme.KinoYellow

@Composable
fun KinoHomeCarrousel(
    modifier: Modifier = Modifier,
    title: String,
    movieCards: List<MovieCard>,
    onMovieClick: (Long) -> Unit,
    onSeeAllClick: () -> Unit
) {
    Column(modifier = modifier) {

        Column {
            Text(
                text = title,
                color = KinoWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier
                    .clickable { onSeeAllClick() }
                    .padding(start = 16.dp, bottom = 4.dp)
            )

            HorizontalDivider(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .width(150.dp),
                color = KinoYellow,
                thickness = 2.dp
            )

            Spacer(modifier.height(KinoSpacing.mediumSmall))

            if (movieCards.isEmpty()) {
                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(4) {
                        KinoSkeletonPosterCard()
                    }
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(movieCards) { movieCard ->
                        KinoPosterCard(
                            movieCard = movieCard,
                            onClick = { id -> onMovieClick(id) },
                            Modifier.width(110.dp).aspectRatio(2f/3f)
                        )
                    }
                    item {
                        KinoSeeMoreCard(onClick = onSeeAllClick)
                    }
                }
            }
        }
    }
}