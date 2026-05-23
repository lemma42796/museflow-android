package com.ixuea.courses.mymusic.component.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.ExperimentalGlanceApi
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.action.actionStartService
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.ixuea.courses.mymusic.MainActivity
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.playback.PlaybackService
import com.ixuea.courses.mymusic.util.Constant

/**
 * 音乐桌面微件。
 *
 * Glance 提供 Compose-style API，运行时仍遵循 AppWidget 的系统限制。
 */
class MusicWidget : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MusicGlanceWidget
}

internal object MusicGlanceWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val snapshot = MusicWidgetStore.readSnapshot(context)
        val cover = MusicWidgetStore.readCover(context)
        provideContent {
            MusicWidgetContent(snapshot, cover)
        }
    }
}

private val WidgetSurface = ColorProvider(Color(0xE614171A))
private val WidgetOnSurface = ColorProvider(Color.White)
private val WidgetAccent = ColorProvider(Color(0xFF5EEAD4))
private val WidgetTrack = ColorProvider(Color(0x33FFFFFF))

@OptIn(ExperimentalGlanceApi::class)
@Composable
private fun MusicWidgetContent(snapshot: MusicWidgetSnapshot, cover: Bitmap?) {
    val context = LocalContext.current
    val iconTint = ColorFilter.tint(WidgetOnSurface)

    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .background(WidgetSurface)
            .cornerRadius(18.dp)
            .padding(start = 14.dp, top = 10.dp, end = 14.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            provider = cover?.let { ImageProvider(it) } ?: ImageProvider(R.drawable.placeholder),
            contentDescription = "当前歌曲封面",
            modifier = GlanceModifier
                .size(64.dp)
                .cornerRadius(14.dp)
                .clickable(actionStartActivity(createPlayerIntent(context))),
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = GlanceModifier.width(12.dp))

        Column(
            modifier = GlanceModifier
                .defaultWeight()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = snapshot.title,
                maxLines = 1,
                style = TextStyle(
                    color = WidgetOnSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )

            Spacer(modifier = GlanceModifier.height(7.dp))

            LinearProgressIndicator(
                progress = snapshot.progressFraction,
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = WidgetAccent,
                backgroundColor = WidgetTrack,
            )

            Spacer(modifier = GlanceModifier.height(7.dp))

            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WidgetActionIcon(
                    resId = R.drawable.thumb,
                    contentDescription = "喜欢",
                    colorFilter = iconTint,
                )
                WidgetActionIcon(
                    resId = R.drawable.music_previous,
                    contentDescription = "上一首",
                    colorFilter = iconTint,
                    action = createPlaybackIntent(context, Constant.ACTION_PREVIOUS),
                )
                WidgetActionIcon(
                    resId = if (snapshot.isPlaying) R.drawable.music_pause else R.drawable.music_play,
                    contentDescription = if (snapshot.isPlaying) "暂停" else "播放",
                    colorFilter = iconTint,
                    action = createPlaybackIntent(context, Constant.ACTION_PLAY),
                )
                WidgetActionIcon(
                    resId = R.drawable.music_next,
                    contentDescription = "下一首",
                    colorFilter = iconTint,
                    action = createPlaybackIntent(context, Constant.ACTION_NEXT),
                )
                WidgetActionIcon(
                    resId = if (snapshot.isGlobalLyricShown) R.drawable.ic_lyric else R.drawable.ic_lyric_selected,
                    contentDescription = "桌面歌词",
                    colorFilter = if (snapshot.isGlobalLyricShown) ColorFilter.tint(WidgetAccent) else iconTint,
                    action = createPlaybackIntent(context, Constant.ACTION_LYRIC),
                )
            }
        }
    }
}

@Composable
private fun WidgetActionIcon(
    resId: Int,
    contentDescription: String,
    colorFilter: ColorFilter,
    action: Intent? = null,
) {
    val modifier = GlanceModifier
        .size(30.dp)
        .padding(5.dp)
        .let { base ->
            if (action == null) {
                base
            } else {
                base.clickable(actionStartService(action, isForegroundService = true))
            }
        }

    Image(
        provider = ImageProvider(resId),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit,
        colorFilter = colorFilter,
    )
}

private fun createPlayerIntent(context: Context): Intent {
    return Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        action = Constant.ACTION_MUSIC_PLAYER_PAGE
    }
}

private fun createPlaybackIntent(context: Context, action: String): Intent {
    return Intent(context, PlaybackService::class.java).apply {
        this.action = action
    }
}
