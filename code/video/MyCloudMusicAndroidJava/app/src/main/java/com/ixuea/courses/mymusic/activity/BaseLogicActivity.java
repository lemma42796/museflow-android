package com.ixuea.courses.mymusic.activity;

import android.view.View;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.login.activity.LoginHomeActivity;
import com.ixuea.courses.mymusic.component.player.activity.MusicPlayerActivity;
import com.ixuea.courses.mymusic.manager.MusicListManager;
import com.ixuea.courses.mymusic.manager.impl.GlobalLyricManagerImpl;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.LiteORMUtil;
import com.ixuea.courses.mymusic.util.PreferenceUtil;
import com.ixuea.courses.mymusic.util.ServiceUtil;
import com.ixuea.courses.mymusic.util.SuperDarkUtil;
import com.ixuea.courses.mymusic.view.PlaceholderView;
import com.ixuea.superui.loading.SuperRoundLoadingDialogFragment;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

import cn.jiguang.analytics.android.api.JAnalyticsInterface;

/**
 * 项目中特有逻辑
 * <p>
 * 例如：显示迷你控制栏播放状态
 */
public class BaseLogicActivity extends BaseCommonActivity {

    protected PreferenceUtil sp;
    private WeakReference<SuperRoundLoadingDialogFragment> loadingWeakReference;
    private PlaceholderView placeholderView;
    private GlobalLyricManagerImpl globalLyricManager;

    @Override
    protected void initViews() {
        super.initViews();
        if (SuperDarkUtil.isDark(this)) {
            //状态栏文字白色
            QMUIStatusBarHelper.setStatusBarDarkMode(this);
        } else {
            //状态栏文字黑色
            QMUIStatusBarHelper.setStatusBarLightMode(this);
        }
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        globalLyricManager = GlobalLyricManagerImpl.getInstance(getApplicationContext());

        if (isRegisterEventBus()) {
            EventBus.getDefault().register(this);
        }

        sp = PreferenceUtil.getInstance(getHostActivity());
    }

    /**
     * 获取界面方法
     *
     * @return
     */
    protected BaseLogicActivity getHostActivity() {
        return this;
    }

    /**
     * 显示加载对话框
     */
    public void showLoading() {
        showLoading(getString(R.string.loading));
    }

    /**
     * 显示加载对话框
     */
    public void showLoading(int data) {
        showLoading(getString(data));
    }

    /**
     * 显示加载对话框
     */
    public void showLoading(String message) {
        if (loadingWeakReference == null || loadingWeakReference.get() == null) {
            loadingWeakReference = new WeakReference<>(
                    SuperRoundLoadingDialogFragment.newInstance(message)
            );
        }

        SuperRoundLoadingDialogFragment dialog = loadingWeakReference.get();
        if (dialog.getDialog() == null || !dialog.getDialog().isShowing()) {
            dialog.show(getSupportFragmentManager(), "SuperRoundLoadingDialogFragment");
        }
    }

    /**
     * 隐藏加载对话框
     */
    public void hideLoading() {
        SuperRoundLoadingDialogFragment dialog = loadingWeakReference.get();
        if (dialog != null) {
            dialog.dismiss();
            loadingWeakReference.clear();
        }
        loadingWeakReference = null;
    }

    /**
     * 加载数据方法
     *
     * @param isPlaceholder 是否是通过placeholder控件触发的
     */
    protected void loadData(boolean isPlaceholder) {

    }

    /**
     * 加载数据方法
     */
    protected void loadData() {
        loadData(false);
    }

    /**
     * 获取当前界面占位view
     *
     * @return
     */
    public PlaceholderView getPlaceholderView() {
        if (placeholderView == null) {
            placeholderView = findViewById(R.id.placeholder);

            if (placeholderView != null) {
                placeholderView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadData(true);
                    }
                });
            }
        }
        return placeholderView;
    }

    /**
     * 进入音乐播放界面
     */
    public void startMusicPlayerActivity() {
        //简单播放器界面
//        startActivity(SimplePlayerActivity.class);

        //黑胶唱片界面
        startActivity(MusicPlayerActivity.class);
    }

    /**
     * 获取播放列表管理器
     *
     * @return
     */
    protected MusicListManager getMusicListManager() {
        return MusicPlayerService.getListManager(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegisterEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 是否注册EventBus
     *
     * @return
     */
    protected boolean isRegisterEventBus() {
        return false;
    }

    /**
     * 获取数据库管理器
     *
     * @return
     */
    protected LiteORMUtil getOrm() {
        return LiteORMUtil.getInstance(getApplicationContext());
    }

    protected void toLogin() {
        startActivity(LoginHomeActivity.class);
    }

    /**
     * 只要用户登录了，才会执行代码块
     *
     * @param data
     */
    protected void loginAfter(Runnable data) {
        if (sp.isLogin()) {
            //已经登录了

            data.run();
        } else {
            toLogin();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ServiceUtil.isBackgroundRunning(getApplicationContext())) {
            //如果当前程序在前台

            //就尝试隐藏桌面歌词
            globalLyricManager.tryHide();
        }

        if (StringUtils.isNotBlank(pageId())) {
            //使用极光分析
            //统计页面
            JAnalyticsInterface.onPageStart(getHostActivity(), pageId());
        }
    }

    /**
     * 界面停止了
     */
    @Override
    protected void onStop() {
        super.onStop();
        //要在onStop方法里面显示
        //不能在onPause方法
        //因为当onPause方法执行的时候
        //系统的进程还没有后台
        if (ServiceUtil.isBackgroundRunning(getApplicationContext())) {
            //如果当前程序在后台

            //就尝试显示桌面歌词
            globalLyricManager.tryShow();
        }
    }

    //region 统计

    /**
     * 当页面暂停了
     * 例如：弹窗；或者切换到后台
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (StringUtils.isNotBlank(pageId())) {
            //页面结束
            JAnalyticsInterface.onPageEnd(getHostActivity(), pageId());
        }
    }

    /**
     * 返回页面标识
     *
     * @return
     */
    protected String pageId() {
        return null;
    }
    //endregion
}
