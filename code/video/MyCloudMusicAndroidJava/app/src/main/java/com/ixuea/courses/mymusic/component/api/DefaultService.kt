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
    suspend fun sheets(
        @Query("category") category: String?,
        @Query("size") size: Int,
    ): ListResponse<Sheet>

    @GET("v1/sheets/{id}")
    suspend fun sheetDetail(
        @Header("testHeader") testHeader: String,
        @Path("id") id: String,
    ): DetailResponse<Sheet>

    @GET("v1/users/{userId}/create")
    suspend fun createSheets(@Path("userId") userId: String): ListResponse<Sheet>

    @GET("v1/users/{userId}/collect")
    suspend fun collectSheets(@Path("userId") userId: String): ListResponse<Sheet>

    @POST("v1/sheets")
    suspend fun createSheet(@Body data: Sheet): DetailResponse<Sheet>

    @GET("v1/comments")
    suspend fun comments(): ListResponse<Comment>

    @GET("v1/ads")
    suspend fun ads(@Query("position") position: Int): ListResponse<Ad>

    @GET("v1/songs")
    suspend fun songs(): ListResponse<Song>

    @GET("v1/songs/{id}")
    suspend fun songDetail(@Path("id") id: String): DetailResponse<Song>

    @POST("v1/sessions")
    suspend fun login(@Body data: User): DetailResponse<Session>

    @GET("v1/users/{id}")
    suspend fun userDetail(
        @Path("id") id: String,
        @QueryMap data: Map<String, String>,
    ): DetailResponse<User>

    @GET("v1/users/{id}/following")
    suspend fun friends(@Path("id") id: String): ListResponse<User>

    @GET("v1/users/{id}/followers")
    suspend fun fans(@Path("id") id: String): ListResponse<User>

    @PATCH("v1/users/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body data: User,
    ): DetailResponse<Base>

    @POST("v1/friends")
    suspend fun follow(@Body data: Map<String, String>): DetailResponse<BaseId>

    @DELETE("v1/friends/{userId}")
    suspend fun deleteFollow(@Path("userId") userId: String): DetailResponse<BaseId>

    @POST("v1/users")
    suspend fun register(@Body data: User): DetailResponse<BaseId>

    @POST("v1/collects")
    suspend fun collect(@Body data: Map<String, String>): DetailResponse<Base>

    @DELETE("v1/collects/{id}")
    suspend fun deleteCollect(@Path("id") id: String): DetailResponse<Base>

    @GET("v1/comments")
    suspend fun comments(@QueryMap data: Map<String, String>): ListResponse<Comment>

    @POST("v1/likes")
    suspend fun like(@Body data: Map<String, String>): DetailResponse<BaseId>

    @DELETE("v1/likes/{id}")
    suspend fun cancelLike(
        @Path("id") id: String,
        @Query("style") style: Int,
    ): DetailResponse<Base>

    @POST("v1/comments")
    suspend fun createComment(@Body data: Comment): DetailResponse<Comment>

    @GET("v1/feeds")
    suspend fun feeds(@QueryMap data: Map<String, String>): ListResponse<Feed>

    @POST("v1/feeds")
    suspend fun createFeed(@Body data: Feed): DetailResponse<Base>

    @Multipart
    @POST("v1/files")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("flavor") flavor: RequestBody,
    ): DetailResponse<Resource>

    @Multipart
    @POST("v1/files/multi")
    suspend fun uploadFiles(
        @Part file: List<@JvmSuppressWildcards MultipartBody.Part>,
        @Part("flavor") flavor: RequestBody,
    ): ListResponse<Resource>
}
