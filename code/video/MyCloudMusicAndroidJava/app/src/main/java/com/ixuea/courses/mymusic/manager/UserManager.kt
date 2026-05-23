package com.ixuea.courses.mymusic.manager

import android.content.Context
import com.ixuea.courses.mymusic.component.user.domain.LoadUserDetailUseCase
import com.ixuea.courses.mymusic.component.user.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 用户管理器。
 */
class UserManager @Suppress("UNUSED_PARAMETER") constructor(context: Context) {
    private val loadUserDetail = LoadUserDetailUseCase()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    /**
     * 获取用户。
     */
    fun getUser(userId: String, userListener: UserListener) {
        scope.launch {
            when (val result = loadUserDetail(userId)) {
                is LoadUserDetailUseCase.Result.Success -> {
                    userListener.onGetUserSuccess(result.user)
                }

                is LoadUserDetailUseCase.Result.Error -> {
                    // Legacy callback API has no error channel.
                }
            }
        }
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
