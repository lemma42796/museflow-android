package com.ixuea.courses.mymusic.component.api;

import com.ixuea.courses.mymusic.component.ad.model.Ad;
import com.ixuea.courses.mymusic.component.address.model.Address;
import com.ixuea.courses.mymusic.component.address.model.request.DataRequest;
import com.ixuea.courses.mymusic.component.comment.model.Comment;
import com.ixuea.courses.mymusic.component.feed.model.Feed;
import com.ixuea.courses.mymusic.component.input.model.CodeRequest;
import com.ixuea.courses.mymusic.component.login.model.Session;
import com.ixuea.courses.mymusic.component.order.model.Order;
import com.ixuea.courses.mymusic.component.order.model.request.OrderRequest;
import com.ixuea.courses.mymusic.component.order.model.response.ConfirmOrderResponse;
import com.ixuea.courses.mymusic.component.pay.model.request.PayRequest;
import com.ixuea.courses.mymusic.component.pay.model.response.PayResponse;
import com.ixuea.courses.mymusic.component.search.model.Suggest;
import com.ixuea.courses.mymusic.component.sheet.model.Sheet;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.component.video.model.Video;
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
 * 默认远程数据源
 */
public interface DefaultService {
    /**
     * 歌单列表
     *
     * @return
     */
    @GET("v1/sheets")
    Observable<ListResponse<Sheet>> sheets(@Query(value = "category") String category, @Query(value = "size") int size);

    /**
     * 歌单详情
     *
     * @param testHeader 可以通过@Header这种方式针对单个请求传递请求头，这里就是测试，无实际作用
     * @param id
     * @return
     */
    @GET("v1/sheets/{id}")
    Observable<DetailResponse<Sheet>> sheetDetail(@Header("testHeader") String testHeader, @Path("id") String id);

    /**
     * 获取用户创建的歌单
     *
     * @param userId
     * @return
     */
    @GET("v1/users/{userId}/create")
    Observable<ListResponse<Sheet>> createSheets(@Path("userId") String userId);

    /**
     * 获取用户收藏的歌单
     *
     * @param userId
     * @return
     */
    @GET("v1/users/{userId}/collect")
    Observable<ListResponse<Sheet>> collectSheets(@Path("userId") String userId);

    /**
     * 创建歌单
     *
     * @param data
     * @return
     */
    @POST("v1/sheets")
    Observable<DetailResponse<Sheet>> createSheet(@Body Sheet data);

    /**
     * 评论列表
     *
     * @return
     */
    @GET("v1/comments")
    Observable<ListResponse<Comment>> comments();

    /**
     * 广告列表
     *
     * @return
     */
    @GET("v1/ads")
    Observable<ListResponse<Ad>> ads(@Query(value = "position") int position);

    /**
     * 单曲
     *
     * @return
     */
    @GET("v1/songs")
    Observable<ListResponse<Song>> songs();

    /**
     * 单曲详情
     *
     * @param id
     * @return
     */
    @GET("v1/songs/{id}")
    Observable<DetailResponse<Song>> songDetail(@Path("id") String id);

    /**
     * 登录
     *
     * @param data
     * @return
     */
    @POST("v1/sessions")
    Observable<DetailResponse<Session>> login(@Body User data);

    /**
     * 用户详情
     *
     * @param id
     * @param data
     * @return
     */
    @GET("v1/users/{id}")
    Observable<DetailResponse<User>> userDetail(@Path("id") String id, @QueryMap Map<String, String> data);

    /**
     * 好友列表（我关注的人）
     *
     * @param id
     * @return
     */
    @GET("v1/users/{id}/following")
    Observable<ListResponse<User>> friends(@Path("id") String id);

    /**
     * 粉丝列表（关注我的人）
     *
     * @param id
     * @return
     */
    @GET("v1/users/{id}/followers")
    Observable<ListResponse<User>> fans(@Path("id") String id);

    /**
     * 更新用户
     * <p>
     * 如果被更新用户没有手机号，邮箱，密码，第一次可以更新，后续不能更新
     * 当然这样实现有一些安全问题，真实项目中推荐新增/更新，手机号，邮箱用验证验证
     * 类似手机号验证码登录那样实现，这里就不在实现了
     *
     * @param id
     * @param data
     * @return
     */
    @PATCH("v1/users/{id}")
    Observable<DetailResponse<Base>> updateUser(@Path("id") String id, @Body User data);

    /**
     * 关注用户
     *
     * @param data
     * @return
     */
    @POST("v1/friends")
    Observable<DetailResponse<BaseId>> follow(@Body Map<String, String> data);

    /**
     * 取消关注用户
     *
     * @param userId
     * @return
     */
    @DELETE("v1/friends/{userId}")
    Observable<DetailResponse<BaseId>> deleteFollow(@Path("userId") String userId);

    /**
     * 注册
     *
     * @param data
     * @return
     */
    @POST("v1/users")
    Observable<DetailResponse<BaseId>> register(@Body User data);

    /**
     * 发送验证码
     *
     * @param data
     * @return
     */
    @POST("v1/codes")
    Observable<DetailResponse<Base>> sendCode(@Query(value = "style") int style, @Body CodeRequest data);

    /**
     * 校验验证码
     *
     * @param data
     * @return
     */
    @POST("v1/codes/check")
    Observable<DetailResponse<Base>> checkCode(@Body CodeRequest data);

    /**
     * 重置密码
     *
     * @param data
     * @return
     */
    @POST("v1/users/reset_password")
    Observable<DetailResponse<BaseId>> resetPassword(@Body User data);

    /**
     * 收藏歌单
     *
     * @param data
     * @return
     */
    @POST("v1/collects")
    Observable<DetailResponse<Base>> collect(@Body Map<String, String> data);

    /**
     * 取消收藏歌单
     *
     * @param id
     * @return
     */
    @DELETE("v1/collects/{id}")
    Observable<DetailResponse<Base>> deleteCollect(@Path("id") String id);

    /**
     * 评论列表
     *
     * @param data
     * @return
     */
    @GET("v1/comments")
    Observable<ListResponse<Comment>> comments(@QueryMap Map<String, String> data);

    /**
     * 点赞
     *
     * @param data
     * @return
     */
    @POST("v1/likes")
    Observable<DetailResponse<BaseId>> like(@Body Map<String, String> data);

    /**
     * 取消点赞
     *
     * @param id    评论id
     * @param style 类型：0：评论；10：动态
     * @return
     */
    @DELETE("v1/likes/{id}")
    Observable<DetailResponse<Base>> cancelLike(@Path("id") String id, @Query(value = "style") int style);

    /**
     * 创建评论
     *
     * @param data
     * @return
     */
    @POST("v1/comments")
    Observable<DetailResponse<Comment>> createComment(@Body Comment data);

    /**
     * 视频列表
     *
     * @return
     */
    @GET("v1/videos")
    Observable<ListResponse<Video>> videos(@Query(value = "page") int page);

    /**
     * 视频详情
     *
     * @param id
     * @return
     */
    @GET("v1/videos/{id}")
    Observable<DetailResponse<Video>> videoDetail(@Path("id") String id);

    /**
     * 动态列表
     *
     * @param data
     * @return
     */
    @GET("v1/feeds")
    Observable<ListResponse<Feed>> feeds(@QueryMap Map<String, String> data);

    /**
     * 发布动态
     *
     * @param data
     * @return
     */
    @POST("v1/feeds")
    Observable<DetailResponse<Base>> createFeed(@Body Feed data);

    /**
     * 搜索歌单
     *
     * @param data
     * @return
     */
    @GET("v1/searches/sheets")
    Observable<ListResponse<Sheet>> searchSheets(@QueryMap Map<String, String> data);

    /**
     * 搜索用户
     *
     * @param data
     * @return
     */
    @GET("v1/searches/users")
    Observable<ListResponse<User>> searchUsers(@QueryMap Map<String, String> data);

    /**
     * 搜索建议
     *
     * @param data
     * @return
     */
    @GET("v1/searches/suggests")
    Observable<DetailResponse<Suggest>> searchSuggest(@QueryMap Map<String, String> data);

    /**
     * 地址列表
     *
     * @return
     */
    @GET("v3/addresses")
    Observable<ListResponse<Address>> addresses();

    /**
     * 地址详情
     *
     * @param id
     * @return
     */
    @GET("v1/addresses/{id}")
    Observable<DetailResponse<Address>> addressDetail(@Path("id") String id);

    /**
     * 创建地址
     *
     * @param data
     * @return
     */
    @POST("v3/addresses")
    Observable<DetailResponse<BaseId>> createAddress(@Body Address data);

    /**
     * 更新地址
     *
     * @param id
     * @param data
     * @return
     */
    @PATCH("v1/addresses/{id}")
    Observable<DetailResponse<BaseId>> updateAddress(@Path("id") String id, @Body Address data);

    /**
     * 删除地址
     *
     * @param id
     * @return
     */
    @DELETE("v1/addresses/{id}")
    Observable<DetailResponse<Base>> deleteAddress(@Path("id") String id);

    /**
     * 收货地址文本解析
     * <p>
     * 例如：输入的信息为：四川省成都市天府新区牧华路远大中央公园9栋1单元801 李薇 131411112223
     * 返回为：
     * {
     * "name": "李薇",
     * "phone": "13141111222",
     * "province": "四川省",
     * "province_code": "510000",
     * "city": "成都市",
     * "city_code": "510100",
     * "area": "双流区",
     * "area_code": "510116",
     * "detail": "牧华路远大中央公园9栋1单元801",
     * "default_address": 0
     * }
     * <p>
     * 类似顺丰公众号（大部分快递都有类似的功能），收货地址解析功能
     * <p>
     * 提示：该接口并不是简单的字符串拆分，还会自动纠正省市区信息
     *
     * @return
     */
    @POST("v1/nlp/address")
    Observable<DetailResponse<Address>> recognitionAddress(@Body DataRequest data);


    /**
     * 订单确认
     * <p>
     * 这一步，不会创建订单，而是根据用户传递的商品id，收货地址，优惠券id，计算价格
     * 目的是让用户确认
     *
     * @return
     */
    @POST("v1/orders/confirm")
    Observable<DetailResponse<ConfirmOrderResponse>> confirmOrder(@Body OrderRequest data);

    /**
     * 创建订单
     *
     * @param data
     * @return
     */
    @POST("v1/orders")
    Observable<DetailResponse<BaseId>> createOrder(@Body OrderRequest data);

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @GET("v1/orders/{id}")
    Observable<DetailResponse<Order>> orderDetail(@Path("id") String id);

    /**
     * 获取订单支付参数
     *
     * @return
     */
    @POST("v1/orders/{id}/pay")
    Observable<DetailResponse<PayResponse>> orderPay(@Path("id") String id, @Body PayRequest data);

    /**
     * 订单列表
     *
     * @return
     */
    @GET("v1/orders")
    Observable<ListResponse<Order>> orders(@Query("status") int status);

    /**
     * 取消订单
     *
     * @param id
     * @param base
     * @return
     */
    @PATCH("v1/orders/{id}/cancel")
    Observable<DetailResponse<Base>> cancelOrder(@Path("id") String id, @Body Base base);

    /**
     * 上传文件
     * @param file 文件
     * @param flavor 渠道，例如：客户端会传递prod，dev，local等值，服务端方便保存到不同地方，这样后面好清理测试资源
     * @return
     */
    @Multipart
    @POST("v1/files")
    Observable<DetailResponse<Resource>> uploadFile(@Part MultipartBody.Part file, @Part("flavor") RequestBody flavor);

    /**
     * 上传多个文件
     * @param file 文件
     * @param flavor 渠道，例如：客户端会传递prod，dev，local等值，服务端方便保存到不同地方，这样后面好清理测试资源
     * @return
     */
    @Multipart
    @POST("v1/files/multi")
    Observable<ListResponse<Resource>> uploadFiles(@Part List<MultipartBody.Part> file, @Part("flavor") RequestBody flavor);

}
