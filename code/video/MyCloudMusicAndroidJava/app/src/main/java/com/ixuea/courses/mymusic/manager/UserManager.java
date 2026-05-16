package com.ixuea.courses.mymusic.manager;

import android.content.Context;

import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理器
 */
public class UserManager {
    private static UserManager instance;
    private final Context context;
    /**
     * 用户缓存
     */
    private Map<String, User> userCaches = new HashMap<>();

    public UserManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public synchronized static UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context);
        }
        return instance;
    }

    /**
     * 获取用户
     *
     * @param userId       用户id
     * @param userListener
     */
    public void getUser(String userId, UserListener userListener) {
        //先从缓存中获取
        User data = userCaches.get(userId);
        if (data != null) {
            userListener.onGetUserSuccess(data);
            return;
        }

        //请求网络获取
        DefaultRepository.getInstance()
                .userDetail(userId)
                .subscribe(new HttpObserver<DetailResponse<User>>() {
                    @Override
                    public void onSucceeded(DetailResponse<User> data) {
                        //回调请求成功
                        userListener.onGetUserSuccess(data.getData());

                        //添加到缓存
                        userCaches.put(userId, data.getData());
                    }
                });
    }

    public interface UserListener {
        /**
         * 用户获取成功
         *
         * @param data
         */
        void onGetUserSuccess(User data);
    }
}
