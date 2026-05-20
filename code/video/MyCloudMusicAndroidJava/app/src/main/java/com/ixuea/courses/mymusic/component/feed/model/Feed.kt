package com.ixuea.courses.mymusic.component.feed.model

import com.ixuea.courses.mymusic.component.comment.model.Comment
import com.ixuea.courses.mymusic.component.user.model.User
import com.ixuea.courses.mymusic.model.Common
import com.ixuea.courses.mymusic.model.Resource

/**
 * 动态
 */
class Feed : Common() {
    var content: String? = null
    var province: String? = null
    var provinceCode: String? = null
    var city: String? = null
    var cityCode: String? = null
    var area: String? = null
    var areaCode: String? = null
    var position: String? = null
    var address: String? = null
    var longitude: Double? = null
    var latitude: Double? = null
    var user: User? = null
    var medias: List<Resource>? = null
    var likes: List<User>? = null
    var comments: List<Comment>? = null
}
