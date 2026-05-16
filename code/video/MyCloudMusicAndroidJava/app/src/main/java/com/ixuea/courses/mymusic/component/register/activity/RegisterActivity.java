package com.ixuea.courses.mymusic.component.register.activity;

import static autodispose2.AutoDispose.autoDisposable;

import com.ixuea.common.util.RegularUtil;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.login.activity.BaseLoginActivity;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.databinding.ActivityRegisterBinding;
import com.ixuea.courses.mymusic.model.BaseId;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.StringUtil;
import com.ixuea.superui.toast.SuperToast;

import org.apache.commons.lang3.StringUtils;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 注册界面
 */
public class RegisterActivity extends BaseLoginActivity<ActivityRegisterBinding> {
    @Override
    protected void initListeners() {
        super.initListeners();
        binding.primary.setOnClickListener(v -> processNext());
    }

    private void processNext() {
        //获取昵称
        String nickname = binding.nickname.getText().toString().trim();
        if (StringUtils.isBlank(nickname)) {
            SuperToast.show(R.string.enter_nickname);
            return;
        }

        //判断昵称格式
        if (!StringUtil.isNickname(nickname)) {
            SuperToast.show(R.string.error_nickname_format);
            return;
        }

        //手机号
        String phone = binding.phone.getText().toString().trim();
        if (StringUtils.isBlank(phone)) {
            SuperToast.show(R.string.enter_phone);
            return;
        }

        //手机号格式
        if (!RegularUtil.isPhone(phone)) {
            SuperToast.show(R.string.error_phone_format);
            return;
        }

        //邮箱
        String email = binding.email.getText().toString().trim();
        if (StringUtils.isBlank(email)) {
            SuperToast.show(R.string.enter_email);
            return;
        }

        //邮箱格式
        if (!RegularUtil.isEmail(email)) {
            SuperToast.show(R.string.error_email_format);
            return;
        }

        //密码
        String password = binding.password.getText().toString().trim();
        if (StringUtils.isBlank(password)) {
            SuperToast.show(R.string.enter_password);
            return;
        }

        //密码格式
        if (!StringUtil.isPassword(password)) {
            SuperToast.show(R.string.error_password_format);
            return;
        }

        //确认密码
        String confirmPassword = binding.confirmPassword.getText().toString().trim();
        if (StringUtils.isBlank(confirmPassword)) {
            SuperToast.show(R.string.enter_confirm_password);
            return;
        }

        //确认密码格式
        if (!StringUtil.isPassword(confirmPassword)) {
            SuperToast.show(R.string.error_confirm_password_format);
            return;
        }

        //判断密码和确认密码是否一样
        if (!password.equals(confirmPassword)) {
            SuperToast.show(R.string.error_confirm_password);
            return;
        }

        User data = new User();
        data.setNickname(nickname);
        data.setPhone(phone);
        data.setEmail(email);
        data.setPassword(password);

        registerClick(data);
    }

    private void registerClick(User data) {
        //调用注册接口
        DefaultRepository.getInstance().register(data)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<BaseId>>() {
                    @Override
                    public void onSucceeded(DetailResponse<BaseId> d) {
                        login(User.createLogin(data.getPhone(), data.getEmail(), data.getPassword()));
                    }
                });
    }

    @Override
    protected String pageId() {
        return "Register";
    }
}