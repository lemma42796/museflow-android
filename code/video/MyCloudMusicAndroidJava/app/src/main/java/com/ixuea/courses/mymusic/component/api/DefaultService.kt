package com.ixuea.courses.mymusic.component.api

import com.ixuea.courses.mymusic.component.ad.model.Ad
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
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * Public slim remote API for the refactored paths.
 */
interface DefaultService {
    @GET("v1/sheets")
    fun sheets(
        @Query("category") category: String?,
        @Query("size") size: Int,
    ): Observable<ListResponse<Sheet>>

    @GET("v1/sheets/{id}")
    fun sheetDetail(
        @Header("testHeader") testHeader: String,
        @Path("id") id: String,
    ): Observable<DetailResponse<Sheet>>

    @GET("v1/users/{userId}/create")
    fun createSheets(@Path("userId") userId: String): Observable<ListResponse<Sheet>>

    @GET("v1/users/{userId}/collect")
    fun collectSheets(@Path("userId") userId: String): Observable<ListResponse<Sheet>>

    @POST("v1/sheets")
    fun createSheet(@Body data: Sheet): Observable<DetailResponse<Sheet>>

    @GET("v1/comments")
    fun comments(): Observable<ListResponse<Comment>>

    @GET("v1/ads")
    fun ads(@Query("position") position: Int): Observable<ListResponse<Ad>>

    @GET("v1/songs")
    fun songs(): Observable<ListResponse<Song>>

    @GET("v1/songs/{id}")
    fun songDetail(@Path("id") id: String): Observable<DetailResponse<Song>>

    @POST("v1/sessions")
    fun login(@Body data: User): Observable<DetailResponse<Session>>

    @GET("v1/users/{id}")
    fun userDetail(
        @Path("id") id: String,
        @QueryMap data: Map<String, String>,
    ): Observable<DetailResponse<User>>

    @GET("v1/users/{id}/following")
    fun friends(@Path("id") id: String): Observable<ListResponse<User>>

    @GET("v1/users/{id}/followers")
    fun fans(@Path("id") id: String): Observable<ListResponse<User>>

    @PATCH("v1/users/{id}")
    fun updateUser(
        @Path("id") id: String,
        @Body data: User,
    ): Observable<DetailResponse<Base>>

    @POST("v1/friends")
    fun follow(@Body data: Map<String, String>): Observable<DetailResponse<BaseId>>

    @DELETE("v1/friends/{userId}")
    fun deleteFollow(@Path("userId") userId: String): Observable<DetailResponse<BaseId>>

    @POST("v1/users")
    fun register(@Body data: User): Observable<DetailResponse<BaseId>>

    @POST("v1/collects")
    fun collect(@Body data: Map<String, String>): Observable<DetailResponse<Base>>

    @DELETE("v1/collects/{id}")
    fun deleteCollect(@Path("id") id: String): Observable<DetailResponse<Base>>

    @GET("v1/comments")
    fun comments(@QueryMap data: Map<String, String>): Observable<ListResponse<Comment>>

    @POST("v1/likes")
    fun like(@Body data: Map<String, String>): Observable<DetailResponse<BaseId>>

    @DELETE("v1/likes/{id}")
    fun cancelLike(
        @Path("id") id: String,
        @Query("style") style: Int,
    ): Observable<DetailResponse<Base>>

    @POST("v1/comments")
    fun createComment(@Body data: Comment): Observable<DetailResponse<Comment>>

    @GET("v1/feeds")
    fun feeds(@QueryMap data: Map<String, String>): Observable<ListResponse<Feed>>

    @POST("v1/feeds")
    fun createFeed(@Body data: Feed): Observable<DetailResponse<Base>>

    @Multipart
    @POST("v1/files")
    fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("flavor") flavor: RequestBody,
    ): Observable<DetailResponse<Resource>>

    @Multipart
    @POST("v1/files/multi")
    fun uploadFiles(
        @Part file: List<@JvmSuppressWildcards MultipartBody.Part>,
        @Part("flavor") flavor: RequestBody,
    ): Observable<ListResponse<Resource>>
}
