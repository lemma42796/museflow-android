package com.ixuea.courses.mymusic.fragment;

import android.view.View;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseLogicActivity;
import com.ixuea.courses.mymusic.component.login.activity.LoginHomeActivity;
import com.ixuea.courses.mymusic.manager.MusicListManager;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.LiteORMUtil;
import com.ixuea.courses.mymusic.util.PreferenceUtil;
import com.ixuea.courses.mymusic.view.PlaceholderView;

import org.greenrobot.eventbus.EventBus;

/**
 * 项目中特有逻辑
 */
public abstract class BaseLogicFragment extends BaseCommonFragment {
    private PlaceholderView placeholderView;
    protected PreferenceUtil sp;

    @Override
    protected void initDatum() {
        super.initDatum();
        if (isRegisterEventBus()) {
            EventBus.getDefault().register(this);
        }

        sp = PreferenceUtil.getInstance(getHostActivity());
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
            placeholderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadData(true);
                }
            });
        }
        return placeholderView;
    }

    /**
     * 获取播放列表管理器
     *
     * @return
     */
    protected MusicListManager getMusicListManager() {
        return MusicPlayerService.getListManager(getHostActivity().getApplicationContext());
    }

    @Override
    public void onDestroy() {
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
        return LiteORMUtil.getInstance(getHostActivity().getApplicationContext());
    }

    /**
     * 进入音乐播放界面
     */
    public void startMusicPlayerActivity() {
        ((BaseLogicActivity) getHostActivity()).startMusicPlayerActivity();
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
}
