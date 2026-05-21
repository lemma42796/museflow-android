package com.ixuea.courses.mymusic.manager

import android.content.Context
import com.ixuea.courses.mymusic.component.api.HttpObserver
import com.ixuea.courses.mymusic.component.user.model.User
import com.ixuea.courses.mymusic.model.response.DetailResponse
import com.ixuea.courses.mymusic.repository.DefaultRepository

/**
 * 用户管理器。
 */
class UserManager(context: Context) {
    private val context: Context = context.applicationContext
    private val userCaches: MutableMap<String, User> = HashMap()

    /**
     * 获取用户。
     */
    fun getUser(userId: String, userListener: UserListener) {
        val cachedUser = userCaches[userId]
        if (cachedUser != null) {
            userListener.onGetUserSuccess(cachedUser)
            return
        }

        DefaultRepository.getInstance()
            .userDetail(userId)
            .subscribe(object : HttpObserver<DetailResponse<User>>() {
                override fun onSucceeded(data: DetailResponse<User>) {
                    val user = data.data ?: return
                    userListener.onGetUserSuccess(user)
                    userCaches[userId] = user
                }
            })
    }

    fun interface UserListener {
        /**
         * 用户获取成功。
         */
        fun onGetUserSuccess(data: User)
    }

    companion object {
        @Volatile
        private var instance: UserManager? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): UserManager {
            if (instance == null) {
                instance = UserManager(context)
            }
            return instance!!
        }
    }
}
