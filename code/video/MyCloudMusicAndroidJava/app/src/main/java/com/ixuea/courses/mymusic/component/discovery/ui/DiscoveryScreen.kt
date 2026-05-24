package com.ixuea.courses.mymusic.component.discovery.ui

import android.widget.ImageView
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.LifecycleOwner
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.ad.model.Ad
import com.ixuea.courses.mymusic.component.discovery.model.ui.BannerData
import com.ixuea.courses.mymusic.component.discovery.model.ui.ButtonData
import com.ixuea.courses.mymusic.component.discovery.model.ui.FooterData
import com.ixuea.courses.mymusic.component.discovery.model.ui.IconTitleButtonData
import com.ixuea.courses.mymusic.component.discovery.model.ui.SheetData
import com.ixuea.courses.mymusic.component.discovery.model.ui.SongData
import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity
import com.ixuea.courses.mymusic.ui.compose.EmptyContent
import com.ixuea.courses.mymusic.util.ImageUtil

private val HomeHorizontalPadding = 16.dp
private val HeroShape = RoundedCornerShape(28.dp)
private val SectionShape = RoundedCornerShape(24.dp)

@Suppress("UNUSED_PARAMETER")
@Composable
fun DiscoveryScreen(
    state: DiscoveryUiState,
    lifecycleOwner: LifecycleOwner,
    onSheetClick: (Sheet) -> Unit,
    onSongClick: (Song) -> Unit,
    onRefreshClick: () -> Unit,
    onCustomDiscoveryClick: () -> Unit,
) {
    val heroAd = state.sections
        .asSequence()
        .filterIsInstance<BannerData>()
        .flatMap { it.data.asSequence() }
        .firstOrNull()
    val heroSong = state.sections
        .asSequence()
        .filterIsInstance<SongData>()
        .flatMap { it.data.asSequence() }
        .firstOrNull()
    val contentSections = state.sections.filterNot { it is BannerData }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.sections.isEmpty() && !state.isLoading) {
            EmptyContent(
                text = stringResource(R.string.empty_song_tip),
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item(key = "home-header") {
                    HomeGreeting()
                }

                if (heroAd != null) {
                    item(key = "home-hero") {
                        HomeHeroCard(
                            ad = heroAd,
                            song = heroSong,
                            onSongClick = onSongClick,
                        )
                    }
                }

                items(
                    items = contentSections,
                    key = { section -> section.discoveryStableKey() },
                ) { section ->
                    DiscoverySection(
                        section = section,
                        onSheetClick = onSheetClick,
                        onSongClick = onSongClick,
                        onRefreshClick = onRefreshClick,
                        onCustomDiscoveryClick = onCustomDiscoveryClick,
                    )
                }
            }
        }

        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

private fun BaseMultiItemEntity.discoveryStableKey(): String {
    return when (this) {
        is BannerData -> "banner-$sort"
        is ButtonData -> "buttons-$sort"
        is SheetData -> "sheets-$sort"
        is SongData -> "songs-$sort"
        is FooterData -> "footer"
        else -> "section-$itemType"
    }
}

@Composable
private fun HomeGreeting() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = HomeHorizontalPadding,
                top = 14.dp,
                end = HomeHorizontalPadding,
                bottom = 2.dp,
            ),
    ) {
        Text(
            text = "今天想听什么？",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "为你整理今日推荐和正在流动的新声音",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun HomeHeroCard(
    ad: Ad,
    song: Song?,
    onSongClick: (Song) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(184.dp)
            .padding(horizontal = HomeHorizontalPadding),
        shape = HeroShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            RemoteImage(
                url = ad.icon,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.30f)),
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp),
            ) {
                Text(
                    text = "MuseFlow 今日首页",
                    color = Color.White.copy(alpha = 0.84f),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = ad.title?.takeIf { it.isNotBlank() } ?: "跟随节奏开始播放",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp),
                )

                if (song != null) {
                    Surface(
                        modifier = Modifier
                            .padding(top = 14.dp)
                            .clickable { onSongClick(song) },
                        color = Color.White.copy(alpha = 0.92f),
                        contentColor = Color(0xFF10231E),
                        shape = RoundedCornerShape(999.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.play_solid),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                text = song.title?.takeIf { it.isNotBlank() } ?: "立即播放",
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscoverySection(
    section: BaseMultiItemEntity,
    onSheetClick: (Sheet) -> Unit,
    onSongClick: (Song) -> Unit,
    onRefreshClick: () -> Unit,
    onCustomDiscoveryClick: () -> Unit,
) {
    when (section) {
        is ButtonData -> DiscoveryButtons(data = section.data)

        is SheetData -> DiscoverySheetSection(
            sheets = section.data,
            onSheetClick = onSheetClick,
        )

        is SongData -> DiscoverySongSection(
            songs = section.data,
            onSongClick = onSongClick,
        )

        is FooterData -> DiscoveryFooter(
            onRefreshClick = onRefreshClick,
            onCustomDiscoveryClick = onCustomDiscoveryClick,
        )
    }
}

@Composable
private fun DiscoveryButtons(data: List<IconTitleButtonData>) {
    if (data.isEmpty()) {
        return
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = HomeHorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        itemsIndexed(
            items = data,
            key = { index, item -> "${item.title}-$index" },
        ) { index, item ->
            DiscoveryButtonItem(
                item = item,
                tone = chipTone(index),
            )
        }
    }
}

@Composable
private fun DiscoveryButtonItem(
    item: IconTitleButtonData,
    tone: Color,
) {
    val label = stringResource(item.title)

    Surface(
        modifier = Modifier.clickable { },
        color = tone,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(999.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.76f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(item.icon),
                    contentDescription = label,
                    tint = Unspecified,
                    modifier = Modifier.size(23.dp),
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun DiscoverySheetSection(
    sheets: List<Sheet>,
    onSheetClick: (Sheet) -> Unit,
) {
    if (sheets.isEmpty()) {
        return
    }

    DiscoverySectionTitle(
        title = stringResource(R.string.recommend_sheet),
        showMore = true,
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = HomeHorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(
            items = sheets,
            key = { index, sheet -> sheet.id ?: "sheet-$index" },
        ) { _, sheet ->
            SheetCard(
                sheet = sheet,
                onClick = { onSheetClick(sheet) },
                modifier = Modifier.width(148.dp),
            )
        }
    }
}

@Composable
private fun DiscoverySongSection(
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
) {
    if (songs.isEmpty()) {
        return
    }

    DiscoverySectionTitle(
        title = stringResource(R.string.recommend_song),
        showMore = true,
    )

    Column(
        modifier = Modifier.padding(horizontal = HomeHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        songs.forEach { song ->
            SongRow(
                song = song,
                onClick = { onSongClick(song) },
            )
        }
    }
}

@Composable
private fun DiscoverySectionTitle(
    title: String,
    showMore: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HomeHorizontalPadding, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        if (showMore) {
            TextButton(onClick = {}) {
                Text(stringResource(R.string.more))
            }
        }
    }
}

@Composable
private fun SheetCard(
    sheet: Sheet,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Box {
            RemoteImage(
                url = sheet.icon,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(SectionShape),
            )
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.Black.copy(alpha = 0.38f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.play),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(10.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatPlayCount(sheet.clicksCount),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }

        Text(
            text = sheet.title.orEmpty(),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 9.dp),
        )
    }
}

@Composable
private fun SongRow(
    song: Song,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.46f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RemoteImage(
                url = song.icon,
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(16.dp)),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = song.singer?.nickname?.takeIf { it.isNotBlank() } ?: "MuseFlow 推荐",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
            ) {
                Icon(
                    painter = painterResource(R.drawable.play_solid),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(14.dp),
                )
            }
        }
    }
}

@Composable
private fun DiscoveryFooter(
    onRefreshClick: () -> Unit,
    onCustomDiscoveryClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HomeHorizontalPadding),
        color = MaterialTheme.colorScheme.surface,
        shape = SectionShape,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = onRefreshClick,
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
                Text(stringResource(R.string.change_content))
            }

            TextButton(
                onClick = onCustomDiscoveryClick,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
                Text(stringResource(R.string.custom_discovery))
            }
        }
    }
}

@Composable
private fun RemoteImage(
    url: String?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = { viewContext ->
            ImageView(viewContext).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageResource(R.drawable.placeholder)
            }
        },
        update = { imageView ->
            val value = url.orEmpty()
            if (imageView.tag != value) {
                imageView.tag = value
                ImageUtil.show(context, imageView, value)
            }
        },
    )
}

@Composable
private fun chipTone(index: Int): Color {
    val colorScheme = MaterialTheme.colorScheme
    return when (index % 4) {
        0 -> colorScheme.primaryContainer.copy(alpha = 0.72f)
        1 -> colorScheme.secondaryContainer.copy(alpha = 0.72f)
        2 -> colorScheme.tertiaryContainer.copy(alpha = 0.72f)
        else -> colorScheme.surfaceVariant.copy(alpha = 0.88f)
    }
}

private fun formatPlayCount(count: Int): String {
    return when {
        count >= 10000 -> "${count / 10000}万"
        count > 0 -> count.toString()
        else -> "推荐"
    }
}
