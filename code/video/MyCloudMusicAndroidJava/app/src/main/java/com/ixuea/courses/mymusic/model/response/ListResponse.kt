package com.ixuea.courses.mymusic.model.response

/**
 * 解析列表网络请求。
 */
class ListResponse<T> : BaseResponse() {
    var data: Meta<T>? = null
}
