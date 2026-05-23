package com.ixuea.courses.mymusic.component.sheet.ui

import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.player.fragment.SmallAudioControlPageFragment
import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.ui.compose.AvatarImage
import com.ixuea.courses.mymusic.ui.compose.EmptyContent
import com.ixuea.courses.mymusic.ui.compose.MuseFlowScaffold
import com.ixuea.courses.mymusic.util.ResourceUtil

@Composable
fun SheetDetailScreen(
    state: SheetDetailUiState,
    headerColor: Int,
    canDelete: Boolean,
    isDownloaded: (Song) -> Boolean,
    onBack: () -> Unit,
    onHeaderColorResolved: (Int) -> Unit,
    onPlayAll: () -> Unit,
    onSongClick: (Int) -> Unit,
    onCollectClick: (Sheet) -> Unit,
    onUserClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSortClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onReportClick: () -> Unit,
    bottomBar: @Composable () -> Unit,
) {
    val sheet = state.sheet
    val topBarColor = Color(headerColor)

    MuseFlowScaffold(
        title = stringResource(R.string.activity_sheet_detail),
        onBack = onBack,
        topBarContainerColor = topBarColor,
        topBarContentColor = Color.White,
        actions = {
            SheetDetailActions(
                canDelete = canDelete,
                onSearchClick = onSearchClick,
                onSortClick = onSortClick,
                onDeleteClick = onDeleteClick,
                onReportClick = onReportClick,
            )
        },
        bottomBar = bottomBar,
    ) { innerPadding ->
        if (sheet == null && !state.isLoading) {
            EmptyContent(
                text = stringResource(R.string.no_sheet_detail),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
            return@MuseFlowScaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 12.dp),
        ) {
            if (sheet != null) {
                item {
                    SheetHeader(
                        sheet = sheet,
                        headerColor = headerColor,
                        onHeaderColorResolved = onHeaderColorResolved,
                        onPlayAll = onPlayAll,
                        onCollectClick = { onCollectClick(sheet) },
                        onUserClick = {
                            sheet.user?.id?.let(onUserClick)
                        },
                        onCommentClick = {
                            sheet.id?.let(onCommentClick)
                        },
                    )
                }

                val songs = sheet.songs.orEmpty()
                if (songs.isEmpty()) {
                    item {
                        EmptyContent(
                            text = stringResource(R.string.no_sheet_song),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                        )
                    }
                } else {
                    itemsIndexed(
                        items = songs,
                        key = { index, song -> song.id ?: "song-$index" },
                    ) { index, song ->
                        SheetSongRow(
                            index = index + 1,
                            song = song,
                            downloaded = isDownloaded(song),
                            onClick = { onSongClick(index) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SheetDetailActions(
    canDelete: Boolean,
    onSearchClick: () -> Unit,
    onSortClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onReportClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = onSearchClick) {
        Icon(
            painter = painterResource(R.drawable.search),
            contentDescription = stringResource(R.string.search),
            tint = Color.White,
        )
    }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                painter = painterResource(R.drawable.more_vertical_dot),
                contentDescription = stringResource(R.string.more),
                tint = Color.White,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.sort)) },
                onClick = {
                    expanded = false
                    onSortClick()
                },
            )
            if (canDelete) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.delete)) },
                    onClick = {
                        expanded = false
                        onDeleteClick()
                    },
                )
            }
            DropdownMenuItem(
                text = { Text(stringResource(R.string.report)) },
                onClick = {
                    expanded = false
                    onReportClick()
                },
            )
        }
    }
}

@Composable
private fun SheetHeader(
    sheet: Sheet,
    headerColor: Int,
    onHeaderColorResolved: (Int) -> Unit,
    onPlayAll: () -> Unit,
    onCollectClick: () -> Unit,
    onUserClick: () -> Unit,
    onCommentClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(headerColor)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SheetCoverImage(
                icon = sheet.icon,
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(8.dp)),
                onHeaderColorResolved = onHeaderColorResolved,
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sheet.title.orEmpty(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .clickable(onClick = onUserClick),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AvatarImage(
                        url = sheet.user?.icon.orEmpty(),
                        size = 28.dp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = sheet.user?.nickname.orEmpty(),
                        color = Color.White.copy(alpha = 0.86f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        SheetStatsCard(
            sheet = sheet,
            onCommentClick = onCommentClick,
        )

        SheetPlayControl(
            sheet = sheet,
            onPlayAll = onPlayAll,
            onCollectClick = onCollectClick,
        )
    }
}

@Composable
private fun SheetCoverImage(
    icon: String?,
    modifier: Modifier = Modifier,
    onHeaderColorResolved: (Int) -> Unit,
) {
    val context = LocalContext.current
    val defaultColor = ContextCompat.getColor(context, R.color.primary)

    AndroidView(
        modifier = modifier,
        factory = { viewContext ->
            ImageView(viewContext).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageResource(R.drawable.placeholder)
            }
        },
        update = { imageView ->
            val iconValue = icon.orEmpty()
            if (imageView.tag == iconValue) {
                return@AndroidView
            }
            imageView.tag = iconValue

            if (iconValue.isBlank()) {
                imageView.setImageResource(R.drawable.placeholder)
                onHeaderColorResolved(defaultColor)
                return@AndroidView
            }

            val uri = ResourceUtil.resourceUri(iconValue)
            val glidePalette = GlidePalette
                .with(uri)
                .use(BitmapPalette.Profile.VIBRANT)
                .intoCallBack(
                    object : BitmapPalette.CallBack {
                        override fun onPaletteLoaded(palette: Palette?) {
                            onHeaderColorResolved(
                                palette?.vibrantSwatch?.rgb ?: defaultColor,
                            )
                        }
                    },
                )
                .crossfade(true)

            Glide.with(imageView)
                .load(uri)
                .listener(glidePalette)
                .into(imageView)
        },
    )
}

@Composable
private fun SheetStatsCard(
    sheet: Sheet,
    onCommentClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Row(
            modifier = Modifier
                .height(46.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SheetStatCell(value = sheet.clicksCount.toString())
            VerticalDivider(
                modifier = Modifier
                    .height(20.dp)
                    .width(1.dp),
            )
            SheetStatCell(
                value = sheet.commentsCount.toString(),
                onClick = onCommentClick,
            )
            VerticalDivider(
                modifier = Modifier
                    .height(20.dp)
                    .width(1.dp),
            )
            SheetStatCell(value = sheet.collectsCount.toString())
        }
    }
}

@Composable
private fun RowScope.SheetStatCell(
    value: String,
    onClick: (() -> Unit)? = null,
) {
    val modifier = if (onClick == null) {
        Modifier
    } else {
        Modifier.clickable(onClick = onClick)
    }

    Row(
        modifier = modifier
            .weight(1f)
            .height(46.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.search_on_surface),
            contentDescription = null,
            tint = Unspecified,
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun SheetPlayControl(
    sheet: Sheet,
    onPlayAll: () -> Unit,
    onCollectClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onPlayAll)
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.play),
            contentDescription = null,
            tint = Unspecified,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.play_all),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = stringResource(R.string.music_count, sheet.songs?.size ?: 0),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )

        val collectText = if (sheet.isCollect) {
            stringResource(R.string.cancel_collect, sheet.collectsCount)
        } else {
            stringResource(R.string.collect, sheet.collectsCount)
        }
        val collectColors = if (sheet.isCollect) {
            ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
        } else {
            ButtonDefaults.buttonColors()
        }

        Button(
            onClick = onCollectClick,
            shape = RoundedCornerShape(0.dp),
            colors = collectColors,
            contentPadding = PaddingValues(horizontal = 18.dp),
            modifier = Modifier.height(56.dp),
        ) {
            Text(collectText)
        }
    }
}

@Composable
private fun SheetSongRow(
    index: Int,
    song: Song,
    downloaded: Boolean,
    onClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clickable(onClick = onClick)
                .padding(end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.width(50.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = index.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (downloaded) {
                        Icon(
                            painter = painterResource(R.drawable.ic_song_downloaded),
                            contentDescription = null,
                            tint = Unspecified,
                            modifier = Modifier
                                .padding(end = 5.dp)
                                .size(15.dp),
                        )
                    }
                    Text(
                        text = song.singer?.nickname.orEmpty(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(R.drawable.more_vertical_dot),
                    contentDescription = stringResource(R.string.more),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        HorizontalDivider()
    }
}

@Composable
fun SmallAudioControlHost(
    fragmentManager: FragmentManager,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            FragmentContainerView(context).apply {
                id = R.id.small_audio_control
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            }
        },
        update = { container ->
            if (fragmentManager.findFragmentById(container.id) == null &&
                !fragmentManager.isStateSaved
            ) {
                fragmentManager
                    .beginTransaction()
                    .replace(container.id, SmallAudioControlPageFragment())
                    .commit()
            }
        },
    )
}
