package com.ixuea.courses.mymusic.exception

import com.ixuea.courses.mymusic.model.response.BaseResponse

/**
 * 网络响应错误。
 *
 * data 字段就是网络响应返回的数据，PagingSource 等场景可以统一封装为 Throwable。
 */
class ResponseException(
    var data: BaseResponse?,
) : RuntimeException() {
    companion object {
        @JvmStatic
        fun create(data: BaseResponse?): Throwable {
            return ResponseException(data)
        }
    }
}
