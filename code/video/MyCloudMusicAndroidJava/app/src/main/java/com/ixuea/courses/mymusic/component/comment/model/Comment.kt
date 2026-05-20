package com.ixuea.courses.mymusic.component.comment.model

import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import com.ixuea.courses.mymusic.component.user.model.User
import com.ixuea.courses.mymusic.model.Common
import org.apache.commons.lang3.StringUtils

/**
 * 评论模型
 */
class Comment : Common() {
    var content: String? = null
    var likesCount: Long = 0
    var sheet: Sheet? = null
    var likeId: String? = null
    var parent: Comment? = null
    var parentId: String? = null
    var sheetId: String? = null
    var feedId: String? = null
    var user: User? = null

    val isLiked: Boolean
        get() = StringUtils.isNotBlank(likeId)
}
