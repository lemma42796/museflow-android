package com.ixuea.courses.mymusic;

import static autodispose2.AutoDispose.autoDisposable;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;

import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.ixuea.courses.mymusic.activity.BaseViewModelActivity;
import com.ixuea.courses.mymusic.adapter.OnPageChangeListenerAdapter;
import com.ixuea.courses.mymusic.component.about.activity.AboutActivity;
import com.ixuea.courses.mymusic.component.about.activity.AboutCodeActivity;
import com.ixuea.courses.mymusic.component.ad.model.Ad;
import com.ixuea.courses.mymusic.component.address.activity.AddressActivity;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.cart.activity.ShopCartActivity;
import com.ixuea.courses.mymusic.component.chat.activity.ChatActivity;
import com.ixuea.courses.mymusic.component.chat.model.event.MessageUnreadCountChangedEvent;
import com.ixuea.courses.mymusic.component.code.activity.CodeActivity;
import com.ixuea.courses.mymusic.component.conversation.activity.ConversationActivity;
import com.ixuea.courses.mymusic.component.login.activity.LoginHomeActivity;
import com.ixuea.courses.mymusic.component.main.adapter.MainAdapter;
import com.ixuea.courses.mymusic.component.main.tab.TabEntity;
import com.ixuea.courses.mymusic.component.order.activity.OrderActivity;
import com.ixuea.courses.mymusic.component.product.activity.ProductActivity;
import com.ixuea.courses.mymusic.component.scan.activity.ScanActivity;
import com.ixuea.courses.mymusic.component.search.activity.SearchActivity;
import com.ixuea.courses.mymusic.component.setting.activity.SettingActivity;
import com.ixuea.courses.mymusic.component.sheet.activity.SheetDetailActivity;
import com.ixuea.courses.mymusic.component.user.activity.UserActivity;
import com.ixuea.courses.mymusic.component.user.activity.UserDetailActivity;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.component.web.activity.WebActivity;
import com.ixuea.courses.mymusic.databinding.ActivityMainBinding;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.IntentUtil;
import com.ixuea.courses.mymusic.util.ServiceUtil;
import com.ixuea.courses.mymusic.util.StringUtil;
import com.ixuea.superui.dialog.SuperDialog;
import com.ixuea.superui.process.SuperProcessUtil;
import com.ixuea.superui.toast.SuperToast;
import com.ixuea.superui.util.SuperShortcutUtil;
import com.ixuea.superui.util.SuperViewUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import cn.jpush.android.api.JPushInterface;
import io.rong.imlib.RongIMClient;
import timber.log.Timber;

/**
 * 主界面
 */
public class MainActivity extends BaseViewModelActivity<ActivityMainBinding> {
    /**
     * 底部指示器（tab）文本，图标，选中的图标
     */
    private static final int[] indicatorTitles = new int[]{R.string.discovery, R.string.video, R.string.me, R.string.feed, R.string.live};
    private static final int[] indicatorIcons = new int[]{R.drawable.discovery, R.drawable.video, R.drawable.me, R.drawable.feed, R.drawable.live};
    private static final int[] indicatorSelectedIcons = new int[]{R.drawable.discovery_selected, R.drawable.video_selected, R.drawable.me_selected, R.drawable.feed_selected, R.drawable.live_selected};
    private static final String TAG = "MainActivity";
    private MainAdapter adapter;

    /**
     * 处理完成后，是否关闭界面
     */
    private boolean isNeedFinish;
    private ActivityResultLauncher<Intent> requestDrawOverlaysCallback;

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.getInstance().onInit();

        //尝试启动service
        ServiceUtil.startService(getApplicationContext(), MusicPlayerService.class);

        if (Constant.ACTION_LYRIC.equals(getIntent().getAction())) {
            isNeedFinish = true;

            if (Settings.canDrawOverlays(getHostActivity())) {
                Timber.d("onCreate canDrawOverlays finish");
                ServiceUtil.startService(getApplicationContext(), IntentUtil.createMusicPlayerServiceIntent(getApplicationContext(), Constant.ACTION_LYRIC));

                finish();
            }
        }

        //注册请求悬浮窗权限回调
        requestDrawOverlaysCallback = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(getHostActivity())) {
                        //授权成功
                        ServiceUtil.startService(getApplicationContext(), IntentUtil.createMusicPlayerServiceIntent(getApplicationContext(), Constant.ACTION_LYRIC));
                    } else {
                        //授权失败
                        Timber.d("request DrawOverlays fail");
                    }
                }
            }
        });
    }

    @Override
    protected void initViews() {
        super.initViews();
        //状态栏透明，内容显示到状态栏
        QMUIStatusBarHelper.translucent(this);

        //缓存页面数量
        // 默认是缓存一个
        binding.content.list.setOffscreenPageLimit(5);

        //指示器
        ArrayList<CustomTabEntity> indicatorTabs = new ArrayList<>();
        for (int i = 0; i < indicatorTitles.length; i++) {
            indicatorTabs.add(
                    new TabEntity(
                            getString(indicatorTitles[i]),
                            indicatorSelectedIcons[i],
                            indicatorIcons[i]
                    )
            );
        }
        binding.content.indicator.setTabData(indicatorTabs);

        //动态tab显示消息提醒
        binding.content.indicator.showDot(3);
    }

    @Override
    protected void initDatum() {
        super.initDatum();

        adapter = new MainAdapter(getHostActivity(), getSupportFragmentManager());
        binding.content.list.setAdapter(adapter);

        adapter.setDatum(Arrays.asList(0, 1, 2, 3, 4));

        processIntent(getIntent());

        initShortcut();

        showMessageUnreadCount();

        String registrationID = JPushInterface.getRegistrationID(this);
        Timber.d("jpush registrationID %s", registrationID);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        binding.avatar.postDelayed(new Runnable() {
            @Override
            public void run() {
                postRun(intent);
            }
        }, 800);
    }

    private void postRun(Intent intent) {
        String action = intent.getAction();
        if (Constant.ACTION_LOGIN.equals(action)) {
            //跳转到登录主界面
            startActivity(LoginHomeActivity.class);
        } else if (Constant.ACTION_AD.equals(action)) {
            //广告点击
            processAdClick(getIntent().getParcelableExtra(Constant.AD));
        } else if (Constant.ACTION_MUSIC_PLAYER_PAGE.equals(action)) {
            startMusicPlayerActivity();
        } else if (Constant.ACTION_LYRIC.equals(action)) {
            //显示或隐藏桌面歌词点击了

            //请求获取权限，当然真实项目中，可以先显示一个提示对话框
            //告诉用户为什么需要该权限，然后在调用方法获取
            requestDrawOverlays();
        } else if (Constant.ACTION_SCAN.equals(action)) {
            //扫一扫
            startActivity(ScanActivity.class);
        } else if (Constant.ACTION_SEARCH.equals(action)) {
            //搜索
            startActivity(SearchActivity.class);
        } else if (Constant.ACTION_CHAT.equals(action)) {
            //本地显示的消息通知点击

            //要跳转到聊天界面
            String id = intent.getStringExtra(Constant.ID);
            startActivityExtraId(ChatActivity.class, id);
        } else if (Constant.ACTION_PUSH.equals(action)) {

            String id = intent.getStringExtra(Constant.PUSH);
            String sheetId = intent.getStringExtra(Constant.SHEET_ID);
            if (StringUtils.isNotBlank(id)) {
                //聊天通知点击
                startActivityExtraId(ChatActivity.class, id);
            } else if (StringUtils.isNotBlank(sheetId)) {
                //跳转到歌单详情
                startActivityExtraId(SheetDetailActivity.class, sheetId);
            }

        }
    }

    /**
     * 请求悬浮权限
     *
     * @return
     */
    private void requestDrawOverlays() {
        if (!Settings.canDrawOverlays(getHostActivity())) {
            //如果没有显示浮窗的权限
            //就跳转到设置界面
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getHostActivity().getPackageName()));
//            startActivityForResult(intent, Constant.REQUEST_OVERLAY_PERMISSION);

            requestDrawOverlaysCallback.launch(intent);
        } else {
            ServiceUtil.startService(getApplicationContext(), IntentUtil.createMusicPlayerServiceIntent(getApplicationContext(), Constant.ACTION_LYRIC));
        }
    }

//    /**
//     * 使用startActivityForResult启动界面后的回调
//     *
//     * @param requestCode
//     * @param resultCode
//     * @param data
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case Constant.REQUEST_OVERLAY_PERMISSION:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (Settings.canDrawOverlays(getHostActivity())) {
//                        //授权成功
//                        ServiceUtil.startService(getApplicationContext(), IntentUtil.createMusicPlayerServiceIntent(getApplicationContext(), Constant.ACTION_LYRIC));
//                    } else {
//                        //授权失败
//                        Timber.d("request DrawOverlays fail");
//                    }
//                }
//                break;
//        }
//    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //用户容器点击
        binding.userContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                if (sp.isLogin()) {
                    startActivityExtraId(UserDetailActivity.class, sp.getUserId());
                } else {
                    startActivity(LoginHomeActivity.class);
                }
            }
        });

        //打开侧滑按钮点击
        binding.content.leftButton.setOnClickListener(v -> {
            binding.drawer.openDrawer(GravityCompat.START);
        });

        //搜索容器点击
        binding.content.searchContainer.setOnClickListener(v -> {
            startActivity(LoginHomeActivity.class);
        });

        //设置指示器切换监听器
        binding.content.indicator.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                binding.content.list.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        binding.content.list.addOnPageChangeListener(new OnPageChangeListenerAdapter() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.content.indicator.setCurrentTab(position);
            }
        });

        binding.content.searchContainer.setOnClickListener(v -> startActivity(SearchActivity.class));
        binding.messageContainer.setOnClickListener(v -> loginAfter(() -> startActivity(ConversationActivity.class)));
        binding.scan.setOnClickListener(v -> startActivity(ScanActivity.class));
        binding.friendContainer.setOnClickListener(v -> UserActivity.start(getHostActivity(), Constant.STYLE_FRIEND));
        binding.fansContainer.setOnClickListener(v -> UserActivity.start(getHostActivity(), Constant.STYLE_FANS));
        binding.myCode.setOnClickListener(v -> loginAfter(() -> startActivityExtraId(CodeActivity.class, sp.getUserId())));

        binding.mall.setOnClickListener(v -> startActivity(ProductActivity.class));
        binding.myOrder.setOnClickListener(v -> loginAfter(() -> startActivity(OrderActivity.class)));
        binding.shopCart.setOnClickListener(v -> loginAfter(() -> startActivity(ShopCartActivity.class)));

        binding.receivingAddress.setOnClickListener(v -> loginAfter(() -> {
            closeDrawer();
            startActivity(AddressActivity.class);
        }));

        //设置
        binding.setting.setOnClickListener(v -> startActivity(SettingActivity.class));
        binding.about.setOnClickListener(v -> startActivity(AboutActivity.class));
        binding.aboutCode.setOnClickListener(v -> startActivity(AboutCodeActivity.class));

        //关闭应用点击
        binding.closeApp.setOnClickListener(v -> SuperProcessUtil.killApp());

        //退出登录点击
        binding.primary.setOnClickListener(v -> {
            //弹出确认对话框
            //防止用户误操作
            //同时我们本质是想留住用户
            SuperDialog.newInstance(getSupportFragmentManager())
                    .setTitleRes(R.string.confirm_logout)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppContext.getInstance().logout();
                            showNotLogin();
                            closeDrawer();
                        }
                    }).show();
        });
    }

    private void closeDrawer() {
        binding.drawer.closeDrawer(GravityCompat.START);
    }

    /**
     * 动态添加快捷方式
     * <p>
     * 之所以要动态添加的目的是，我们希望用户点击后，启动 启动界面，然后再执行相应的操作
     * 之所以要启动 启动界面目的是，有些操作需要在这里初始化，当然也可以在AppContext，还有就是显示广告
     * https://developer.android.google.cn/guide/topics/ui/shortcuts/creating-shortcuts#java
     */
    private void initShortcut() {
        //扫一扫
        SuperShortcutUtil.addShortcut(
                getHostActivity(),
                R.string.scan,
                R.drawable.scan_primary,
                Constant.ACTION_SCAN,
                Constant.VALUE10 + 1
        );

        //搜索
        SuperShortcutUtil.addShortcut(
                getHostActivity(),
                R.string.search,
                R.drawable.search_primary,
                Constant.ACTION_SEARCH,
                Constant.VALUE10
        );

    }

    /**
     * 关闭界面时调用
     */
    @Override
    public void onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            //如果侧滑打开了，返回关闭侧滑，不关闭应用
            closeDrawer();
            return;
        }
        super.onBackPressed();
    }

    public void showMessageUnreadCount(Integer unReadCount) {
        if (unReadCount > 0) {
            String messageCount = StringUtil.formatMessageCount(unReadCount);

            binding.content.indicator.showMsg(0, unReadCount);
            binding.content.leftButton.showTextBadge(messageCount);
            binding.message.showTextBadge(messageCount);
        } else {
            binding.content.indicator.hideMsg(0);
            binding.content.leftButton.hiddenBadge();
            binding.message.hiddenBadge();
        }
    }


    public void processAdClick(Ad data) {
        Timber.d("processAdClick %s", data.getTitle());
//        Timber.d(new RuntimeException("出错了"));
        if (data.getUri().startsWith("http")) {
            WebActivity.start(getHostActivity(), data.getUri());
        } else {
            try {
                //应用
                Intent intent = new Intent(Intent.ACTION_VIEW);

                Uri uri = Uri.parse(data.getUri());

                //例如：打开我们在腾讯课程上的仿微信项目课程详情页面
//            Uri uri = Uri.parse("tencentedu://openpage/coursedetail?courseid=4875119&termid=103425768&taid=11008662607906617&fromWeb=1&sessionPath=165013705913519225082472#");
//            Uri uri = Uri.parse("http://www.ixuea.com/");
                intent.setData(uri);

                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                //没有安装对应的应用

                //解决方法是用浏览器打开网页界面
                SuperToast.show(R.string.not_found_activity);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        showUserInfo();
    }

    private void showUserInfo() {
        if (sp.isLogin()) {
            //已经登录了

            //获取用户信息
            loadUserData();

            SuperViewUtil.show(binding.primary);
        } else {
            showNotLogin();
        }
    }

    private void loadUserData() {
        //对于普通网络请求，可以在这里，或者框架内部先判断网络有效，才进行网络请求
        //也可以直接请求网络，因为网络请求部分也实现了网络错误处理
//        if (SuperNetworkUtil.isNetworkConnected(getHostActivity())) {
        DefaultRepository.getInstance()
                .userDetail(sp.getUserId())
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<User>>() {
                    @Override
                    public void onSucceeded(DetailResponse<User> data) {
                        showData(data.getData());
                    }

                    @Override
                    public boolean onFailed(DetailResponse<User> data, Throwable e) {
                        return true;
                    }
                });
//        }
    }

    private void showData(User data) {
        //显示头像
        ImageUtil.showAvatar(getHostActivity(), binding.avatar, data.getIcon());

        //显示昵称
        binding.nickname.setText(data.getNickname());
    }

    /**
     * 显示未登录状态
     */
    private void showNotLogin() {
        binding.nickname.setText(R.string.login_now);
        binding.avatar.setImageResource(R.drawable.default_avatar);
        SuperViewUtil.gone(binding.primary);
    }

    /**
     * 消息未读数改变了通知
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageUnreadCountChangedEvent(MessageUnreadCountChangedEvent event) {
        showMessageUnreadCount();
    }


    /**
     * 显示未读消息数
     */
    private void showMessageUnreadCount() {
        RongIMClient.getInstance().getTotalUnreadCount(new RongIMClient.ResultCallback<Integer>() {

            /**
             * 成功回调
             * @param unReadCount 未读数
             */
            @Override
            public void onSuccess(Integer unReadCount) {
                showMessageUnreadCount(unReadCount);
            }

            /**
             * 错误回调
             *
             * @param errorCode 错误码
             */
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    @Override
    protected String pageId() {
        return "Main";
    }
}