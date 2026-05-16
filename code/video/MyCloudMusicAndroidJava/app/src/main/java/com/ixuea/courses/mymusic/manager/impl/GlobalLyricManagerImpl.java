package com.ixuea.courses.mymusic.manager.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.ixuea.courses.mymusic.MainActivity;
import com.ixuea.courses.mymusic.component.lyric.view.GlobalLyricView;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.component.splash.activity.SplashActivity;
import com.ixuea.courses.mymusic.manager.GlobalLyricManager;
import com.ixuea.courses.mymusic.manager.MusicListManager;
import com.ixuea.courses.mymusic.manager.MusicPlayerListener;
import com.ixuea.courses.mymusic.manager.MusicPlayerManager;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.NotificationUtil;
import com.ixuea.courses.mymusic.util.PreferenceUtil;
import com.ixuea.courses.mymusic.util.ScreenUtil;
import com.ixuea.courses.mymusic.util.SizeUtil;
import com.ixuea.courses.mymusic.util.WidgetUtil;

/**
 * 全局（桌面）歌词管理器实现
 */
public class GlobalLyricManagerImpl implements GlobalLyricManager, MusicPlayerListener, GlobalLyricView.OnGlobalLyricDragListener, GlobalLyricView.GlobalLyricListener {
    private static GlobalLyricManagerImpl instance;
    private final Context context;
    private final MusicPlayerManager musicPlayerManager;

    /**
     * 偏好设置工具类
     */
    private final PreferenceUtil sp;

    /**
     * 窗口管理器
     */
    private WindowManager windowManager;

    /**
     * 窗体的布局样式
     */
    private WindowManager.LayoutParams layoutParams;

    /**
     * 全局歌词view
     */
    private GlobalLyricView globalLyricView;
    private BroadcastReceiver unlockGlobalLyricBroadcastReceiver;
    private GlobalLyricView.GlobalLyricOtherListener globalLyricOtherListener;

    public GlobalLyricManagerImpl(Context context) {
        this.context = context.getApplicationContext();

        //初始化偏好设置工具类
        sp = PreferenceUtil.getInstance(this.context);

        //初始化音乐播放管理器
        musicPlayerManager = MusicPlayerService.getMusicPlayerManager(this.context);

        //添加播放监听器
        musicPlayerManager.addMusicPlayerListener(this);

        //初始化窗口管理器
        initWindowManager();

        //从偏好设置中获取是否要显示全局歌词
        if (sp.isShowGlobalLyric()) {
            //创建全局歌词View
            initGlobalLyricView();

            //如果原来锁定了歌词
            if (sp.isGlobalLyricLock()) {
                //锁定歌词
                lock();
            }
        }
    }

    public synchronized static GlobalLyricManagerImpl getInstance(Context context) {
        if (instance == null) {
            instance = new GlobalLyricManagerImpl(context);
        }
        return instance;
    }

    /**
     * 锁定全局歌词
     */
    private void lock() {
        //保存全局歌词锁定状态
        sp.setGlobalLyricLock(true);

        //设置全局歌词控件状态
        setGlobalLyricStatus();

        //显示简单模式
        globalLyricView.simpleStyle();

        //更新布局
        updateView();

        //显示解锁全局歌词通知
        NotificationUtil.showUnlockGlobalLyricNotification(context);

        //注册接收解锁全局歌词广告接收器
        registerUnlockGlobalLyricReceiver();
    }

    /**
     * 注册接收解锁全局歌词广告接收器
     */
    private void registerUnlockGlobalLyricReceiver() {
        if (unlockGlobalLyricBroadcastReceiver == null) {
            //创建广播接受者
            unlockGlobalLyricBroadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    if (Constant.ACTION_UNLOCK_LYRIC.equals(intent.getAction())) {
                        //歌词解锁事件
                        unlock();
                    }
                }
            };

            IntentFilter intentFilter = new IntentFilter();

            //只监听歌词解锁事件
            intentFilter.addAction(Constant.ACTION_UNLOCK_LYRIC);

            //注册
            context.registerReceiver(unlockGlobalLyricBroadcastReceiver, intentFilter);
        }
    }

    /**
     * 解锁歌词
     */
    private void unlock() {
        //设置没有锁定歌词
        sp.setGlobalLyricLock(false);

        //设置歌词状态
        setGlobalLyricStatus();

        //解锁后显示标准样式
        globalLyricView.normalStyle();

        //更新view
        updateView();

        //清除歌词解锁通知
        NotificationUtil.clearUnlockGlobalLyricNotification(context);

        //解除接收全局歌词事件广播接受者
        unregisterUnlockGlobalLyricReceiver();
    }

    /**
     * 解除接收全局歌词事件广播接受者
     */
    private void unregisterUnlockGlobalLyricReceiver() {
        if (unlockGlobalLyricBroadcastReceiver != null) {
            context.unregisterReceiver(unlockGlobalLyricBroadcastReceiver);
            unlockGlobalLyricBroadcastReceiver = null;
        }
    }

    @Override
    public void show() {
        //检查全局悬浮窗权限
        if (!Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(context, SplashActivity.class);
            intent.setAction(Constant.ACTION_LYRIC);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        }

        //初始化全局歌词控件
        initGlobalLyricView();

        //设置显示了全局歌词
        sp.setShowGlobalLyric(true);

        WidgetUtil.onGlobalLyricShowStatusChanged(context, isShowing());
    }

    @Override
    public void hide() {
        if (globalLyricView != null) {
            //移出全局歌词控件
            windowManager.removeView(globalLyricView);
            globalLyricView = null;
        }

        //设置没有显示全局歌词
        sp.setShowGlobalLyric(false);

        WidgetUtil.onGlobalLyricShowStatusChanged(context, isShowing());
    }

    @Override
    public boolean isShowing() {
        return globalLyricView != null;
    }

    /**
     * 初始化窗口管理器
     * <p>
     * 因为全局View是通过他显示的
     * <p>
     * 可以在构造方法中初始化
     * 也可以延迟到要显示的时候才初始化
     * 这样效果更高
     */
    private void initWindowManager() {
        if (windowManager == null) {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            //窗体的布局样式
            layoutParams = new WindowManager.LayoutParams();

            //设置窗体显示类型
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //6.0及以上版本要设置该类型
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }

            //设置显示的模式
            layoutParams.format = PixelFormat.RGBA_8888;

            //设置对齐的方法
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;

            //设置窗体宽度和高度
            int screenWith = ScreenUtil.getScreenWith(context);
            //和屏幕一样宽
            layoutParams.width = screenWith;

            //高度是包裹内容
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            //从偏好设置中获取坐标
            layoutParams.y = sp.getGlobalLyricViewY();

            //设置全局歌词view状态
            setGlobalLyricStatus();
        }
    }

    /**
     * 设置全局歌词控件状态
     */
    private void setGlobalLyricStatus() {
        if (sp.isGlobalLyricLock()) {
            //已经锁定了

            //窗口不接受任何事件
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        } else {
            //没有锁定

            //窗口可以接受触摸事件
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
    }

    /**
     * 创建全局歌词View
     */
    private void initGlobalLyricView() {
//        //创建一个测试文本
//        TextView tv = new TextView(context);
//        tv.setBackgroundColor(context.getColor(R.color.primary));
//        tv.setText("这是一个简单的文本");
//
//        windowManager.addView(tv,layoutParams);

        if (globalLyricView == null) {
            globalLyricView = new GlobalLyricView(context);

            //设置歌词拖拽回调
            globalLyricView.setLyricDragListener(this);

            //设置歌词view监听器
            globalLyricView.setGlobalLyricListener(this);

            globalLyricView.setGlobalLyricOtherListener(globalLyricOtherListener);
        }

        if (globalLyricView.getParent() == null) {
            //如果没有添加
            //就添加
            windowManager.addView(globalLyricView, layoutParams);
        }

        //显示播放状态
        showMusicPlayStatus();

        //显示初始化数据
        showLyricData();
    }

    /**
     * 显示音乐播放状态
     */
    private void showMusicPlayStatus() {
        globalLyricView.setPlay(musicPlayerManager.isPlaying());
    }

    private void showLyricData() {
        if (globalLyricView == null) {
            return;
        }

        //获取当前播放的音乐
        if (getMusicListManager().getData() == null || getMusicListManager().getData().getParsedLyric() == null) {
            //清空原来的歌词
            globalLyricView.clearLyric();
        } else {
            //设置歌词模式
            globalLyricView.setAccurate(getMusicListManager().getData().getParsedLyric().isAccurate());

            //如果显示了歌词
            //执行一次进度方法
            //相当于初始化数据
            onProgress(getMusicListManager().getData());
        }
    }

    /**
     * 获取播放列表管理器
     *
     * @return
     */
    protected MusicListManager getMusicListManager() {
        return MusicPlayerService.getListManager(context);
    }

    //region 播放管理器回调
    @Override
    public void onPaused(Song data) {
        if (!hasGlobalLyricView()) {
            return;
        }

        globalLyricView.setPlay(false);
    }


    @Override
    public void onPlaying(Song data) {
        if (!hasGlobalLyricView()) {
            return;
        }

        globalLyricView.setPlay(true);
    }

    @Override
    public void onProgress(Song data) {
        if (data.getParsedLyric() == null) {
            return;
        }

        if (!hasGlobalLyricView()) {
            return;
        }

        globalLyricView.onProgress(data);
    }

    @Override
    public void onLyricReady(Song data) {
        showLyricData();
    }
    //endregion

    private boolean hasGlobalLyricView() {
        return globalLyricView != null;
    }

    /**
     * 全局歌词拖拽回调
     *
     * @param y y轴方向上移动的距离
     */
    @Override
    public void onGlobalLyricDrag(int y) {
        layoutParams.y = y - SizeUtil.getStatusBarHeight(context);

        //更新view
        updateView();

        //保存歌词y坐标
        sp.setGlobalLyricViewY(layoutParams.y);
    }

    /**
     * 更新布局
     */
    private void updateView() {
        windowManager.updateViewLayout(globalLyricView, layoutParams);
    }

    public void setGlobalLyricOtherListener(GlobalLyricView.GlobalLyricOtherListener globalLyricOtherListener) {
        this.globalLyricOtherListener = globalLyricOtherListener;
    }

    @Override
    public void tryHide() {
        if (sp.isShowGlobalLyric()) {
            globalLyricView.setVisibility(View.GONE);
        }
    }

    @Override
    public void tryShow() {
        if (sp.isShowGlobalLyric()) {
            globalLyricView.setVisibility(View.VISIBLE);
        }
    }

    //region 歌词view回调
    @Override
    public void onLogoClick() {
        Intent intent = new Intent(context, MainActivity.class);

        intent.setAction(Constant.ACTION_MUSIC_PLAYER_PAGE);

        //在Activity以外启动界面
        //都要写这个标识
        //具体的还比较复杂
        //基础课程中讲解
        //这里学会这样用就行了
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

    @Override
    public void onLockClick() {
        lock();
    }

    @Override
    public void onPreviousClick() {
        getMusicListManager().play(getMusicListManager().previous());
    }

    @Override
    public void onPlayClick() {
        if (musicPlayerManager.isPlaying()) {
            getMusicListManager().pause();
        } else {
            getMusicListManager().resume();
        }
    }

    @Override
    public void onNextClick() {
        getMusicListManager().play(getMusicListManager().next());
    }
    //endregion
}
