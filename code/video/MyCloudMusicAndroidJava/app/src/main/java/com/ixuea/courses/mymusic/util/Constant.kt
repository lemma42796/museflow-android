package com.ixuea.courses.mymusic.util

import android.provider.MediaStore

/**
 * 常量类
 */
object Constant {
    const val ACTION_LOGIN: String = "ACTION_LOGIN"
    const val ID: String = "id"

    const val STYLE_BANNER: Int = 0
    const val STYLE_BUTTON: Int = 1
    const val STYLE_SHEET: Int = 2
    const val STYLE_SONG: Int = 3
    const val STYLE_TITLE: Int = 4
    const val STYLE_FOOTER: Int = 5
    const val STYLE_CATEGORY: Int = 6
    const val STYLE_VIDEO: Int = 7
    const val STYLE_COMMENT: Int = 8
    const val STYLE_USER: Int = 9
    const val STYLE_CONFIRM_ORDER: Int = 10
    const val STYLE_ORDER: Int = 11
    const val STYLE_FRIEND: Int = 12
    const val STYLE_FANS: Int = 13
    const val STYLE_FRIEND_SELECT: Int = 14

    const val VALUE_NO: Int = -1
    const val VALUE0: Int = 0
    const val VALUE10: Int = 10
    const val VALUE20: Int = 20
    const val VALUE30: Int = 30

    const val SIZE12: Int = 12
    const val SIZE10: Int = 10

    const val TITLE_KEY: String = "title"
    const val URL: String = "url"
    const val DATA: String = "data"
    const val STYLE: String = "style"

    const val STYLE_PHONE_LOGIN: Int = VALUE0
    const val STYLE_FORGOT_PASSWORD: Int = VALUE10

    const val ANONYMOUS: String = "anonymous"

    /**
     * 用户详情昵称查询字段
     */
    const val NICKNAME: String = "nickname"
    const val AD: String = "ad"
    const val PUSH: String = "push"

    /**
     * 广告点击了
     */
    const val ACTION_AD: String = "com.ixuea.courses.mymusic.ACTION_AD"

    /**
     * 打开音乐播放界面
     */
    const val ACTION_MUSIC_PLAYER_PAGE: String = "com.ixuea.courses.mymusic.ACTION_MUSIC_PLAYER_PAGE"

    const val ACTION_UNLOCK_LYRIC: String = "com.ixuea.courses.mymusic.ACTION_UNLOCK_LYRIC"

    /**
     * 歌词操作
     */
    const val ACTION_LYRIC: String = "com.ixuea.courses.mymusic.ACTION_LYRIC"
    const val ACTION_PLAY: String = "com.ixuea.courses.mymusic.ACTION_PLAY"
    const val ACTION_PREVIOUS: String = "com.ixuea.courses.mymusic.ACTION_PREVIOUS"
    const val ACTION_NEXT: String = "com.ixuea.courses.mymusic.ACTION_NEXT"
    const val ACTION_SCAN: String = "com.ixuea.courses.mymusic.ACTION_SCAN"
    const val ACTION_SEARCH: String = "com.ixuea.courses.mymusic.ACTION_SEARCH"
    const val ACTION_CHAT: String = "com.ixuea.courses.mymusic.ACTION_CHAT"
    const val ACTION_PUSH: String = "com.ixuea.courses.mymusic.ACTION_PUSH"

    /**
     * 保持播放进度间隔（毫秒）
     */
    const val SAVE_PROGRESS_TIME: Long = 2000

    /**
     * LRC歌词
     */
    const val LRC: Int = 0

    /**
     * KSC歌词
     */
    const val KSC: Int = 10

    /**
     * 隐藏歌词拖拽时间
     */
    const val LYRIC_HIDE_DRAG_TIME: Long = 4000

    /**
     * Android 媒体库本地音乐查询条件。
     */
    @JvmField
    val MEDIA_AUDIO_SELECTION: String =
        MediaStore.Audio.AudioColumns.IS_MUSIC + " != 0 AND " +
            MediaStore.Audio.AudioColumns.SIZE + " >= ? AND " +
            MediaStore.Audio.AudioColumns.DURATION + " >= ?"

    /**
     * 1M
     */
    const val MUSIC_FILTER_SIZE: Int = 1 * 1024 * 1024

    /**
     * 60s
     */
    const val MUSIC_FILTER_DURATION: Int = 60 * 1000

    /**
     * 扫描本地音乐放大镜圆周半径
     */
    const val DEFAULT_RADIUS_SCAN_LOCAL_MUSIC_ZOOM: Double = 30.0
    const val SHEET_ID: String = "sheet_id"
    const val PAGE: String = "page"
    const val USER_ID: String = "user_id"
    const val SEPARATOR: String = "，"

    /**
     * 搜索接口查询关键字
     */
    const val QUERY: String = "query"

    const val REQUEST_OVERLAY_PERMISSION: Int = 100

    /**
     * 解锁全局歌词Id
     */
    const val NOTIFICATION_UNLOCK_LYRIC_ID: Int = 10001

    /**
     * 每页返回数量。
     */
    const val DEFAULT_SIZE: Int = 10

    /**
     * 默认页码
     */
    const val DEFAULT_PAGE: Int = 1

    const val ANDROID: Int = 0
    const val IOS: Int = 10
    const val WEB: Int = 20
    const val WAP: Int = 30

    const val PACKAGE_MAP_TENCENT: String = "com.tencent.map"
    const val PACKAGE_MAP_BAIDU: String = "com.baidu.BaiduMap"
    const val PACKAGE_MAP_AMAP: String = "com.autonavi.minimap"
    const val PACKAGE_MAP_SOGOU: String = "com.tencent.map"

    const val WAIT_PAY: Int = 0
    const val CLOSE: Int = 10
    const val WAIT_SHIPPED: Int = 500
    const val WAIT_RECEIVED: Int = 510
    const val WAIT_COMMENT: Int = 520
    const val COMPLETE: Int = 530

    const val ALIPAY: Int = 10
    const val WECHAT: Int = 20
    const val HUABEI_STAGE: Int = 30

    const val TEXT_LEFT: Int = 100
    const val TEXT_RIGHT: Int = 110
    const val IMAGE_LEFT: Int = 120
    const val IMAGE_RIGHT: Int = 130

    /**
     * 聊天消息每页获取数量
     */
    const val DEFAULT_MESSAGE_COUNT: Int = 10

    /**
     * 加盐格式化字符串
     */
    const val SALT_START: String = "wt5j1URZ1H6RDtt"

    /**
     * 加盐格式化字符串
     */
    const val SALT_END: String = "uWg7x2E0Mr5Xwzm"

    /**
     * 请求/响应签名key
     */
    const val HEADER_SIGN: String = "Sign"
}
