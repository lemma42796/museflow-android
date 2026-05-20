package com.ixuea.courses.mymusic.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * 偏好设置工具类
 * 是否登录了，是否显示引导界面，用户Id
 */
class DefaultPreferenceUtil(context: Context) {
    private val context: Context = context.applicationContext
    private val preference: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(this.context)

    /**
     * 设置同意了用户协议
     */
    fun setAcceptTermsServiceAgreement() {
        putBoolean(TERMS_SERVICE, true)
    }

    /**
     * 获取是否同意了用户条款
     */
    fun isAcceptTermsServiceAgreement(): Boolean {
        return preference.getBoolean(TERMS_SERVICE, false)
    }

    /**
     * 保存boolean
     */
    private fun putBoolean(key: String, value: Boolean) {
        preference.edit().putBoolean(key, value).apply()
    }

    /**
     * 移动网络下是否播放
     *
     * @return 默认不能播放
     */
    fun isMobileNetworkPlay(): Boolean {
        return preference.getBoolean(KEY_MOBILE_NETWORK_PLAY, false)
    }

    companion object {
        /**
         * 偏好设置文件名称
         */
        private const val NAME = "ixuea_my_cloud_music"
        private const val KEY_MOBILE_NETWORK_PLAY = "mobile_network_play"
        private const val TERMS_SERVICE = "TERMS_SERVICE"

        private var instance: DefaultPreferenceUtil? = null

        /**
         * 获取偏好设置单例
         */
        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): DefaultPreferenceUtil {
            if (instance == null) {
                instance = DefaultPreferenceUtil(context)
            }
            return instance!!
        }
    }
}
