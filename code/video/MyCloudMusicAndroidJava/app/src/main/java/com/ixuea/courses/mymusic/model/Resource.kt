package com.ixuea.courses.mymusic.model

/**
 * 资源。将资源放到单独对象中，方便后续扩展类型、大小、备注等字段。
 */
class Resource @JvmOverloads constructor(
    var uri: String? = null,
) : Base() {
    /**
     * 类型，0：图片；10：视频。
     */
    var style: Int = 0
}
