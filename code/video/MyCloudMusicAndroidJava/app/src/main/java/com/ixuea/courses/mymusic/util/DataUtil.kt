package com.ixuea.courses.mymusic.util

import com.ixuea.courses.mymusic.component.song.model.Song

object DataUtil {
    @JvmStatic
    fun changePlayListFlag(datum: List<Song>, value: Boolean) {
        for (data in datum) {
            data.isPlayList = value
        }
    }
}
