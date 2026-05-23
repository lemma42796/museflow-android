package com.ixuea.courses.mymusic.component.lyric.ui

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.ui.compose.MuseFlowScaffold
import com.ixuea.courses.mymusic.util.ImageUtil
import com.ixuea.superui.SquareLinearLayout

@Composable
fun ShareLyricImageScreen(
    song: Song,
    lyric: String,
    onBack: () -> Unit,
    onShareClick: () -> Unit,
    onContentViewReady: (View) -> Unit,
) {
    MuseFlowScaffold(
        title = stringResource(R.string.activity_share_lyric_image),
        onBack = onBack,
        actions = {
            IconButton(onClick = onShareClick) {
                Icon(
                    painter = painterResource(R.drawable.share),
                    contentDescription = stringResource(R.string.share),
                )
            }
        },
    ) { innerPadding ->
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            factory = { context ->
                ShareLyricImageContentView(context).also(onContentViewReady)
            },
            update = { view ->
                view.bind(song, lyric)
            },
        )
    }
}

private class ShareLyricImageContentView(context: Context) : LinearLayout(context) {
    private val iconView = ImageView(context)
    private val lyricView = TextView(context)
    private val songView = TextView(context)

    init {
        orientation = VERTICAL
        setPadding(dp(16), dp(16), dp(16), dp(16))
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )

        addView(createCoverContainer())
        addView(lyricView.createLyricLayout())
        addView(songView.createSongLayout())
        addView(createFooter())
    }

    fun bind(song: Song, lyric: String) {
        ImageUtil.show(context, iconView, song.icon)
        lyricView.text = lyric
        songView.text = context.getString(
            R.string.share_song_name,
            song.singer?.nickname.orEmpty(),
            song.title,
        )
    }

    private fun createCoverContainer(): View {
        return SquareLinearLayout(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            addView(
                iconView.apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setImageResource(R.drawable.placeholder)
                    layoutParams = LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                },
            )
        }
    }

    private fun TextView.createLyricLayout(): TextView {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        ).apply {
            topMargin = dp(10)
        }
        gravity = Gravity.START
        setLineSpacing(0f, 1.5f)
        setTextColor(ContextCompat.getColor(context, R.color.black20))
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        return this
    }

    private fun TextView.createSongLayout(): TextView {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        ).apply {
            topMargin = dp(40)
        }
        gravity = Gravity.END
        setTextColor(ContextCompat.getColor(context, R.color.black80))
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        return this
    }

    private fun createFooter(): View {
        return LinearLayout(context).apply {
            gravity = Gravity.CENTER_VERTICAL
            orientation = HORIZONTAL
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                topMargin = dp(40)
            }

            addView(
                ImageView(context).apply {
                    setImageResource(R.mipmap.ic_launcher)
                    layoutParams = LayoutParams(dp(12), dp(12))
                },
            )
            addView(
                TextView(context).apply {
                    text = "来自 MuseFlow•歌词分享"
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    layoutParams = LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f,
                    ).apply {
                        marginStart = dp(5)
                    }
                },
            )
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
