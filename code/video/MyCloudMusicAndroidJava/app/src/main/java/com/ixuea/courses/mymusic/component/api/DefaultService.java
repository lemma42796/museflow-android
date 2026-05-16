package com.ixuea.courses.mymusic.component.api;

import com.ixuea.courses.mymusic.component.ad.model.Ad;
import com.ixuea.courses.mymusic.component.comment.model.Comment;
import com.ixuea.courses.mymusic.component.feed.model.Feed;
import com.ixuea.courses.mymusic.component.login.model.Session;
import com.ixuea.courses.mymusic.component.sheet.model.Sheet;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.model.Base;
import com.ixuea.courses.mymusic.model.BaseId;
import com.ixuea.courses.mymusic.model.Resource;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.model.response.ListResponse;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Public slim remote API for the refactored paths.
 */
public interface DefaultService {
    @GET("v1/sheets")
    Observable<ListResponse<Sheet>> sheets(@Query(value = "category") String category, @Query(value = "size") int size);

    @GET("v1/sheets/{id}")
    Observable<DetailResponse<Sheet>> sheetDetail(@Header("testHeader") String testHeader, @Path("id") String id);

    @GET("v1/users/{userId}/create")
    Observable<ListResponse<Sheet>> createSheets(@Path("userId") String userId);

    @GET("v1/users/{userId}/collect")
    Observable<ListResponse<Sheet>> collectSheets(@Path("userId") String userId);

    @POST("v1/sheets")
    Observable<DetailResponse<Sheet>> createSheet(@Body Sheet data);

    @GET("v1/comments")
    Observable<ListResponse<Comment>> comments();

    @GET("v1/ads")
    Observable<ListResponse<Ad>> ads(@Query(value = "position") int position);

    @GET("v1/songs")
    Observable<ListResponse<Song>> songs();

    @GET("v1/songs/{id}")
    Observable<DetailResponse<Song>> songDetail(@Path("id") String id);

    @POST("v1/sessions")
    Observable<DetailResponse<Session>> login(@Body User data);

    @GET("v1/users/{id}")
    Observable<DetailResponse<User>> userDetail(@Path("id") String id, @QueryMap Map<String, String> data);

    @GET("v1/users/{id}/following")
    Observable<ListResponse<User>> friends(@Path("id") String id);

    @GET("v1/users/{id}/followers")
    Observable<ListResponse<User>> fans(@Path("id") String id);

    @PATCH("v1/users/{id}")
    Observable<DetailResponse<Base>> updateUser(@Path("id") String id, @Body User data);

    @POST("v1/friends")
    Observable<DetailResponse<BaseId>> follow(@Body Map<String, String> data);

    @DELETE("v1/friends/{userId}")
    Observable<DetailResponse<BaseId>> deleteFollow(@Path("userId") String userId);

    @POST("v1/users")
    Observable<DetailResponse<BaseId>> register(@Body User data);

    @POST("v1/collects")
    Observable<DetailResponse<Base>> collect(@Body Map<String, String> data);

    @DELETE("v1/collects/{id}")
    Observable<DetailResponse<Base>> deleteCollect(@Path("id") String id);

    @GET("v1/comments")
    Observable<ListResponse<Comment>> comments(@QueryMap Map<String, String> data);

    @POST("v1/likes")
    Observable<DetailResponse<BaseId>> like(@Body Map<String, String> data);

    @DELETE("v1/likes/{id}")
    Observable<DetailResponse<Base>> cancelLike(@Path("id") String id, @Query(value = "style") int style);

    @POST("v1/comments")
    Observable<DetailResponse<Comment>> createComment(@Body Comment data);

    @GET("v1/feeds")
    Observable<ListResponse<Feed>> feeds(@QueryMap Map<String, String> data);

    @POST("v1/feeds")
    Observable<DetailResponse<Base>> createFeed(@Body Feed data);

    @Multipart
    @POST("v1/files")
    Observable<DetailResponse<Resource>> uploadFile(@Part MultipartBody.Part file, @Part("flavor") RequestBody flavor);

    @Multipart
    @POST("v1/files/multi")
    Observable<ListResponse<Resource>> uploadFiles(@Part List<MultipartBody.Part> file, @Part("flavor") RequestBody flavor);
}
