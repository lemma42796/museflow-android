package com.ixuea.courses.mymusic.model.response

import com.ixuea.courses.mymusic.component.comment.model.Comment

/**
 * 解析列表网络请求。
 */
class Meta<T> {
    var total: Int? = null
    var pages: Int? = null
    var size: Int? = null
    var page: Int? = null
    var next: Int? = null
    var data: List<T>? = null

    companion object {
        /**
         * 获取下一页。
         */
        @JvmStatic
        fun nextPage(data: Meta<Comment>?): Int {
            return data?.next ?: 1
        }
    }
}
