package com.ixuea.courses.mymusic.component.input.activity;

import static autodispose2.AutoDispose.autoDisposable;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.input.activity.model.ui.InputCodePageData;
import com.ixuea.courses.mymusic.component.input.model.CodeRequest;
import com.ixuea.courses.mymusic.component.login.activity.BaseLoginActivity;
import com.ixuea.courses.mymusic.component.password.activity.SetPasswordActivity;
import com.ixuea.courses.mymusic.component.password.mode.ui.SetPasswordPageData;
import com.ixuea.courses.mymusic.databinding.ActivityInputCodeBinding;
import com.ixuea.courses.mymusic.model.Base;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.Constant;
import com.king.view.splitedittext.SplitEditText;

import org.apache.commons.lang3.StringUtils;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 输入验证码界面
 * <p>
 * 可以是手机验证，也可以邮箱验证码
 */
public class InputCodeActivity extends BaseLoginActivity<ActivityInputCodeBinding> {

    private InputCodePageData pageData;
    private CodeRequest codeRequest;
    private int codeStyle;
    private CountDownTimer countDownTimer;

    @Override
    protected void initDatum() {
        super.initDatum();
        pageData = extraData();

        codeRequest = new CodeRequest();

        String target;
        if (StringUtils.isNotBlank(pageData.getPhone())) {
            target = pageData.getPhone();
            codeStyle = Constant.VALUE10;
            codeRequest.setPhone(pageData.getPhone());
        } else {
            target = pageData.getEmail();
            codeStyle = Constant.VALUE0;
            codeRequest.setEmail(pageData.getEmail());
        }

        binding.codeSendTarget.setText(getString(R.string.verification_code_sent_to, target));

        sendCode();
    }

    private void sendCode() {
        DefaultRepository.getInstance()
                .sendCode(codeStyle, codeRequest)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<Base>>() {
                    @Override
                    public void onSucceeded(DetailResponse<Base> data) {
                        //发送成功了

                        //开始倒计时
                        startCountDown();
                    }
                });
    }

    /**
     * 开始倒计时
     * 现在没有保存退出的状态
     * 也就说，返回在进来就可以点击了
     */
    private void startCountDown() {
        //倒计时的总时间,间隔
        //单位是毫秒
        countDownTimer = new CountDownTimer(60000, 1000) {

            /**
             * 间隔时间调用
             * @param millisUntilFinished
             */
            @Override
            public void onTick(long millisUntilFinished) {
                binding.resend.setText(getString(R.string.resend_count, millisUntilFinished / 1000));
            }

            /**
             * 倒计时完成
             */
            @Override
            public void onFinish() {
                binding.resend.setText(R.string.resend);

                binding.resend.setEnabled(true);
            }
        };

        //启动
        countDownTimer.start();

        binding.resend.setEnabled(false);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //设置监听
        binding.code.setOnTextInputListener(new SplitEditText.OnSimpleTextInputListener() {
            @Override
            public void onTextInputCompleted(String s) {
                processNext(s);
            }
        });

        binding.resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode();
            }
        });
    }

    private void processNext(String data) {
        if (pageData.getStyle() == Constant.STYLE_PHONE_LOGIN) {
            //手机号验证码登录
            login(pageData.getPhone(), data);
        } else {
            //先校验验证码
            codeRequest.setCode(data);
            DefaultRepository.getInstance()
                    .checkCode(codeRequest)
                    .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe(new HttpObserver<DetailResponse<Base>>() {
                        @Override
                        public void onSucceeded(DetailResponse<Base> d) {
                            //重设密码
                            SetPasswordActivity.start(getHostActivity(), new SetPasswordPageData(
                                    pageData.getPhone(),
                                    pageData.getEmail(),
                                    data
                            ));
                        }

                        @Override
                        public boolean onFailed(DetailResponse<Base> data, Throwable e) {
                            //清除验证码输入的内容
                            binding.code.setText("");
                            return super.onFailed(data, e);
                        }
                    });

        }
    }

    /**
     * 界面销毁时调用
     */
    @Override
    public void onDestroy() {
        //销毁定时器
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        super.onDestroy();
    }

    public static void start(Context context, InputCodePageData data) {
        Intent intent = new Intent(context, InputCodeActivity.class);
        intent.putExtra(Constant.DATA, data);
        context.startActivity(intent);
    }

}