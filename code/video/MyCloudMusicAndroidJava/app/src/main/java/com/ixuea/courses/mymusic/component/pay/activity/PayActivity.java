package com.ixuea.courses.mymusic.component.pay.activity;

import static autodispose2.AutoDispose.autoDisposable;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.order.activity.OrderDetailActivity;
import com.ixuea.courses.mymusic.component.order.model.Order;
import com.ixuea.courses.mymusic.component.pay.model.event.AlipayStatusChangedEvent;
import com.ixuea.courses.mymusic.component.pay.model.event.PaySuccessEvent;
import com.ixuea.courses.mymusic.component.pay.model.event.WechatPayStatusChangedEvent;
import com.ixuea.courses.mymusic.component.pay.model.request.PayRequest;
import com.ixuea.courses.mymusic.component.pay.model.response.PayResponse;
import com.ixuea.courses.mymusic.component.pay.model.response.WechatPay;
import com.ixuea.courses.mymusic.databinding.ActivityPayBinding;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.AnalysisUtil;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.PayUtil;
import com.ixuea.courses.mymusic.util.SuperDateUtil;
import com.ixuea.superui.dialog.SuperDialog;
import com.ixuea.superui.toast.SuperToast;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import me.shihao.library.XRadioGroup;

/**
 * 支付界面
 */
public class PayActivity extends BaseTitleActivity<ActivityPayBinding> implements XRadioGroup.OnCheckedChangeListener {

    private int style;
    private String id;
    private Order data;
    private CountDownTimer countDownTimer;
    /**
     * 支付渠道
     */
    private int channel = Constant.ALIPAY;

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void initViews() {
        super.initViews();

        //默认选中支付宝
        binding.radioGroupPay.check(R.id.radio_alipay);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        style = extraInt(Constant.STYLE);
        id = extraId();

        loadData();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //设置支付方式改变监听器
        binding.radioGroupPay.setOnCheckedChangeListener(this);

        //支付按钮点击
        binding.primary.setOnClickListener(v -> loadPayData());
    }

    /**
     * 获取支付参数
     */
    private void loadPayData() {
        //创建参数
        PayRequest data = new PayRequest();

        //设置支付渠道
        data.setChannel(channel);

        DefaultRepository.getInstance()
                .orderPay(id, data)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<PayResponse>>() {
                    @Override
                    public void onSucceeded(DetailResponse<PayResponse> data) {
                        processPay(data.getData());
                    }
                });
    }


    /**
     * 处理支付
     *
     * @param data
     */
    private void processPay(PayResponse data) {
        switch (data.getChannel()) {
            case Constant.ALIPAY:
                //支付宝支付
                processAlipay(data.getPay());
                break;
            case Constant.WECHAT:
                //微信支付
                processWechat(data.getWechatPay());
                break;
            default:
                SuperToast.show(R.string.error_pay_channel);
                break;
        }
    }

    /**
     * 处理支付宝支付
     *
     * @param data
     */
    private void processAlipay(String data) {
        PayUtil.alipay(getHostActivity(), data);
    }

    /**
     * 处理微信支付
     *
     * @param data
     */
    private void processWechat(WechatPay data) {
        //把服务端返回的参数
        //设置到对应的字段
        PayReq request = new PayReq();

        request.appId = data.getAppid();
        request.partnerId = data.getPartnerid();
        request.prepayId = data.getPrepayid();
        request.nonceStr = data.getNoncestr();
        request.timeStamp = data.getTimestamp();
        request.packageValue = data.getPackageValue();
        request.sign = data.getSign();

        AppContext.getInstance().getWxapi().sendReq(request);
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        //获取订单数据，主要是获取价格，因为服务端可能更改了价格
        DefaultRepository.getInstance()
                .orderDetail(id)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<Order>>() {
                    @Override
                    public void onSucceeded(DetailResponse<Order> data) {
                        showData(data.getData());
                    }
                });
    }

    private void showData(Order data) {
        this.data = data;

        binding.price.setText(getResources().getString(R.string.price, data.getPrice()));

        if (data.isPaid()) {
            //发送支付成功通知
            EventBus.getDefault().post(new PaySuccessEvent());

            finish();

            //跳转到订单详情界面
            startActivityExtraId(OrderDetailActivity.class, data.getId());
        } else {
            startCountDown();
        }
    }

    private void startCountDown() {
        if (countDownTimer != null) {
            return;
        }

        //15分钟倒计时
        //注意：这里倒计时完成后，由于目前服务端实现不是很精确，所以订单可能此时还没有关闭
        //真实项目中服务端用队列实现，可以提交精确时间
        //如果还觉得的保险，客户端/获取服务端在返回数据时可以在判断时间，如果当前时间大于订单超时时间
        //也显示已经关闭，或者服务端在关闭，这样一系列方法实现后，就可以做到相对更精确关闭超时订单了
        //但这样实现太复杂了，毕竟这个课程不是专门讲解商城的，详细的在商城项目讲解
        //订单创建时间后，15分钟内完成支付
        DateTime createdAt = DateUtil.parse(data.getCreatedAt());

        //创建时间增加15分钟
        createdAt = DateUtil.offsetMinute(createdAt, 15);

        DateTime now = DateTime.now();

        //计算时间差；小时间，大时间
        long between = DateUtil.between(now, createdAt, DateUnit.MS);
        if (between <= 1000) {
            //小于1秒，已经超时了，可以弹窗提示，这里就直接关闭
            finish();
        }

        countDownTimer = new CountDownTimer(between, 1000) {
            /**
             * 每次间隔调用
             * <p>
             * 显示时:分:秒
             *
             * @param millisUntilFinished
             */
            @Override
            public void onTick(long millisUntilFinished) {
                String result = SuperDateUtil.ms2hms((int) millisUntilFinished);
                binding.payRemainingTime.setText(getString(R.string.pay_remaining_time, result));
            }

            /**
             * 倒计时完成
             */
            @Override
            public void onFinish() {
                //可以弹窗提示，支付超时，已经自动关闭订单
                //如果还需要购买，请重新下单
                //这里就直接关闭界面了
                finish();
            }
        };

        //启动定时器
        countDownTimer.start();
    }

    /**
     * 启动该界面
     *
     * @param context
     * @param style   类型
     * @param data    数据
     */
    public static void start(Context context, int style, String data) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra(Constant.STYLE, style);
        intent.putExtra(Constant.ID, data);
        context.startActivity(intent);
    }

    private void cancelCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelCountDown();
    }

    @Override
    public void onBackPressed() {
        showBackConfirmDialog();
    }

    /**
     * 显示返回确认对话框
     */
    private void showBackConfirmDialog() {
        SuperDialog.newInstance(getSupportFragmentManager())
                .setTitleRes(R.string.confirm_exit_pay)
                .setMessageRes(R.string.confirm_exit_pay_message)

                //确认按钮
                .setOnClickListener(v -> {

                }, R.string.continue_pay)
                //取消按钮
                .setOnCancelClickListener(v -> {
                    finish();
                    if (style == Constant.STYLE_CONFIRM_ORDER) {
                        //只有从确认订单跳转过来，才需要跳转到订单详情
                        startActivityExtraId(OrderDetailActivity.class, data.getId());
                    }
                }, R.string.confirm_departure)
                .show();
    }


    /**
     * 从确认订单跳转过来
     *
     * @param data
     */
    public static void startFromConfirmOrder(Context context, String data) {
        start(context, Constant.STYLE_CONFIRM_ORDER, data);
    }

    /**
     * 从订单（订单详情，订单列表等）跳转过来
     *
     * @param data
     */
    public static void startFromOrder(Context context, String data) {
        start(context, Constant.STYLE_ORDER, data);
    }

    /**
     * 支付方式单选按钮改变了
     *
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(XRadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_alipay:
                //支付宝
                channel = Constant.ALIPAY;
                break;
            case R.id.radio_wechat:
                channel = Constant.WECHAT;
                break;
            case R.id.radio_huawei_stage:
                channel = Constant.HUABEI_STAGE;
                break;
        }
    }

    /**
     * 支付宝支付状态改变了
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAlipayStatusChanged(AlipayStatusChangedEvent event) {
        String resultStatus = event.getData().getResultStatus();

        if ("9000".equals(resultStatus)) {
            //本地支付成功

            //不能依赖本地支付结果
            //一定要以服务端为准
            showLoading(R.string.hint_pay_wait);

            //延时3秒
            //因为支付宝回调我们服务端可能有延迟
            binding.primary.postDelayed(() -> {
                checkPayStatus();
            }, 3000);

            //这里就不根据服务端判断了
            //购买成功统计
            AnalysisUtil.onPurchase(getHostActivity(), true, data);
        } else if ("6001".equals(resultStatus)) {
            //支付取消
            SuperToast.show(R.string.error_pay_cancel);

            AnalysisUtil.onPurchase(getHostActivity(), false, data);
        } else {
            //支付失败
            SuperToast.show(R.string.error_pay_failed);

            AnalysisUtil.onPurchase(getHostActivity(), false, data);
        }
    }

    /**
     * 微信支付状态改变了
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWechatPayStatusChanged(WechatPayStatusChangedEvent event) {
        if (BaseResp.ErrCode.ERR_OK == event.getData().errCode) {
            //本地支付成功

            //不能依赖本地支付结果
            //一定要以服务端为准
            showLoading(R.string.hint_pay_wait);

            //延时3秒
            //因为回调我们服务端可能有延迟
            binding.primary.postDelayed(() -> {
                checkPayStatus();
            }, 3000);

            //这里就不根据服务端判断了
            //购买成功统计
            AnalysisUtil.onPurchase(getHostActivity(), true, data);
        } else if (BaseResp.ErrCode.ERR_USER_CANCEL == event.getData().errCode) {
            //支付取消
            SuperToast.show(R.string.error_pay_cancel);

            AnalysisUtil.onPurchase(getHostActivity(), false, data);
        } else {
            //支付失败
            SuperToast.show(R.string.error_pay_failed);

            AnalysisUtil.onPurchase(getHostActivity(), false, data);
        }
    }

    /**
     * 检查支付状态
     */
    private void checkPayStatus() {
        //隐藏加载对话框
        hideLoading();

        //请求订单详情
        loadData();
    }
}