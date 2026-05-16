package com.ixuea.courses.mymusic.component.ad.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import com.ixuea.courses.mymusic.MainActivity;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseViewModelActivity;
import com.ixuea.courses.mymusic.component.ad.model.Ad;
import com.ixuea.courses.mymusic.databinding.ActivityAdBinding;
import com.ixuea.courses.mymusic.util.AnalysisUtil;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.FileUtil;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.IntentUtil;
import com.ixuea.superui.util.SuperViewUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayer;

import java.io.File;

/**
 * 自己实现的广告启动界面，支持图片，视频全屏显示
 * 只是实现这样广告相关流程，没有像真正广告sdk那样有收益
 */
public class AdActivity extends BaseViewModelActivity<ActivityAdBinding> implements ITXVodPlayListener {
    private Ad data;
    private String action;
    private CountDownTimer adCountDownTimer;
    private TXVodPlayer player;

    @Override
    protected void initViews() {
        super.initViews();
        //设置沉浸式状态栏
        QMUIStatusBarHelper.translucent(this);
    }

    @Override
    protected void initDatum() {
        super.initDatum();

        //获取广告信息
        data = sp.getSplashAd();
        if (data == null) {
            next();
            return;
        }

        //显示广告信息
        show();

        binding.shimmer.startShimmer();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //跳过广告按钮
        binding.skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消倒计时
                cancelCountDown();

                next();

                AnalysisUtil.onSkipAd(getHostActivity(), sp.getUserId());
            }
        });

        //点击广告按钮
        binding.primary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消倒计时
                cancelCountDown();

                action = Constant.ACTION_AD;

                next();
            }
        });
    }

    private void next() {
        //创建意图
        Intent intent = new Intent(getHostActivity(), MainActivity.class);
        IntentUtil.cloneIntent(getIntent(), intent);

        if (data != null) {
            //添加广告
            intent.putExtra(Constant.AD, data);
        }

        if (action != null) {
            //要跳转到广告界面
            //先启动主界面的
            //好处是
            //用户在广告界面
            //返回正好看到的主界面
            //这样才符合应用逻辑
            intent.setAction(action);
        }

        startActivity(intent);

        //关闭当前界面
        finish();
    }

    private void show() {
        File targetFile = FileUtil.adFile(getHostActivity(), data.getIcon());
        if (!targetFile.exists()) {
            //记录日志，因为正常来说，只要保存了，文件不能丢失
            next();
            return;
        }

        SuperViewUtil.show(binding.adControl);

        switch (data.getStyle()) {
            case Constant.VALUE0:
                showImageAd(targetFile);
                break;
            case Constant.VALUE10:
                showVideoAd(targetFile);
                break;
        }
    }

    /**
     * 显示视频广告
     *
     * @param data
     */
    private void showVideoAd(File data) {
        SuperViewUtil.show(binding.video);
        SuperViewUtil.show(binding.preload);

        //在要用到的时候在初始化，更节省资源，当然播放器控件也可以在这里动态创建
        //设置播放监听器

        //创建 player 对象
        player = new TXVodPlayer(getHostActivity());

        //静音，当然也可以在界面上添加静音切换按钮
        player.setMute(true);

        //关键 player 对象与界面 view
        player.setPlayerView(binding.video);

        //设置播放监听器
        player.setVodListener(this);

        //铺满
        binding.video.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);

        //开启硬件加速
        player.enableHardwareDecode(true);

        player.startPlay(data.getAbsolutePath());
    }

    /**
     * 显示图片广告
     *
     * @param data
     */
    private void showImageAd(File data) {
        ImageUtil.showLocalImage(getHostActivity(), binding.image, data.getAbsolutePath());

        startCountDown(5000);
    }

    private void startCountDown(int data) {
        //创建倒计时
        adCountDownTimer = new CountDownTimer(data, 1000) {
            /**
             * 每次间隔调用
             *
             * @param millisUntilFinished
             */
            @Override
            public void onTick(long millisUntilFinished) {
                binding.skip.setText(getString(R.string.skip_ad_count, millisUntilFinished / 1000 + 1));
            }

            /**
             * 倒计时完成
             */
            @Override
            public void onFinish() {
                //执行下一步方法
                next();
            }
        };

        //启动定时器
        adCountDownTimer.start();
    }

    /**
     * 结束播放时记得销毁 view 控件，尤其是在下次 startPlay 之前，否则会产生大量的内存泄露以及闪屏问题。
     * <p>
     * 同时，在退出播放界面时，记得一定要调用渲染 View 的onDestroy()函数，否则可能会产生内存泄露和“Receiver not registered”报警。
     * https://cloud.tencent.com/document/product/881/20216
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelCountDown();

        if (player != null) {
            player.stopPlay(true); // true 代表清除最后一帧画面
        }

        binding.video.onDestroy();
    }

    private void cancelCountDown() {
        if (adCountDownTimer != null) {
            adCountDownTimer.cancel();
            adCountDownTimer = null;
        }
    }

    //region 视频播放监听

    /**
     * 播放事件
     *
     * @param txVodPlayer
     * @param event
     * @param bundle
     */
    @Override
    public void onPlayEvent(TXVodPlayer txVodPlayer, int event, Bundle bundle) {
        if (TXLiveConstants.PLAY_EVT_PLAY_BEGIN == event) {
            //视频播放开始，如果有转菊花什么的这个时候该停了

        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {
            // 视频I帧到达，开始播放
        } else if (TXLiveConstants.PLAY_EVT_PLAY_END == event) {
            //视频播放结束

            next();
        } else if (TXLiveConstants.PLAY_EVT_PLAY_PROGRESS == event) {
            if (adCountDownTimer != null) {
                return;
            }

            // 加载进度, 单位是毫秒
//                int duration_ms = bundle.getInt(TXLiveConstants.EVT_PLAYABLE_DURATION_MS);
//                mLoadBar.setProgress(duration_ms);

            // 播放进度, 单位是毫秒
            int progress = bundle.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS);

            // 视频总长, 单位是毫秒
            int duration = bundle.getInt(TXLiveConstants.EVT_PLAY_DURATION_MS);

            startCountDown(duration);
        }
    }

    /**
     * 网络状态
     *
     * @param txVodPlayer
     * @param bundle
     */
    @Override
    public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {

    }
    //endregion

    @Override
    public void onPause() {
        super.onPause();
        pausePlay();
    }

    @Override
    public void onResume() {
        super.onResume();
        startPlay();
    }

    private void startPlay() {
        if (player != null) {
            player.resume();
        }
    }

    private void pausePlay() {
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected String pageId() {
        return "Ad";
    }
}