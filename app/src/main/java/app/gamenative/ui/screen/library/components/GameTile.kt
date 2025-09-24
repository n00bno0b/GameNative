package app.gamenative.ui.screen.library.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.gamenative.data.LibraryItem
import app.gamenative.ui.util.ListItemImage

@Composable
internal fun GameTile(
    modifier: Modifier = Modifier,
    appInfo: LibraryItem,
    onClick: () -> Unit,
    onFocus: () -> Unit,
    scale: Float = 1f,
) {
    Card(
        modifier = modifier
            .width(200.dp)
            .height(280.dp)
            .padding(8.dp)
            .clickable { onClick() }
            .onFocusChanged { if (it.isFocused) onFocus() }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ListItemImage(
                modifier = Modifier.fillMaxSize(),
                imageModifier = Modifier.clip(RoundedCornerShape(16.dp)),
                image = { appInfo.headerImageUrl ?: "" },
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
                    .align(Alignment.BottomCenter)
            )
            Text(
                text = appInfo.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }
    }
}
