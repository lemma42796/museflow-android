package com.ixuea.courses.mymusic.component.discovery.model.ui

import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity
import com.ixuea.courses.mymusic.util.Constant

/**
 * 发现界面，单曲外层容器数据
 */
class SongData(
    var data: MutableList<Song>,
    sort: Int,
) : BaseSort(sort), BaseMultiItemEntity {
    override val itemType: Int
        get() = Constant.STYLE_SONG
}
