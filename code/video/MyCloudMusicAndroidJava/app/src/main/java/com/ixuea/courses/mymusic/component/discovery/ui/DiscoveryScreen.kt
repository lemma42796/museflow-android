package com.ixuea.courses.mymusic.component.discovery.ui

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.ixuea.superui.util.DensityUtil
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator

@Composable
fun DiscoveryScreen(
    state: DiscoveryUiState,
    lifecycleOwner: LifecycleOwner,
    onSheetClick: (Sheet) -> Unit,
    onSongClick: (Song) -> Unit,
    onRefreshClick: () -> Unit,
    onCustomDiscoveryClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.sections.isEmpty() && !state.isLoading) {
            EmptyContent(
                text = stringResource(R.string.empty_song_tip),
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                items(
                    items = state.sections,
                    key = { section -> section.discoveryStableKey() },
                ) { section ->
                    DiscoverySection(
                        section = section,
                        lifecycleOwner = lifecycleOwner,
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
private fun DiscoverySection(
    section: BaseMultiItemEntity,
    lifecycleOwner: LifecycleOwner,
    onSheetClick: (Sheet) -> Unit,
    onSongClick: (Song) -> Unit,
    onRefreshClick: () -> Unit,
    onCustomDiscoveryClick: () -> Unit,
) {
    when (section) {
        is BannerData -> DiscoveryBanner(
            ads = section.data,
            lifecycleOwner = lifecycleOwner,
        )

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
private fun DiscoveryBanner(
    ads: List<Ad>,
    lifecycleOwner: LifecycleOwner,
) {
    if (ads.isEmpty()) {
        return
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .aspectRatio(2.57f),
        factory = { context ->
            Banner<Ad, BannerImageAdapter<Ad>>(context).apply {
                setBannerRound(DensityUtil.dip2px(context, 10F))
                setIndicator(CircleIndicator(context))
                addBannerLifecycleObserver(lifecycleOwner)
            }
        },
        update = { banner ->
            val adapter = object : BannerImageAdapter<Ad>(ads) {
                override fun onBindView(
                    holder: BannerImageHolder,
                    data: Ad,
                    position: Int,
                    size: Int,
                ) {
                    ImageUtil.show(holder.itemView.context, holder.itemView as ImageView, data.icon)
                }
            }
            banner.setAdapter(adapter)
        },
    )
}

@Composable
private fun DiscoveryButtons(data: List<IconTitleButtonData>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 10.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        data.forEach { item ->
            DiscoveryButtonItem(item)
        }
    }
}

@Composable
private fun DiscoveryButtonItem(item: IconTitleButtonData) {
    val label = stringResource(item.title)

    Column(
        modifier = Modifier
            .widthIn(min = 58.dp, max = 72.dp)
            .clickable { },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = label,
                tint = Unspecified,
                modifier = Modifier.size(28.dp),
            )
        }
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp),
        )
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        sheets.chunked(3).forEach { rowSheets ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowSheets.forEach { sheet ->
                    SheetCard(
                        sheet = sheet,
                        onClick = { onSheetClick(sheet) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(3 - rowSheets.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
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

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        songs.forEachIndexed { index, song ->
            SongRow(
                song = song,
                showDivider = index != songs.lastIndex,
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
            .padding(horizontal = 16.dp, vertical = 10.dp),
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
                    .clip(RoundedCornerShape(8.dp)),
            )
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.36f))
                    .padding(horizontal = 6.dp, vertical = 3.dp),
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
                    text = sheet.clicksCount.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }

        Text(
            text = sheet.title.orEmpty(),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
        )
    }
}

@Composable
private fun SongRow(
    song: Song,
    showDivider: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RemoteImage(
                url = song.icon,
                modifier = Modifier
                    .size(51.dp)
                    .clip(RoundedCornerShape(6.dp)),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "%s-%s".format(song.singer?.nickname.orEmpty(), "专辑名称"),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        if (showDivider) {
            HorizontalDivider()
        }
    }
}

@Composable
private fun DiscoveryFooter(
    onRefreshClick: () -> Unit,
    onCustomDiscoveryClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.click_refresh),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.clickable(onClick = onRefreshClick),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.change_content),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Button(
            onClick = onCustomDiscoveryClick,
            modifier = Modifier.padding(top = 16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(stringResource(R.string.custom_discovery))
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
