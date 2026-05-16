package com.ixuea.courses.mymusic.component.login.activity;

import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.input.activity.InputUserIdentityActivity;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.component.web.activity.WebActivity;
import com.ixuea.courses.mymusic.databinding.ActivityLoginHomeBinding;
import com.ixuea.courses.mymusic.util.SuperTextUtil;
import com.ixuea.superui.toast.SuperToast;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import timber.log.Timber;

/**
 * 登录主界面
 */
public class LoginHomeActivity extends BaseLoginActivity<ActivityLoginHomeBinding> {


    private User data;

    @Override
    protected void initViews() {
        super.initViews();
        SuperTextUtil.setLinkColor(binding.userAgreement, getColor(R.color.link));

        Spanned content = Html.fromHtml(getString(R.string.user_agreement));
        SpannableStringBuilder result = SuperTextUtil.setHtmlLinkClick(content, data -> WebActivity.start(getHostActivity(), "", data));

        binding.userAgreement.setText(result);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
//        EventBus.getDefault().register(this);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //手机号登录点击
        binding.phoneLogin.setOnClickListener(v -> InputUserIdentityActivity.startWithPhoneLogin(getHostActivity()));

        binding.usernameLogin.setOnClickListener(v -> startActivity(LoginActivity.class));
        binding.wechat.setOnClickListener(v -> otherLogin(Wechat.NAME));
        binding.qq.setOnClickListener(v -> otherLogin(QQ.NAME));
    }

    /**
     * 通用第三方登录
     *
     * @param name
     */
    public void otherLogin(String name) {
        //初始化具体的平台
        Platform platform = ShareSDK.getPlatform(name);

        //设置false表示使用SSO授权方式
        platform.SSOSetting(false);

        //回调信息
        //可以在这里获取基本的授权返回的信息
        platform.setPlatformActionListener(new PlatformActionListener() {

            /**
             * 登录成功了
             * @param platform
             * @param i
             * @param hashMap
             */
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                //登录成功了

                //就可以获取到昵称，头像，OpenId
                //该方法回调不是在主线程

                //从数据库获取信息
                //也可以通过user参数获取
                PlatformDb db = platform.getDb();

                data = new User();

                data.setNickname(db.getUserName());
                data.setIcon(db.getUserIcon());

                //判断登录类型
                if (Wechat.NAME.equals(name)) {
                    data.setWechatId(db.getUserId());
                } else if (QQ.NAME.equals(name)) {
                    data.setQqId(db.getUserId());
                }

                //继续登录
                binding.phoneLogin.post(() -> {
                    //切换到主线程
                    login(data);
                });
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Timber.e("third part login fail %s %d", platform.getName(), i);
                binding.phoneLogin.post(() -> {
                    SuperToast.error(R.string.login_error);
                });
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Timber.d("third part login cancel %s %d", platform.getName(), i);
            }
        });

        //authorize与showUser单独调用一个即可
        //授权并获取用户信息
        platform.showUser(null);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void loginStatusChangedEvent(LoginStatusChangedEvent event) {
//        finish();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }


    @Override
    protected String pageId() {
        return "LoginHome";
    }
}