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
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
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

    fun ads(position: Int): Observable<ListResponse<Ad>> {
        return service.ads(position).applySchedulers()
    }

    fun bannerAd(): Observable<ListResponse<Ad>> {
        return ads(Constant.VALUE0)
    }

    fun splashAd(): Observable<ListResponse<Ad>> {
        return ads(Constant.VALUE10)
    }

    fun sheets(category: String?): Observable<ListResponse<Sheet>> {
        return sheets(category, Constant.SIZE10)
    }

    fun sheets(size: Int): Observable<ListResponse<Sheet>> {
        return sheets(null, size)
    }

    fun sheets(category: String?, size: Int): Observable<ListResponse<Sheet>> {
        return service.sheets(category, size).applySchedulers()
    }

    fun sheetDetail(id: String): Observable<DetailResponse<Sheet>> {
        return service.sheetDetail("testHeader", id).applySchedulers()
    }

    fun createSheets(userId: String): Observable<ListResponse<Sheet>> {
        return service.createSheets(userId).applySchedulers()
    }

    fun collectSheets(userId: String): Observable<ListResponse<Sheet>> {
        return service.collectSheets(userId).applySchedulers()
    }

    fun createSheet(data: Sheet): Observable<DetailResponse<Sheet>> {
        return service.createSheet(data).applySchedulers()
    }

    fun songs(): Observable<ListResponse<Song>> {
        return service.songs().applySchedulers()
    }

    fun songDetail(id: String): Observable<DetailResponse<Song>> {
        return service.songDetail(id).applySchedulers()
    }

    fun login(data: User): Observable<DetailResponse<Session>> {
        return service.login(data).applySchedulers()
    }

    fun userDetail(id: String, nickname: String?): Observable<DetailResponse<User>> {
        val data = hashMapOf<String, String>()
        if (!nickname.isNullOrBlank()) {
            data[Constant.NICKNAME] = nickname
        }

        return service.userDetail(id, data).applySchedulers()
    }

    fun userDetail(id: String): Observable<DetailResponse<User>> {
        return userDetail(id, null)
    }

    fun friends(id: String): Observable<ListResponse<User>> {
        return service.friends(id).applySchedulers()
    }

    fun fans(id: String): Observable<ListResponse<User>> {
        return service.fans(id).applySchedulers()
    }

    fun updateUser(id: String, data: User): Observable<DetailResponse<Base>> {
        return service.updateUser(id, data).applySchedulers()
    }

    fun follow(userId: String): Observable<DetailResponse<BaseId>> {
        return service.follow(mapOf("id" to userId)).applySchedulers()
    }

    fun deleteFollow(userId: String): Observable<DetailResponse<BaseId>> {
        return service.deleteFollow(userId).applySchedulers()
    }

    fun register(data: User): Observable<DetailResponse<BaseId>> {
        return service.register(data).applySchedulers()
    }

    fun collect(id: String): Observable<DetailResponse<Base>> {
        return service.collect(mapOf("sheet_id" to id)).applySchedulers()
    }

    fun deleteCollect(id: String): Observable<DetailResponse<Base>> {
        return service.deleteCollect(id).applySchedulers()
    }

    fun comments(data: Map<String, String>): Observable<ListResponse<Comment>> {
        return service.comments(data).applySchedulers()
    }

    fun commentLike(data: String): Observable<DetailResponse<BaseId>> {
        return service.like(mapOf("comment_id" to data)).applySchedulers()
    }

    fun cancelCommentLike(data: String): Observable<DetailResponse<Base>> {
        return service.cancelLike(data, Constant.VALUE0).applySchedulers()
    }

    fun createComment(data: Comment): Observable<DetailResponse<Comment>> {
        return service.createComment(data).applySchedulers()
    }

    fun feeds(userId: String?): Observable<ListResponse<Feed>> {
        val datum = hashMapOf<String, String>()
        if (!userId.isNullOrBlank()) {
            datum[Constant.USER_ID] = userId
        }

        return service.feeds(datum).applySchedulers()
    }

    fun createFeed(data: Feed): Observable<DetailResponse<Base>> {
        return service.createFeed(data).applySchedulers()
    }

    fun uploadFile(
        data: MultipartBody.Part,
        flavor: RequestBody
    ): Observable<DetailResponse<Resource>> {
        return service.uploadFile(data, flavor).applySchedulers()
    }

    fun uploadFiles(
        data: List<MultipartBody.Part>,
        flavor: RequestBody
    ): Observable<ListResponse<Resource>> {
        return service.uploadFiles(data, flavor).applySchedulers()
    }

    private fun <T : Any> Observable<T>.applySchedulers(): Observable<T> {
        return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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
