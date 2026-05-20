package com.ixuea.courses.mymusic.component.sheet.model

import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.component.user.model.User
import com.ixuea.courses.mymusic.model.Common
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity
import com.ixuea.courses.mymusic.util.Constant

/**
 * 歌单模型
 */
class Sheet : Common(), BaseMultiItemEntity {
    var title: String? = null
    var icon: String? = null
    var detail: String? = null
    var clicksCount: Int = 0
    var collectsCount: Int = 0
    var commentsCount: Int = 0
    var songsCount: Int = 0
    var songs: ArrayList<Song>? = null
    var user: User? = null
    var collectId: String? = null

    val isCollect: Boolean
        get() = collectId != null

    override val itemType: Int
        get() = Constant.STYLE_SHEET
}
