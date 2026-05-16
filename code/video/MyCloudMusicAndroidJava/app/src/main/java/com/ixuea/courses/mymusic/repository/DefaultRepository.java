package com.ixuea.courses.mymusic.repository;

import com.ixuea.courses.mymusic.component.ad.model.Ad;
import com.ixuea.courses.mymusic.component.api.DefaultService;
import com.ixuea.courses.mymusic.component.api.NetworkModule;
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
import com.ixuea.courses.mymusic.util.Constant;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Public slim repository for refactored music, discovery, feed, comments, user,
 * download, and chat paths.
 */
public class DefaultRepository {
    private static DefaultRepository instance;
    private final DefaultService service;

    public DefaultRepository() {
        service = NetworkModule.provideRetrofit(NetworkModule.provideOkHttpClient()).create(DefaultService.class);
    }

    public synchronized static DefaultRepository getInstance() {
        if (instance == null) {
            instance = new DefaultRepository();
        }
        return instance;
    }

    public Observable<ListResponse<Ad>> ads(int position) {
        return service.ads(position)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ListResponse<Ad>> bannerAd() {
        return ads(Constant.VALUE0);
    }

    public Observable<ListResponse<Ad>> splashAd() {
        return ads(Constant.VALUE10);
    }

    public Observable<ListResponse<Sheet>> sheets(String category) {
        return sheets(category, Constant.SIZE10);
    }

    public Observable<ListResponse<Sheet>> sheets(int size) {
        return sheets(null, size);
    }

    public Observable<ListResponse<Sheet>> sheets(String category, int size) {
        return service.sheets(category, size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DetailResponse<Sheet>> sheetDetail(String id) {
        return service.sheetDetail("testHeader", id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ListResponse<Sheet>> createSheets(String userId) {
        return service.createSheets(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ListResponse<Sheet>> collectSheets(String userId) {
        return service.collectSheets(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DetailResponse<Sheet>> createSheet(Sheet data) {
        return service.createSheet(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ListResponse<Song>> songs() {
        return service.songs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DetailResponse<Song>> songDetail(String id) {
        return service.songDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DetailResponse<Session>> login(User data) {
        return service.login(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DetailResponse<User>> userDetail(String id, String nickname) {
        HashMap<String, String> data = new HashMap<>();
        if (StringUtils.isNotBlank(nickname)) {
            data.put(Constant.NICKNAME, nickname);
        }

        return service.userDetail(id, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DetailResponse<User>> userDetail(String id) {
        return userDetail(id, null);
    }

    public Observable<ListResponse<User>> friends(String id) {
        return service.friends(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ListResponse<User>> fans(String id) {
        return service.fans(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DetailResponse<Base>> updateUser(String id, User data) {
        return service.updateUser(id, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DetailResponse<BaseId>> follow(String userId) {
        Map<String, String> data = new HashMap<>();
        data.put("id", userId);

        return service.follow(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DetailResponse<BaseId>> deleteFollow(String userId) {
        return service.deleteFollow(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DetailResponse<BaseId>> register(User data) {
        return service.register(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DetailResponse<Base>> collect(String id) {
        HashMap<String, String> data = new HashMap<>();
        data.put("sheet_id", id);

        return service.collect(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DetailResponse<Base>> deleteCollect(String id) {
        return service.deleteCollect(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ListResponse<Comment>> comments(Map<String, String> data) {
        return service.comments(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DetailResponse<BaseId>> commentLike(String data) {
        HashMap<String, String> param = new HashMap<>();
        param.put("comment_id", data);

        return service.like(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DetailResponse<Base>> cancelCommentLike(String data) {
        return service.cancelLike(data, Constant.VALUE0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DetailResponse<Comment>> createComment(Comment data) {
        return service.createComment(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ListResponse<Feed>> feeds(String userId) {
        Map<String, String> datum = new HashMap<>();
        if (StringUtils.isNotBlank(userId)) {
            datum.put(Constant.USER_ID, userId);
        }

        return service.feeds(datum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DetailResponse<Base>> createFeed(Feed data) {
        return service.createFeed(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DetailResponse<Resource>> uploadFile(MultipartBody.Part data, RequestBody flavor) {
        return service.uploadFile(data, flavor)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ListResponse<Resource>> uploadFiles(List<MultipartBody.Part> data, RequestBody flavor) {
        return service.uploadFiles(data, flavor)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
