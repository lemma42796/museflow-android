package com.ixuea.courses.mymusic.config

import com.ixuea.courses.mymusic.BuildConfig

/**
 * 配置文件
 *
 * 例如：API地址，QQ等第三方服务配置信息等。
 */
object Config {
    /**
     * 默认延时时间
     */
    const val SPLASH_DEFAULT_DELAY_TIME = 1000L

    /**
     * 是否是调试模式
     */
    @JvmField
    val DEBUG: Boolean = BuildConfig.DEBUG

    /**
     * 端点
     */
    @JvmField
    var ENDPOINT: String = BuildConfig.ENDPOINT

    /**
     * 资源端点
     */
    @JvmField
    var RESOURCE_ENDPOINT: String = BuildConfig.RESOURCE_ENDPOINT

    /**
     * 网络缓存目录大小，100M。
     */
    const val NETWORK_CACHE_SIZE = 1024L * 1024L * 100L

    /**
     * 阿里云OSS AK
     */
    const val ALIYUN_AK = ""

    /**
     * 阿里云OSS SK
     */
    const val ALIYUN_SK = ""

    /**
     * 阿里云OSS Bucket
     */
    const val ALIYUN_OSS_BUCKET_NAME = "dev-courses-misuc"

    /**
     * 阿里云OSS Bucket 地址
     * https://help.aliyun.com/document_detail/31837.html
     */
    const val BUCKET_ENDPOINT = "oss-cn-beijing.aliyuncs.com"

    /**
     * 聊天key
     */
    const val IM_KEY = ""

    /**
     * 二维码地址
     *
     * 真实项目中一般设置为应用的下载宣传界面，因为目前没有这样的界面，所以就设置为官网地址。
     */
    const val QRCODE_URL = "http://www.ixuea.com"

    /**
     * 用户二维码
     */
    const val USER_QRCODE_URL = "http://www.ixuea.com?u=%s"
}
