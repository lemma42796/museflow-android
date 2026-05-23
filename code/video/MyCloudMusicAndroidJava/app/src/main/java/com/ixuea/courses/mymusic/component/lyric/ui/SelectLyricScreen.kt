package com.ixuea.courses.mymusic.component.lyric.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.lyric.model.Line
import com.ixuea.courses.mymusic.ui.compose.MuseFlowScaffold

@Composable
fun SelectLyricScreen(
    lyrics: List<Line>,
    selectedIndexes: Set<Int>,
    onBack: () -> Unit,
    onLyricClick: (Int) -> Unit,
    onShareLyricClick: () -> Unit,
    onShareLyricImageClick: () -> Unit,
) {
    val backgroundColor = colorResource(R.color.black42)
    val contentColor = colorResource(R.color.white)

    MuseFlowScaffold(
        title = stringResource(R.string.activity_select_lyric),
        onBack = onBack,
        topBarContainerColor = backgroundColor,
        topBarContentColor = contentColor,
        modifier = Modifier.background(backgroundColor),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding),
        ) {
            if (lyrics.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.hint_select_lyric),
                        color = contentColor.copy(alpha = 0.72f),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    itemsIndexed(
                        items = lyrics,
                        key = { index, line -> "${line.startTime}-$index" },
                    ) { index, line ->
                        SelectLyricRow(
                            line = line,
                            selected = index in selectedIndexes,
                            contentColor = contentColor,
                            onClick = { onLyricClick(index) },
                        )
                    }
                }
            }

            SelectLyricActions(
                contentColor = contentColor,
                onShareLyricClick = onShareLyricClick,
                onShareLyricImageClick = onShareLyricImageClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun SelectLyricRow(
    line: Line,
    selected: Boolean,
    contentColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .background(if (selected) Color.Black else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Icon(
                painter = painterResource(R.drawable.ic_desktop_color_check),
                contentDescription = null,
                tint = Unspecified,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(22.dp),
            )
        }

        Text(
            text = line.data.orEmpty(),
            color = contentColor,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SelectLyricActions(
    contentColor: Color,
    onShareLyricClick: () -> Unit,
    onShareLyricImageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LyricActionButton(
            text = stringResource(R.string.share_lyric),
            contentColor = contentColor,
            onClick = onShareLyricClick,
        )
        LyricActionButton(
            text = stringResource(R.string.share_lyric),
            contentColor = contentColor,
            onClick = onShareLyricImageClick,
        )
        LyricActionButton(
            text = stringResource(R.string.lyric_video),
            contentColor = contentColor,
            onClick = {},
        )
    }
}

@Composable
private fun LyricActionButton(
    text: String,
    contentColor: Color,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = contentColor,
        ),
        border = BorderStroke(0.5.dp, colorResource(R.color.black80)),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
        )
    }
}
