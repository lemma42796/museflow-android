package com.ixuea.courses.mymusic.manager

import android.content.Context
import com.ixuea.courses.mymusic.component.user.model.User
import com.ixuea.courses.mymusic.component.user.repository.UserRepository

/**
 * 用户管理器。
 */
class UserManager @Suppress("UNUSED_PARAMETER") constructor(context: Context) {
    private val repository = UserRepository.getInstance()

    /**
     * 获取用户。
     */
    fun getUser(userId: String, userListener: UserListener) {
        val cachedUser = repository.cachedUser(userId)
        if (cachedUser != null) {
            userListener.onGetUserSuccess(cachedUser)
            return
        }

        repository.userDetail(userId)
            .subscribe(
                { response ->
                    val user = response.data ?: return@subscribe
                    userListener.onGetUserSuccess(user)
                },
                {
                    // Legacy callback API has no error channel.
                },
            )
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
