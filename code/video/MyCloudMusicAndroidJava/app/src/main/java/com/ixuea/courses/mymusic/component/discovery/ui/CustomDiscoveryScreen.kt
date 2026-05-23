package com.ixuea.courses.mymusic.component.discovery.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.discovery.model.ui.CustomDiscoveryItem
import com.ixuea.courses.mymusic.ui.compose.MuseFlowScaffold

private val CustomDiscoveryRowHeight = 56.dp

@Composable
fun CustomDiscoveryScreen(
    items: List<CustomDiscoveryItem>,
    onBack: () -> Unit,
    onSaveClick: () -> Unit,
    onResetDefaultSortClick: () -> Unit,
    onMove: (from: Int, to: Int) -> Unit,
) {
    MuseFlowScaffold(
        title = stringResource(R.string.custom_discovery),
        onBack = onBack,
        actions = {
            TextButton(onClick = onSaveClick) {
                Text(
                    text = stringResource(R.string.save),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
    ) { innerPadding ->
        CustomDiscoveryList(
            items = items,
            onResetDefaultSortClick = onResetDefaultSortClick,
            onMove = onMove,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}

@Composable
private fun CustomDiscoveryList(
    items: List<CustomDiscoveryItem>,
    onResetDefaultSortClick: () -> Unit,
    onMove: (from: Int, to: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var draggingStyle by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }
    val rowHeightPx = with(LocalDensity.current) { CustomDiscoveryRowHeight.toPx() }

    fun onDrag(deltaY: Float) {
        val style = draggingStyle ?: return
        val from = items.indexOfFirst { item -> item.style == style }
        if (from == -1) {
            return
        }

        dragOffset += deltaY
        val threshold = rowHeightPx / 2
        if (dragOffset > threshold && from < items.lastIndex) {
            onMove(from, from + 1)
            dragOffset -= rowHeightPx
        } else if (dragOffset < -threshold && from > 0) {
            onMove(from, from - 1)
            dragOffset += rowHeightPx
        }
    }

    fun stopDrag() {
        draggingStyle = null
        dragOffset = 0f
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.style },
        ) { _, item ->
            val isDragging = draggingStyle == item.style
            CustomDiscoveryRow(
                item = item,
                isDragging = isDragging,
                dragOffset = if (isDragging) dragOffset else 0f,
                onDragStart = {
                    draggingStyle = item.style
                    dragOffset = 0f
                },
                onDrag = ::onDrag,
                onDragEnd = ::stopDrag,
            )
        }

        item(key = "reset-default-sort") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                TextButton(onClick = onResetDefaultSortClick) {
                    Text(
                        text = stringResource(R.string.reset_default_sort),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomDiscoveryRow(
    item: CustomDiscoveryItem,
    isDragging: Boolean,
    dragOffset: Float,
    onDragStart: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(CustomDiscoveryRowHeight)
            .graphicsLayer { translationY = dragOffset }
            .zIndex(if (isDragging) 1f else 0f)
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(item.title),
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Box(
            modifier = Modifier
                .size(48.dp)
                .pointerInput(item.style) {
                    detectDragGestures(
                        onDragStart = { onDragStart() },
                        onDragEnd = onDragEnd,
                        onDragCancel = onDragEnd,
                    ) { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.y)
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.menu_dot),
                contentDescription = stringResource(R.string.sort),
                tint = Unspecified,
                modifier = Modifier.size(28.dp),
            )
        }
    }
    HorizontalDivider()
}
