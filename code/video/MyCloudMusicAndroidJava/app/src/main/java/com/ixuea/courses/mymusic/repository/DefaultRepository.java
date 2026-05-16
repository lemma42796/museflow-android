package com.ixuea.courses.mymusic.repository;

import com.ixuea.courses.mymusic.component.ad.model.Ad;
import com.ixuea.courses.mymusic.component.address.model.Address;
import com.ixuea.courses.mymusic.component.address.model.request.DataRequest;
import com.ixuea.courses.mymusic.component.api.DefaultService;
import com.ixuea.courses.mymusic.component.api.NetworkModule;
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
import com.ixuea.courses.mymusic.util.Constant;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 本项目默认仓库
 * 主要是从网络，数据库获取数据
 * 目前项目中大部分操作都在这里
 * <p>
 * 如果项目每个模块之间有明显的区别，例如：有商城，有歌单，那可以放到对应模块的Repository
 */
public class DefaultRepository {
    private static DefaultRepository instance;
    private final DefaultService service;

    public DefaultRepository() {
        //虽然当前类是单例设计模式，但因为直接调用provideRetrofit这样的方法
        //所以虽然代码是和MVVM架构模块那边（商城）复用了，但他们不是一个单例对象
        service = NetworkModule.provideRetrofit(NetworkModule.provideOkHttpClient()).create(DefaultService.class);
    }

    /**
     * 返回当前对象的唯一实例
     * <p>
     * 单例设计模式
     * 由于移动端很少有高并发
     * 所以这个就是简单判断
     *
     * @return
     */
    public synchronized static DefaultRepository getInstance() {
        if (instance == null) {
            instance = new DefaultRepository();
        }
        return instance;
    }

    /**
     * 广告列表
     *
     * @return
     */
    public Observable<ListResponse<Ad>> ads(int position) {
        return service.ads(position)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 首页banner界面广告
     *
     * @return
     */
    public Observable<ListResponse<Ad>> bannerAd() {
        return ads(Constant.VALUE0);
    }

    /**
     * 启动界面广告
     *
     * @return
     */
    public Observable<ListResponse<Ad>> splashAd() {
        return ads(Constant.VALUE10);
    }

    /**
     * 歌单列表
     *
     * @return
     */
    public Observable<ListResponse<Sheet>> sheets(String category) {
        return sheets(category, Constant.SIZE10);
    }

    /**
     * 歌单列表
     *
     * @return
     */
    public Observable<ListResponse<Sheet>> sheets(int size) {
        return sheets(null, size);
    }

    /**
     * 歌单列表
     *
     * @return
     */
    public Observable<ListResponse<Sheet>> sheets(String category, int size) {
        return service.sheets(category, size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 歌单详情
     *
     * @param id
     * @return
     */
    public Observable<DetailResponse<Sheet>> sheetDetail(String id) {
        return service.sheetDetail("testHeader", id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取用户创建的歌单
     *
     * @param userId
     * @return
     */
    public Observable<ListResponse<Sheet>> createSheets(String userId) {
        return service.createSheets(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取用户收藏的歌单
     *
     * @param userId
     * @return
     */
    public Observable<ListResponse<Sheet>> collectSheets(String userId) {
        return service.collectSheets(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 创建歌单
     *
     * @param data
     * @return
     */
    public Observable<DetailResponse<Sheet>> createSheet(Sheet data) {
        return service.createSheet(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 单曲
     *
     * @return
     */
    public Observable<ListResponse<Song>> songs() {
        return service.songs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 单曲详情
     *
     * @return
     */
    public Observable<DetailResponse<Song>> songDetail(String id) {
        return service.songDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 登录
     *
     * @param data
     * @return
     */
    public Observable<DetailResponse<Session>> login(User data) {
        return service.login(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 用户详情
     *
     * @param id
     * @return
     */
    public Observable<DetailResponse<User>> userDetail(String id, String nickname) {

        //添加查询参数
        HashMap<String, String> data = new HashMap<>();

        if (StringUtils.isNotBlank(nickname)) {
            //如果昵称不为空才添加
            data.put(Constant.NICKNAME, nickname);
        }

        return service.userDetail(id, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 用户详情
     *
     * @param id
     * @return
     */
    public Observable<DetailResponse<User>> userDetail(String id) {
        return userDetail(id, null);
    }

    /**
     * 好友列表
     *
     * @param id
     * @return
     */
    public Observable<ListResponse<User>> friends(String id) {
        return service.friends(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 粉丝列表
     *
     * @param id
     * @return
     */
    public Observable<ListResponse<User>> fans(String id) {
        return service.fans(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 更新用户
     *
     * @param data
     * @return
     */
    public Observable<DetailResponse<Base>> updateUser(String id, User data) {
        return service.updateUser(id, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 关注用户
     *
     * @param userId
     * @return
     */
    public Observable<DetailResponse<BaseId>> follow(String userId) {

        Map<String, String> data = new HashMap<>();
        data.put("id", userId);

        return service.follow(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 取消关注用户
     *
     * @param userId
     * @return
     */
    public Observable<DetailResponse<BaseId>> deleteFollow(String userId) {
        return service.deleteFollow(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 注册
     *
     * @param data
     * @return
     */
    public Observable<DetailResponse<BaseId>> register(User data) {
        return service.register(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 发送验证码
     *
     * @param style 0:邮件验证码，10：短信验证码
     * @param data
     * @return
     */
    public Observable<DetailResponse<Base>> sendCode(int style, CodeRequest data) {
        return service.sendCode(style, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 校验验证码
     *
     * @param data
     * @return
     */
    public Observable<DetailResponse<Base>> checkCode(CodeRequest data) {
        return service.checkCode(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 重置密码
     *
     * @param data
     * @return
     */
    public Observable<DetailResponse<BaseId>> resetPassword(User data) {
        return service.resetPassword(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 收藏歌单
     *
     * @param id
     * @return
     */
    public Observable<DetailResponse<Base>> collect(String id) {

        HashMap<String, String> data = new HashMap<>();
        data.put("sheet_id", id);

        return service.collect(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 取消收藏歌单
     *
     * @param id
     * @return
     */
    public Observable<DetailResponse<Base>> deleteCollect(String id) {
        return service.deleteCollect(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 评论列表
     *
     * @param data
     * @return
     */
    public Observable<ListResponse<Comment>> comments(Map<String, String> data) {
        return service.comments(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 评论点赞
     *
     * @param data 评论id
     * @return
     */
    public Observable<DetailResponse<BaseId>> commentLike(String data) {
        HashMap<String, String> param = new HashMap<>();
        param.put("comment_id", data);

        return service.like(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 取消评论点赞
     *
     * @param data 评论id
     * @return
     */
    public Observable<DetailResponse<Base>> cancelCommentLike(String data) {
        return service.cancelLike(data, Constant.VALUE0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 创建评论
     *
     * @param data
     * @return
     */
    public Observable<DetailResponse<Comment>> createComment(Comment data) {
        return service.createComment(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 视频列表
     *
     * @return
     */
    public Observable<ListResponse<Video>> videos(int page) {
        return service.videos(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * 视频详情
     *
     * @param id
     * @return
     */
    public Observable<DetailResponse<Video>> videoDetail(String id) {
        return service.videoDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 动态列表
     * 传UserId数据就是该用户的
     * 不传就是全部
     *
     * @param userId
     * @return
     */
    public Observable<ListResponse<Feed>> feeds(String userId) {
        //创建查询参数
        Map<String, String> datum = new HashMap<>();

        if (StringUtils.isNotBlank(userId)) {
            //添加用户id
            datum.put(Constant.USER_ID, userId);
        }

        return service.feeds(datum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 发布动态
     *
     * @param data
     * @return
     */
    public Observable<DetailResponse<Base>> createFeed(Feed data) {
        return service.createFeed(data)
                .subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 搜索歌单
     *
     * @param data
     * @return
     */
    public Observable<ListResponse<Sheet>> searchSheets(String data) {
        return service.searchSheets(getSearchParams(data))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 搜索用户
     *
     * @param data
     * @return
     */
    public Observable<ListResponse<User>> searchUsers(String data) {
        return service.searchUsers(getSearchParams(data))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取查询参数
     *
     * @param data
     * @return
     */
    private Map<String, String> getSearchParams(String data) {
        HashMap<String, String> datum = new HashMap<>();

        //添加查询参数
        datum.put(Constant.QUERY, data);
        return datum;
    }

    /**
     * 搜索建议
     *
     * @param data
     * @return
     */
    public Observable<DetailResponse<Suggest>> searchSuggest(String data) {
        return service.searchSuggest(getSearchParams(data))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 地址列表
     *
     * @return
     */
    public Observable<ListResponse<Address>> addresses() {
        return service.addresses()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 创建地址
     *
     * @param data
     * @return
     */
    public Observable<DetailResponse<BaseId>> createAddress(Address data) {
        return service.createAddress(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 地址详情
     *
     * @param id
     * @return
     */
    public Observable<DetailResponse<Address>> addressDetail(String id) {
        return service.addressDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 更新地址
     *
     * @param data
     * @return
     */
    public Observable<DetailResponse<BaseId>> updateAddress(String id, Address data) {
        return service.updateAddress(id, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除地址
     *
     * @param id
     * @return
     */
    public Observable<DetailResponse<Base>> deleteAddress(String id) {
        return service.deleteAddress(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 地址识别
     *
     * @param data
     * @return
     */
    public Observable<DetailResponse<Address>> recognitionAddress(DataRequest data) {
        return service.recognitionAddress(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 确认订单
     *
     * @param data
     * @return
     */
    public Observable<DetailResponse<ConfirmOrderResponse>> confirmOrder(OrderRequest data) {
        return service.confirmOrder(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 创建订单
     *
     * @param data
     * @return
     */
    public Observable<DetailResponse<BaseId>> createOrder(OrderRequest data) {
        return service.createOrder(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    public Observable<DetailResponse<Order>> orderDetail(String id) {
        return service.orderDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取订单支付参数
     *
     * @param id
     * @param data
     * @return
     */
    public Observable<DetailResponse<PayResponse>> orderPay(String id, PayRequest data) {
        return service.orderPay(id, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 订单列表
     *
     * @return
     */
    public Observable<ListResponse<Order>> orders(int status) {
        return service.orders(status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 取消订单
     *
     * @param data 评论id
     * @return
     */
    public Observable<DetailResponse<Base>> cancelOrder(String data) {
        return service.cancelOrder(data, new Base())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 上传文件
     *
     * @param data
     * @return
     */
    public Observable<DetailResponse<Resource>> uploadFile(MultipartBody.Part data, RequestBody flavor) {
        return service.uploadFile(data, flavor)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 上传多个文件
     *
     * @param data
     * @return
     */
    public Observable<ListResponse<Resource>> uploadFiles(List<MultipartBody.Part> data, RequestBody flavor) {
        return service.uploadFiles(data, flavor)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
