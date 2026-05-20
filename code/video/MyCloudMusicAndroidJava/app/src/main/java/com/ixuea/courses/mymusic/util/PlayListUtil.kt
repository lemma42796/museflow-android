package com.ixuea.courses.mymusic.util

import android.widget.TextView
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.manager.MusicListManager

/**
 * 播放列表工具类
 */
object PlayListUtil {
    /**
     * 显示循环模式
     */
    @JvmStatic
    fun showLoopModel(loopModel: Int, view: TextView) {
        when (loopModel) {
            MusicListManager.MODEL_LOOP_LIST -> view.text = "列表循环"
            MusicListManager.MODEL_LOOP_RANDOM -> view.text = "随机循环"
            else -> view.text = "单曲循环"
        }
    }

    /**
     * 根据循环模式返回对应的图标
     */
    @JvmStatic
    fun getLoopModelIcon(data: Int): Int {
        return when (data) {
            MusicListManager.MODEL_LOOP_RANDOM -> R.drawable.music_repeat_random
            MusicListManager.MODEL_LOOP_ONE -> R.drawable.music_repeat_one
            else -> R.drawable.music_repeat_list
        }
    }
}
