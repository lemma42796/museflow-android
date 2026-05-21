package com.ixuea.courses.mymusic.manager

import android.app.Activity
import com.ixuea.courses.mymusic.MainActivity
import com.ixuea.courses.mymusic.component.login.activity.LoginHomeActivity
import java.util.LinkedList

/**
 * 界面管理器。
 *
 * 保存当前应用所有开启的界面，方便关闭到指定界面。
 */
class MyActivityManager private constructor() {
    /**
     * 添加界面。
     */
    fun add(activity: Activity) {
        activities.add(activity)
    }

    /**
     * 移除界面。
     */
    fun remove(activity: Activity) {
        activities.remove(activity)
    }

    /**
     * 关闭所有界面。
     */
    fun finishAll() {
        val iterator = activities.iterator()
        while (iterator.hasNext()) {
            iterator.next().finish()
        }
    }

    /**
     * 关闭到主界面。
     */
    fun finishToMain() {
        val iterator = LinkedList(activities).descendingIterator()
        while (iterator.hasNext()) {
            val activity = iterator.next()
            if (activity is MainActivity) {
                break
            }
            activity.finish()
        }
    }

    fun finishAllLogin() {
        val iterator = LinkedList(activities).descendingIterator()
        while (iterator.hasNext()) {
            val activity = iterator.next()
            if (activity is LoginHomeActivity) {
                activity.finish()
            }
        }
    }

    companion object {
        private val activities = LinkedHashSet<Activity>()

        @Volatile
        private var instance: MyActivityManager? = null

        /**
         * 获取实例。
         */
        @JvmStatic
        fun getInstance(): MyActivityManager {
            return instance ?: synchronized(this) {
                instance ?: MyActivityManager().also {
                    instance = it
                }
            }
        }
    }
}
