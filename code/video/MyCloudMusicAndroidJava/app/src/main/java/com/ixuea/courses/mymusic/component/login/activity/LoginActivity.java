package com.ixuea.courses.mymusic.component.login.activity;

import android.text.TextUtils;
import android.view.View;

import com.ixuea.common.util.RegularUtil;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.input.activity.InputUserIdentityActivity;
import com.ixuea.courses.mymusic.component.register.activity.RegisterActivity;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.config.Config;
import com.ixuea.courses.mymusic.databinding.ActivityLoginBinding;
import com.ixuea.courses.mymusic.util.StringUtil;
import com.ixuea.superui.toast.SuperToast;

import org.apache.commons.lang3.StringUtils;

/**
 * 登录界面
 */
public class LoginActivity extends BaseLoginActivity<ActivityLoginBinding> {
    @Override
    protected void initDatum() {
        super.initDatum();
        if (Config.DEBUG) {
            binding.username.setText("13141111222");
            binding.password.setText("ixueaedu");
        }
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //登录按钮点击
        binding.primary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户名
                String username = binding.username.getText().toString().trim();
                if (StringUtils.isBlank(username)) {
                    SuperToast.show(R.string.enter_username);
                    return;
                }

                //如果用户名
                //不是手机号也不是邮箱
                //就是格式错误
                if (!(RegularUtil.isPhone(username) || RegularUtil.isEmail(username))) {
                    SuperToast.show(R.string.error_username_format);
                    return;
                }

                //获取密码
                String password = binding.password.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    SuperToast.show(R.string.enter_password);
                    return;
                }

                //判断密码格式
                if (!StringUtil.isPassword(password)) {
                    SuperToast.show(R.string.error_password_format);
                    return;
                }

                //判断是手机号还有邮箱
                String phone = null;
                String email = null;

                if (RegularUtil.isPhone(username)) {
                    //手机号
                    phone = username;
                } else {
                    //邮箱
                    email = username;
                }

                //调用父类的登录方法
                login(User.createLogin(phone, email, password));
            }
        });

        binding.register.setOnClickListener(v -> startActivity(RegisterActivity.class));
        binding.forgotPassword.setOnClickListener(v -> InputUserIdentityActivity.startWithForgotPassword(getHostActivity()));
    }

    @Override
    protected String pageId() {
        return "Login";
    }
}