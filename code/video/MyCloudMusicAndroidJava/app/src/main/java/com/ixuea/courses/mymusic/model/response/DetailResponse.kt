package com.ixuea.courses.mymusic.model.response

/**
 * 详情网络请求解析类。
 */
class DetailResponse<T> : BaseResponse() {
    var data: T? = null
}
