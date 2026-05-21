package com.ixuea.courses.mymusic.util

import android.content.Context
import com.ixuea.courses.mymusic.component.ad.model.Ad
import com.ixuea.superui.util.DensityUtil
import com.tencent.mmkv.MMKV
import org.apache.commons.lang3.StringUtils

/**
 * 偏好设置工具类
 */
class PreferenceUtil(context: Context) {
    private val context: Context = context.applicationContext
    private val preference: MMKV = MMKV.defaultMMKV()

    /**
     * 是否显示引导界面。
     */
    var isShowGuide: Boolean
        get() = getBoolean(SHOW_GUIDE, true)
        set(value) {
            putBoolean(SHOW_GUIDE, value)
        }

    /**
     * 登录 session。
     */
    var session: String?
        get() = preference.getString(SESSION, null)
        set(value) {
            putString(SESSION, value)
        }

    /**
     * 用户 Id。
     */
    var userId: String
        get() = preference.getString(USER_ID, Constant.ANONYMOUS) ?: Constant.ANONYMOUS
        set(value) {
            putString(USER_ID, value)
        }

    /**
     * 聊天 token。
     */
    var chatToken: String?
        get() = preference.getString(USER_CHAT_TOKEN, null)
        set(value) {
            putString(USER_CHAT_TOKEN, value)
        }

    /**
     * 是否登录了。
     */
    val isLogin: Boolean
        get() = Constant.ANONYMOUS != userId

    /**
     * 启动界面广告。
     */
    var splashAd: Ad?
        get() {
            val result = preference.getString(SPLASH_AD, null)
            if (StringUtils.isBlank(result)) {
                return null
            }
            return JSONUtil.fromJSON(result, Ad::class.java)
        }
        set(data) {
            if (data == null) {
                delete(SPLASH_AD)
            } else {
                putString(SPLASH_AD, JSONUtil.toJSON(data))
            }
        }

    /**
     * 最后播放的音乐 Id。
     */
    var lastPlaySongId: String?
        get() = preference.getString(lastPlaySongIdKey, null)
        set(data) {
            preference.edit().putString(lastPlaySongIdKey, data).apply()
        }

    private val lastPlaySongIdKey: String
        get() = String.format("%s%s", userId, LAST_PLAY_SONG_ID)

    /**
     * 本地音乐排序。
     */
    var localMusicSortIndex: Int
        get() = getInt(KEY_LOCAL_MUSIC_SORT, 0)
        set(data) {
            putInt(KEY_LOCAL_MUSIC_SORT, data)
        }

    /**
     * 全局歌词颜色索引。
     */
    var globalLyricTextColorIndex: Int
        get() = getInt(KEY_GLOBAL_LYRIC_TEXT_COLOR, 0)
        set(index) {
            putInt(KEY_GLOBAL_LYRIC_TEXT_COLOR, index)
        }

    /**
     * 全局歌词大小，默认 18sp。
     */
    var globalLyricTextSize: Int
        get() = getInt(KEY_GLOBAL_LYRIC_TEXT_SIZE, DensityUtil.dip2px(context, 18f).toInt())
        set(data) {
            putInt(KEY_GLOBAL_LYRIC_TEXT_SIZE, data)
        }

    /**
     * 全局歌词 y 坐标。
     */
    var globalLyricViewY: Int
        get() = getInt(KEY_GLOBAL_LYRIC_VIEW_Y, 0)
        set(data) {
            putInt(KEY_GLOBAL_LYRIC_VIEW_Y, data)
        }

    /**
     * 是否显示全局歌词。
     */
    var isShowGlobalLyric: Boolean
        get() = getBoolean(KEY_SHOW_GLOBAL_LYRIC, false)
        set(data) {
            putBoolean(KEY_SHOW_GLOBAL_LYRIC, data)
        }

    /**
     * 全局歌词是否锁定。
     */
    var isGlobalLyricLock: Boolean
        get() = getBoolean(KEY_GLOBAL_LYRIC_LOCK, false)
        set(data) {
            putBoolean(KEY_GLOBAL_LYRIC_LOCK, data)
        }

    /**
     * 获取排序。
     */
    fun getSort(style: Int): Int {
        return getInt(getSortKey(style), style)
    }

    private fun getSortKey(style: Int): String {
        return String.format("sort_%d", style)
    }

    fun setSort(style: Int, data: Int) {
        putInt(getSortKey(style), data)
    }

    fun logout() {
        delete(USER_ID)
        delete(SESSION)
        delete(USER_CHAT_TOKEN)
    }

    private fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return preference.getBoolean(key, defaultValue)
    }

    private fun putBoolean(key: String, value: Boolean) {
        preference.edit().putBoolean(key, value).apply()
    }

    private fun getInt(key: String, defaultValue: Int): Int {
        return preference.getInt(key, defaultValue)
    }

    private fun putInt(key: String, data: Int) {
        preference.edit().putInt(key, data).apply()
    }

    private fun putString(key: String, value: String?) {
        preference.edit().putString(key, value).apply()
    }

    private fun delete(data: String) {
        preference.edit().remove(data).commit()
    }

    companion object {
        private const val SHOW_GUIDE = "SHOW_GUIDE"
        private const val SESSION = "session"
        private const val USER_ID = "user_id"
        private const val USER_CHAT_TOKEN = "user_chat_token"
        private const val SPLASH_AD = "SPLASH_AD"
        private const val KEY_LOCAL_MUSIC_SORT = "LOCAL_MUSIC_SORT"
        private const val LAST_PLAY_SONG_ID = "LAST_PLAY_SONG_ID"
        private const val KEY_GLOBAL_LYRIC_TEXT_COLOR = "GLOBAL_LYRIC_TEXT_COLOR"
        private const val KEY_GLOBAL_LYRIC_TEXT_SIZE = "GLOBAL_LYRIC_TEXT_SIZE"
        private const val KEY_GLOBAL_LYRIC_VIEW_Y = "GLOBAL_LYRIC_VIEW_Y"
        private const val KEY_SHOW_GLOBAL_LYRIC = "SHOW_GLOBAL_LYRIC"
        private const val KEY_GLOBAL_LYRIC_LOCK = "GLOBAL_LYRIC_LOCK"

        @Volatile
        private var instance: PreferenceUtil? = null

        /**
         * 获取偏好设置单例。
         */
        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): PreferenceUtil {
            if (instance == null) {
                instance = PreferenceUtil(context)
            }
            return instance!!
        }
    }
}
