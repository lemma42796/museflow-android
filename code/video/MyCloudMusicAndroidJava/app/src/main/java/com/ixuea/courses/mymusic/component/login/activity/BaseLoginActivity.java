package com.ixuea.courses.mymusic.component.login.activity;

import static autodispose2.AutoDispose.autoDisposable;

import android.os.Build;

import androidx.viewbinding.ViewBinding;

import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.login.model.Session;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.manager.MyActivityManager;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.AnalysisUtil;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 登录通用界面
 */
public class BaseLoginActivity<VB extends ViewBinding> extends BaseTitleActivity<VB> {
    /**
     * 登录
     */
    protected void login(User data) {
        //设备名称
        data.setDevice(Build.MODEL);

        //调用登录接口
        DefaultRepository.getInstance().login(data)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<Session>>() {
                    /**
                     * 登录成功
                     *
                     * @param r
                     */
                    @Override
                    public void onSucceeded(DetailResponse<Session> r) {
                        onLogin(r.getData());

                        //统计登录事件
                        AnalysisUtil.onLogin(AppContext.getInstance(), true, data);
                    }

                    @Override
                    public boolean onFailed(DetailResponse<Session> r, Throwable e) {
                        //统计登录事件
                        AnalysisUtil.onLogin(AppContext.getInstance(), false, data);

                        return false;
                    }
                });
    }

    /**
     * 手机号验证码登录
     *
     * @param phone
     * @param code
     */
    protected void login(String phone, String code) {
        User user = new User();
        user.setPhone(phone);
        user.setCode(code);
        login(user);
    }

    private void onLogin(Session data) {
        //保存登录信息
        sp.setSession(data.getSession());
        sp.setUserId(data.getUserId());
        sp.setChatToken(data.getChatToken());

        AppContext.getInstance().onLogin(data);

        MyActivityManager.getInstance().finishAllLogin();
    }
}
