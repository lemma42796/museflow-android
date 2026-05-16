package com.ixuea.courses.mymusic;

import static com.ixuea.android.downloader.DownloadService.downloadManager;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;

import com.amap.api.maps.MapsInitializer;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.ixuea.android.downloader.DownloadService;
import com.ixuea.android.downloader.callback.DownloadManager;
import com.ixuea.courses.mymusic.component.chat.model.event.MessageUnreadCountChangedEvent;
import com.ixuea.courses.mymusic.component.conversation.model.event.NewMessageEvent;
import com.ixuea.courses.mymusic.component.login.model.Session;
import com.ixuea.courses.mymusic.component.login.model.event.LoginStatusChangedEvent;
import com.ixuea.courses.mymusic.config.Config;
import com.ixuea.courses.mymusic.manager.MyActivityManager;
import com.ixuea.courses.mymusic.manager.impl.MusicListManagerImpl;
import com.ixuea.courses.mymusic.util.LiteORMUtil;
import com.ixuea.courses.mymusic.util.MessageUtil;
import com.ixuea.courses.mymusic.util.NotificationUtil;
import com.ixuea.courses.mymusic.util.PreferenceUtil;
import com.ixuea.superui.toast.SuperToast;
import com.ixuea.superui.util.SuperThreadUtil;
import com.mob.MobSDK;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;

import cn.jiguang.analytics.android.api.JAnalyticsInterface;
import cn.jiguang.api.utils.JCollectionAuth;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import dagger.hilt.android.HiltAndroidApp;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.listener.OnReceiveMessageWrapperListener;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.ReceivedProfile;
import io.rong.push.RongPushClient;
import io.rong.push.pushconfig.PushConfig;
import timber.log.Timber;

/**
 * 全局Application
 * <p>
 * HiltAndroidApp:依赖注入配置
 */
@HiltAndroidApp
public class AppContext extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "AppContext";
    private static AppContext instance;
    private MyActivityManager myActivityManager;
    private PreferenceUtil sp;
    private boolean isInit = false;
    private boolean isOCRInit;
    private RongIMClient chatClient;
    private IWXAPI wxapi;

    public static AppContext getInstance() {
        return instance;
    }

    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            final Message message = (Message) msg.obj;
            NotificationUtil.showMessage(MessageUtil.getContent(message.getContent()), message.getTargetId());

        }
    };

    /**
     * 初始化 腾讯开源的高性能keyValue存储，用来替代系统的SharedPreferences
     */
    private void initMMKV() {
        String rootDir = MMKV.initialize(this);
        Log.d(TAG, "initMMKV: " + rootDir);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if (SuperThreadUtil.isMainThread(getApplicationContext())) {
            //一些服务会（例如：小米推送，当前自己的服务可以配置运行到其他进程）运行在另外一个进程，所以会导致这个方法执行多次
            //所以要判断，因为以下这些服务，只在主进程初始化
            initLog();

            initWechat();

            initIM();

            initMMKV();

            sp = PreferenceUtil.getInstance(getApplicationContext());

            //初始化emoji
            EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
            EmojiCompat.init(config);

            //注册界面声明周期监听
            registerActivityLifecycleCallbacks(this);
            myActivityManager = MyActivityManager.getInstance();

            //初始化toast工具类
            SuperToast.init(getApplicationContext());
            if (sp.isLogin()) {
                //用户登录了，启动应用时，要初始化登录才能初始化的逻辑
                onLogin(null);
            }
        }
    }

    /**
     * 初始化微信
     */
    private void initWechat() {
        wxapi = WXAPIFactory.createWXAPI(getApplicationContext(), null);
        wxapi.registerApp(Config.WECHAT_AK);
    }

    public IWXAPI getWxapi() {
        return wxapi;
    }

    public RongIMClient getChatClient() {
        return chatClient;
    }

    /**
     * 初始化聊天服务，整个应用生命周期间，只需要初始化一次
     */
    private void initIM() {
        //设置推送
        //https://doc.rongcloud.cn/im/Android/5.X/noui/push/mi
        PushConfig config = new PushConfig.Builder()
                //开启小米推送
                .enableMiPush(Config.MI_ID, Config.MI_KEY)
                .build();

        RongPushClient.setPushConfig(config);

        //初始化聊天sdk
        RongIMClient.init(getApplicationContext(), Config.IM_KEY, true);

        //设置消息监听
        chatClient = RongIMClient.getInstance();
        chatClient.addOnReceiveMessageListener(new OnReceiveMessageWrapperListener() {
            @Override
            public void onReceivedMessage(Message message, ReceivedProfile profile) {
                //该方法的调用不再主线程
                Timber.e("chat onReceived %s", message);

                if (EventBus.getDefault().hasSubscriberForEvent(NewMessageEvent.class)) {
                    //如果有监听该事件，表示在聊天界面，或者会话界面
                    EventBus.getDefault().post(new NewMessageEvent(message));
                } else {
                    handler.obtainMessage(0, message).sendToTarget();
                }

                //发送消息未读数改变了通知
                EventBus.getDefault().post(new MessageUnreadCountChangedEvent());
            }
        });
//        chatClient.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageWrapperListener() {
//            /**
//             * 接收实时或者离线消息。
//             * 注意:
//             * 1. 针对接收离线消息时，服务端会将 200 条消息打成一个包发到客户端，客户端对这包数据进行解析。
//             * 2. hasPackage 标识是否还有剩余的消息包，left 标识这包消息解析完逐条抛送给 App 层后，剩余多少条。
//             * 如何判断离线消息收完：
//             * 1. hasPackage 和 left 都为 0；
//             * 2. hasPackage 为 0 标识当前正在接收最后一包（200条）消息，left 为 0 标识最后一包的最后一条消息也已接收完毕。
//             *
//             * @param message    接收到的消息对象
//             * @param left       每个数据包数据逐条上抛后，还剩余的条数
//             * @param hasPackage 是否在服务端还存在未下发的消息包
//             * @param offline    消息是否离线消息
//             * @return 是否处理消息。 如果 App 处理了此消息，返回 true; 否则返回 false 由 SDK 处理。
//             */
//            @Override
//            public boolean onReceived(final Message message, final int left, boolean hasPackage, boolean offline) {
//                //该方法的调用不再主线程
//                Timber.e("chat onReceived %s", message);
//
//                return true;
//            }
//        });
    }

    /**
     * 连接聊天服务器
     *
     * @param data
     */
    private void connectChat(Session data) {
        RongIMClient.connect(data.getChatToken(), new RongIMClient.ConnectCallback() {
            /**
             * 成功回调
             * @param userId 当前用户 ID
             */
            @Override
            public void onSuccess(String userId) {
                Timber.d("connect chat success %s", userId);
            }

            /**
             * 错误回调
             * @param errorCode 错误码
             */
            @Override
            public void onError(RongIMClient.ConnectionErrorCode errorCode) {
                Timber.e("connect chat error %s", errorCode);

                if (errorCode.equals(RongIMClient.ConnectionErrorCode.RC_CONN_TOKEN_INCORRECT)) {
                    //从 APP 服务获取新 token，并重连
                } else {
                    //无法连接 IM 服务器，请根据相应的错误码作出对应处理
                }

                //因为我们这个应用，不是类似微信那样纯聊天应用，所以聊天服务器连接失败，也让进入应用
                //真实项目中按照需求实现就行了
                SuperToast.show(R.string.error_message_login);
            }

            /**
             * 数据库回调.
             * @param databaseOpenStatus 数据库打开状态. DATABASE_OPEN_SUCCESS 数据库打开成功; DATABASE_OPEN_ERROR 数据库打开失败
             */
            @Override
            public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {

            }
        });

    }


    private void initLog() {
        if (Config.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            //可以上报到任何地方，真实项目大部分都会用第三方日志服务
            //例如：阿里云日志服务，或者自己公司有；打印到文件那种现在用的太少了，所以也就不实现了
        }
    }

    /**
     * 初始化ocr服务
     */
    public void initOCR() {
        if (isOCRInit) {
            return;
        }

        OCR.getInstance(getApplicationContext()).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {

            }

            @Override
            public void onError(OCRError ocrError) {

            }
        }, getApplicationContext());

        isOCRInit = true;
    }

    public void logout() {
        logoutSilence();
    }

    private void logoutSilence() {
        //清除登录相关信息
        PreferenceUtil.getInstance(getApplicationContext()).logout();

        //清除第三方登录信息
        otherLogout(Wechat.NAME);
        otherLogout(QQ.NAME);

        //退出聊天服务器
        RongIMClient.getInstance().logout();

        loginStatusChanged();
    }

    /**
     * 第三方平台退出
     *
     * @param data
     */
    private void otherLogout(String data) {
        //清除第三方平台登录信息
        Platform platform = ShareSDK.getPlatform(data);
        if (platform.isAuthValid()) {
            platform.removeAccount(true);
        }
    }

    public void onLogin(Session data) {
        if (data == null) {
            data = new Session(sp.getUserId(), sp.getSession(), sp.getChatToken());
        }

        connectChat(data);

        loginStatusChanged();
    }

    private void loginStatusChanged() {
        //销毁一些实例，用的实例在获取，这样获取的用户id就是新的用户
        MusicListManagerImpl.destroy();
        LiteORMUtil.destroy();

        if (downloadManager != null) {
            downloadManager.destroy();
            downloadManager = null;
        }

        EventBus.getDefault().post(new LoginStatusChangedEvent(false));
    }

    /**
     * 获取下载管理器
     *
     * @return
     */
    public DownloadManager getDownloadManager() {
        if (downloadManager == null) {
            PreferenceUtil sp = PreferenceUtil.getInstance(getApplicationContext());

            //创建下载框架配置
            com.ixuea.android.downloader.config.Config config = new com.ixuea.android.downloader.config.Config();

            //数据库名称添加了用户id
            //所以不同的用户数据是隔离的
            config.setDatabaseName(String.format("download_info_%s.db", sp.getUserId()));

            //获取下载管理器
            downloadManager = DownloadService.getDownloadManager(getApplicationContext(), config);
        }
        return downloadManager;
    }

    public LiteORMUtil getOrm() {
        return LiteORMUtil.getInstance(getApplicationContext());
    }

    public PreferenceUtil getPreference() {
        return sp;
    }

    public void onInit() {
        if (!isInit) {
            /**
             * 更新隐私合规状态,需要在初始化地图之前完成
             * @param  context: 上下文
             * @param  isContains: 隐私权政策是否包含高德开平隐私权政策  true是包含
             * @param  isShow: 隐私权政策是否弹窗展示告知用户 true是展示
             * @since 8.1.0
             *
             * https://lbs.amap.com/api/android-sdk/guide/create-project/dev-attention#t2
             */
            MapsInitializer.updatePrivacyShow(getApplicationContext(), true, true);
            MapsInitializer.updatePrivacyAgree(getApplicationContext(), true);

            MobSDK.submitPolicyGrantResult(true, null);

            //bugly
            //允许显示升级的界面
            Beta.canShowUpgradeActs.add(MainActivity.class);

            /**
             *  设置自定义升级对话框UI布局
             *  注意：因为要保持接口统一，需要用户在指定控件按照以下方式设置tag，否则会影响您的正常使用：
             *  标题：beta_title，如：android:tag="beta_title"
             *  升级信息：beta_upgrade_info  如： android:tag="beta_upgrade_info"
             *  更新属性：beta_upgrade_feature 如： android:tag="beta_upgrade_feature"
             *  取消按钮：beta_cancel_button 如：android:tag="beta_cancel_button"
             *  确定按钮：beta_confirm_button 如：android:tag="beta_confirm_button"
             *  详见layout/upgrade_dialog.xml
             */
            Beta.upgradeDialogLayoutId = R.layout.activity_upgrade;

            Bugly.init(getApplicationContext(), Config.BUGLY_APP_KEY, Config.DEBUG);

            //极光服务，同意用户隐私协议
            //https://docs.jiguang.cn/janalytics/guideline/jghgzy
            JCollectionAuth.setAuth(getApplicationContext(), true);

            //初始化极光统计
            JAnalyticsInterface.init(getApplicationContext());
            JAnalyticsInterface.setDebugMode(Config.DEBUG);

            //极光推送
            JPushInterface.setDebugMode(Config.DEBUG);
            JPushInterface.init(getApplicationContext());

            isInit = true;
        }
    }

    //region activity生命周期监听
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        myActivityManager.add(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        myActivityManager.remove(activity);
    }

    //endregion
}
