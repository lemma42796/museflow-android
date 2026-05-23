package com.ixuea.courses.mymusic.repository

import com.ixuea.courses.mymusic.component.ad.model.Ad
import com.ixuea.courses.mymusic.component.api.DefaultService
import com.ixuea.courses.mymusic.component.api.NetworkModule
import com.ixuea.courses.mymusic.component.comment.model.Comment
import com.ixuea.courses.mymusic.component.feed.model.Feed
import com.ixuea.courses.mymusic.component.login.model.Session
import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.component.user.model.User
import com.ixuea.courses.mymusic.model.Base
import com.ixuea.courses.mymusic.model.BaseId
import com.ixuea.courses.mymusic.model.Resource
import com.ixuea.courses.mymusic.model.response.DetailResponse
import com.ixuea.courses.mymusic.model.response.ListResponse
import com.ixuea.courses.mymusic.util.Constant
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Public slim repository for refactored music, discovery, feed, comments, user,
 * download, and chat paths.
 */
class DefaultRepository {
    private val service: DefaultService =
        NetworkModule.provideRetrofit(NetworkModule.provideOkHttpClient())
            .create(DefaultService::class.java)

    suspend fun ads(position: Int): ListResponse<Ad> {
        return service.ads(position)
    }

    suspend fun bannerAd(): ListResponse<Ad> {
        return ads(Constant.VALUE0)
    }

    suspend fun splashAd(): ListResponse<Ad> {
        return ads(Constant.VALUE10)
    }

    suspend fun sheets(category: String?): ListResponse<Sheet> {
        return sheets(category, Constant.SIZE10)
    }

    suspend fun sheets(size: Int): ListResponse<Sheet> {
        return sheets(null, size)
    }

    suspend fun sheets(category: String?, size: Int): ListResponse<Sheet> {
        return service.sheets(category, size)
    }

    suspend fun sheetDetail(id: String): DetailResponse<Sheet> {
        return service.sheetDetail("testHeader", id)
    }

    suspend fun createSheets(userId: String): ListResponse<Sheet> {
        return service.createSheets(userId)
    }

    suspend fun collectSheets(userId: String): ListResponse<Sheet> {
        return service.collectSheets(userId)
    }

    suspend fun createSheet(data: Sheet): DetailResponse<Sheet> {
        return service.createSheet(data)
    }

    suspend fun songs(): ListResponse<Song> {
        return service.songs()
    }

    suspend fun songDetail(id: String): DetailResponse<Song> {
        return service.songDetail(id)
    }

    suspend fun login(data: User): DetailResponse<Session> {
        return service.login(data)
    }

    suspend fun userDetail(id: String, nickname: String?): DetailResponse<User> {
        val data = hashMapOf<String, String>()
        if (!nickname.isNullOrBlank()) {
            data[Constant.NICKNAME] = nickname
        }

        return service.userDetail(id, data)
    }

    suspend fun userDetail(id: String): DetailResponse<User> {
        return userDetail(id, null)
    }

    suspend fun friends(id: String): ListResponse<User> {
        return service.friends(id)
    }

    suspend fun fans(id: String): ListResponse<User> {
        return service.fans(id)
    }

    suspend fun updateUser(id: String, data: User): DetailResponse<Base> {
        return service.updateUser(id, data)
    }

    suspend fun follow(userId: String): DetailResponse<BaseId> {
        return service.follow(mapOf("id" to userId))
    }

    suspend fun deleteFollow(userId: String): DetailResponse<BaseId> {
        return service.deleteFollow(userId)
    }

    suspend fun register(data: User): DetailResponse<BaseId> {
        return service.register(data)
    }

    suspend fun collect(id: String): DetailResponse<Base> {
        return service.collect(mapOf("sheet_id" to id))
    }

    suspend fun deleteCollect(id: String): DetailResponse<Base> {
        return service.deleteCollect(id)
    }

    suspend fun comments(data: Map<String, String>): ListResponse<Comment> {
        return service.comments(data)
    }

    suspend fun commentLike(data: String): DetailResponse<BaseId> {
        return service.like(mapOf("comment_id" to data))
    }

    suspend fun cancelCommentLike(data: String): DetailResponse<Base> {
        return service.cancelLike(data, Constant.VALUE0)
    }

    suspend fun createComment(data: Comment): DetailResponse<Comment> {
        return service.createComment(data)
    }

    suspend fun feeds(userId: String?): ListResponse<Feed> {
        val datum = hashMapOf<String, String>()
        if (!userId.isNullOrBlank()) {
            datum[Constant.USER_ID] = userId
        }

        return service.feeds(datum)
    }

    suspend fun createFeed(data: Feed): DetailResponse<Base> {
        return service.createFeed(data)
    }

    suspend fun uploadFile(
        data: MultipartBody.Part,
        flavor: RequestBody
    ): DetailResponse<Resource> {
        return service.uploadFile(data, flavor)
    }

    suspend fun uploadFiles(
        data: List<MultipartBody.Part>,
        flavor: RequestBody
    ): ListResponse<Resource> {
        return service.uploadFiles(data, flavor)
    }

    companion object {
        @Volatile
        private var instance: DefaultRepository? = null

        @JvmStatic
        @Synchronized
        fun getInstance(): DefaultRepository {
            return instance ?: DefaultRepository().also {
                instance = it
            }
        }
    }
}
