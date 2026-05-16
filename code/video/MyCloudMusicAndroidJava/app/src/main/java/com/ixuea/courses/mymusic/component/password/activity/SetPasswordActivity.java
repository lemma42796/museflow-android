package com.ixuea.courses.mymusic.component.password.activity;

import static autodispose2.AutoDispose.autoDisposable;

import android.content.Context;
import android.content.Intent;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.login.activity.BaseLoginActivity;
import com.ixuea.courses.mymusic.component.password.mode.ui.SetPasswordPageData;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.databinding.ActivitySetPasswordBinding;
import com.ixuea.courses.mymusic.model.BaseId;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.StringUtil;
import com.ixuea.superui.toast.SuperToast;

import org.apache.commons.lang3.StringUtils;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 设置密码界面
 * <p>
 * 目前主要用于，找回密码时重设密码
 */
public class SetPasswordActivity extends BaseLoginActivity<ActivitySetPasswordBinding> {
    private SetPasswordPageData pageData;

    @Override
    protected void initDatum() {
        super.initDatum();
        pageData = extraData();
    }

    @Override
    protected void initListeners() {
        super.initListeners();

        binding.primary.setOnClickListener(v -> {
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

            User param = new User();
            param.setPhone(pageData.getPhone());
            param.setEmail(pageData.getEmail());
            param.setCode(pageData.getCode());
            param.setPassword(password);
            DefaultRepository.getInstance()
                    .resetPassword(param)
                    .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe(new HttpObserver<DetailResponse<BaseId>>() {
                        @Override
                        public void onSucceeded(DetailResponse<BaseId> data) {
                            login(User.createLogin(pageData.getPhone(), pageData.getEmail(), password));
                        }
                    });
        });
    }

    /**
     * 启动界面
     *
     * @param context
     */
    public static void start(Context context, SetPasswordPageData data) {
        Intent intent = new Intent(context, SetPasswordActivity.class);
        intent.putExtra(Constant.DATA, data);
        context.startActivity(intent);
    }
}