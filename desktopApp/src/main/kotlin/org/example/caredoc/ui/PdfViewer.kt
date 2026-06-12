package org.example.caredoc.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun PdfViewer(image: ImageBitmap, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            bitmap = image,
            contentDescription = "PDF Page",
            modifier = Modifier.graphicsLayer(
                scaleX = 1.0f,
                scaleY = 1.0f
            )
        )
    }
}
