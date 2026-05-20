package com.ixuea.courses.mymusic.component.conversation.domain

import android.content.Context
import com.ixuea.courses.mymusic.component.user.model.User
import com.ixuea.courses.mymusic.manager.UserManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LoadConversationUserUseCase {
    suspend operator fun invoke(context: Context, userId: String): User {
        return suspendCancellableCoroutine { continuation ->
            UserManager.getInstance(context.applicationContext).getUser(userId) { user ->
                if (continuation.isActive) {
                    continuation.resume(user)
                }
            }
        }
    }
}
