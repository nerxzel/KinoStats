package com.mooncowpines.kinostats.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mooncowpines.kinostats.domain.model.MovieCard

@Composable
fun KinoBentoPoster(
    title: String,
    movieCard: MovieCard?,
    modifier: Modifier = Modifier,
    onPosterClick: (Long) -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))

        val posterModifier = Modifier
            .weight(1f)
            .aspectRatio(2f / 3f, matchHeightConstraintsFirst = true)

        if (movieCard != null) {
            KinoPosterCard(
                movieCard = movieCard,
                onClick = onPosterClick,
                modifier = posterModifier
            )
        } else {
            Box(
                modifier = posterModifier
                    .background(Color.DarkGray, MaterialTheme.shapes.small)
            )
        }
    }
}