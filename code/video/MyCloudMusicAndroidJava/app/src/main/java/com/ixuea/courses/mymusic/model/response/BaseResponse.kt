package com.ixuea.courses.mymusic.model.response

/**
 * 通用网络请求响应模型
 */
open class BaseResponse {
    /**
     * 状态码，等于 0 表示成功。
     */
    var status: Int = 0

    /**
     * 出错的提示信息，发生了错误不一定有。
     */
    var message: String? = null

    fun isSucceeded(): Boolean {
        return status == 0
    }
}
