package com.ixuea.courses.mymusic.component.lyric.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.lyric.model.Line
import com.ixuea.courses.mymusic.component.lyric.ui.SelectLyricScreen
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme
import com.ixuea.courses.mymusic.util.ShareUtil
import com.ixuea.superui.toast.SuperToast
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import timber.log.Timber

/**
 * 选择歌词界面
 */
class SelectLyricActivity : BaseLogicActivity() {
    private var data: Song? = null
    private var lyrics by mutableStateOf<List<Line>>(emptyList())
    private var selectedIndexes by mutableStateOf<Set<Int>>(emptySet())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MuseFlowTheme {
                SelectLyricScreen(
                    lyrics = lyrics,
                    selectedIndexes = selectedIndexes,
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onLyricClick = ::toggleLyric,
                    onShareLyricClick = ::shareLyric,
                    onShareLyricImageClick = ::shareLyricImage,
                )
            }
        }
    }

    override fun initViews() {
        super.initViews()
        QMUIStatusBarHelper.setStatusBarDarkMode(this)
        setStatusBarColor(getColor(R.color.black42))
    }

    override fun initDatum() {
        super.initDatum()
        data = extraData()
        lyrics = data?.parsedLyric?.datum.orEmpty()
        selectedIndexes = emptySet()
    }

    private fun toggleLyric(position: Int) {
        selectedIndexes = if (position in selectedIndexes) {
            selectedIndexes - position
        } else {
            selectedIndexes + position
        }
    }

    private fun shareLyric() {
        val lyricString = getSelectLyricString("，")

        if (lyricString.isEmpty()) {
            SuperToast.show(R.string.hint_select_lyric)
            return
        }

        Timber.d("share text %s", lyricString)

        data?.let { song ->
            ShareUtil.shareLyricText(hostActivity, song, lyricString)
        }
    }

    private fun shareLyricImage() {
        val lyricString = getSelectLyricString("\n")

        if (lyricString.isEmpty()) {
            SuperToast.show(R.string.hint_select_lyric)
            return
        }

        data?.let { song ->
            ShareLyricImageActivity.start(hostActivity, song, lyricString)
        }
    }

    /**
     * 获取选择的歌词文本
     */
    private fun getSelectLyricString(separator: String): String {
        return selectedIndexes
            .sorted()
            .mapNotNull { index -> lyrics.getOrNull(index)?.data }
            .joinToString(separator)
    }
}
