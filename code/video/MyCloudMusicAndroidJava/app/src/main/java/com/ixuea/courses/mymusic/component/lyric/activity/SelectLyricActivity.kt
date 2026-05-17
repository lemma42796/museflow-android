package com.ixuea.courses.mymusic.component.lyric.activity

import android.text.TextUtils
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseTitleActivity
import com.ixuea.courses.mymusic.component.lyric.adapter.SelectLyricAdapter
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.databinding.ActivitySelectLyricBinding
import com.ixuea.courses.mymusic.util.ShareUtil
import com.ixuea.superui.toast.SuperToast
import com.ixuea.superui.util.SuperRecyclerViewUtil
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import org.apache.commons.lang3.StringUtils
import timber.log.Timber

/**
 * 选择歌词界面
 */
class SelectLyricActivity : BaseTitleActivity<ActivitySelectLyricBinding>() {
    private lateinit var adapter: SelectLyricAdapter
    private var data: Song? = null

    override fun initViews() {
        super.initViews()
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list)

        QMUIStatusBarHelper.setStatusBarDarkMode(this)
        setStatusBarColor(getColor(R.color.black42))
    }

    override fun initDatum() {
        super.initDatum()
        data = extraData()

        adapter = SelectLyricAdapter(R.layout.item_select_lyric)
        binding.list.adapter = adapter

        adapter.setNewInstance(
            data?.parsedLyric?.datum?.toMutableList() ?: mutableListOf()
        )
    }

    override fun initListeners() {
        super.initListeners()
        adapter.setOnItemClickListener { _, _, position ->
            adapter.setSelected(position, !adapter.isSelected(position))
        }

        binding.shareLyric.setOnClickListener {
            val lyricString = getSelectLyricString("，")

            if (TextUtils.isEmpty(lyricString)) {
                SuperToast.show(R.string.hint_select_lyric)
                return@setOnClickListener
            }

            Timber.d("share text %s", lyricString)

            data?.let { song ->
                ShareUtil.shareLyricText(hostActivity, song, lyricString)
            }
        }

        binding.shareLyricImage.setOnClickListener {
            val lyricString = getSelectLyricString("\n")

            if (TextUtils.isEmpty(lyricString)) {
                SuperToast.show(R.string.hint_select_lyric)
                return@setOnClickListener
            }

            data?.let { song ->
                ShareLyricImageActivity.start(hostActivity, song, lyricString)
            }
        }
    }

    /**
     * 获取选择的歌词文本
     */
    private fun getSelectLyricString(separator: String): String {
        val lyrics = mutableListOf<String>()
        val selectedIndexes = adapter.getSelectedIndexes()

        for (i in selectedIndexes.indices) {
            if (selectedIndexes[i] == SELECTED) {
                adapter.getItemOrNull(i)?.data?.let { lyric ->
                    lyrics.add(lyric)
                }
            }
        }

        return StringUtils.join(lyrics, separator)
    }

    companion object {
        private const val SELECTED = 1
    }
}
