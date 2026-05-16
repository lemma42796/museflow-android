package com.ixuea.courses.mymusic.component.input.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.ixuea.common.util.RegularUtil;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.adapter.TextWatcherAdapter;
import com.ixuea.courses.mymusic.component.input.activity.model.ui.InputCodePageData;
import com.ixuea.courses.mymusic.databinding.ActivityInputUserIdentityBinding;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.superui.toast.SuperToast;

import org.apache.commons.lang3.StringUtils;

/**
 * 输入用户邮箱，手机号通用界面
 * <p>
 * 主要用到手机号登录，找回密码时，输入手机号，输入邮箱
 * 根据启动参数区分是那种功能
 * <p>
 * 之所以设计为当前界面只是输入，下一个界面才发送验证码，原因是限制发送验证了
 * 又更改了输入框的手机号，邮箱
 */
public class InputUserIdentityActivity extends BaseTitleActivity<ActivityInputUserIdentityBinding> {
    private int style;

    /**
     * 手机号登录
     *
     * @param context
     */
    public static void startWithPhoneLogin(Context context) {
        start(context, Constant.STYLE_PHONE_LOGIN);
    }

    /**
     * 找回密码
     *
     * @param context
     */
    public static void startWithForgotPassword(Context context) {
        start(context, Constant.STYLE_FORGOT_PASSWORD);
    }

    /**
     * 启动界面
     *
     * @param context
     */
    private static void start(Context context, int style) {
        Intent intent = new Intent(context, InputUserIdentityActivity.class);
        intent.putExtra(Constant.STYLE, style);
        context.startActivity(intent);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        style = extraInt(Constant.STYLE);
        switch (style) {
            case Constant.STYLE_PHONE_LOGIN:
                //手机号登录
                setTitle(R.string.phone_login);

                binding.username.setHint(R.string.enter_phone);

                //输入手机号
                binding.username.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                break;
            case Constant.STYLE_FORGOT_PASSWORD:
                //找回密码
                setTitle(R.string.forgot_password);

                binding.username.setHint(R.string.enter_phone_or_email);
                break;
        }
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //输入框改变了
        binding.username.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                boolean notBlank = StringUtils.isNotBlank(s.toString().trim());
                binding.primary.setEnabled(notBlank);
            }
        });

        binding.primary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = binding.username.getText().toString().trim();
                boolean isPhone = RegularUtil.isPhone(content);
                if (isPhone || RegularUtil.isEmail(content)) {
                    InputCodePageData pageData = new InputCodePageData(style);

                    if (isPhone) {
                        pageData.setPhone(content);
                    } else {
                        pageData.setEmail(content);
                    }

                    InputCodeActivity.start(getHostActivity(), pageData);
                } else {
                    SuperToast.show(R.string.error_username_format);
                }
            }
        });
    }
}