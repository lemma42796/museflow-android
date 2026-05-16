package com.ixuea.courses.mymusic.util;

import android.provider.MediaStore;

/**
 * 常量类
 */
public class Constant {
    public static final String ACTION_LOGIN = "ACTION_LOGIN";
    public static final String ID = "id";

    /**
     * 类型常量
     * <p>
     * 主要用来显示列表时区分不同的类型
     */
    public static final int STYLE_BANNER = 0;
    public static final int STYLE_BUTTON = 1;
    public static final int STYLE_SHEET = 2;
    public static final int STYLE_SONG = 3;
    public static final int STYLE_TITLE = 4;
    public static final int STYLE_FOOTER = 5;
    public static final int STYLE_CATEGORY = 6;
    public static final int STYLE_VIDEO = 7;
    public static final int STYLE_COMMENT = 8;
    public static final int STYLE_USER = 9;
    public static final int STYLE_CONFIRM_ORDER = 10;
    public static final int STYLE_ORDER = 11;
    public static final int STYLE_FRIEND = 12;
    public static final int STYLE_FANS = 13;
    public static final int STYLE_FRIEND_SELECT = 14;

    public static final int VALUE_NO = -1;
    public static final int VALUE0 = 0;
    public static final int VALUE10 = 10;
    public static final int VALUE20 = 20;
    public static final int VALUE30 = 30;

    public static final int SIZE12 = 12;
    public static final int SIZE10 = 10;

    public static final String TITLE_KEY = "title";
    public static final String URL = "url";
    public static final String DATA = "data";
    public static final String STYLE = "style";

    public static final int STYLE_PHONE_LOGIN = VALUE0;
    public static final int STYLE_FORGOT_PASSWORD = VALUE10;

    public static final String ANONYMOUS = "anonymous";

    /**
     * 用户详情昵称查询字段
     */
    public static final String NICKNAME = "nickname";
    public static final String AD = "ad";
    public static final String PUSH = "push";

    /**
     * 广告点击了
     */
    public static final String ACTION_AD = "com.ixuea.courses.mymusic.ACTION_AD";

    /**
     * 打开音乐播放界面
     */
    public static final String ACTION_MUSIC_PLAYER_PAGE = "com.ixuea.courses.mymusic.ACTION_MUSIC_PLAYER_PAGE";


    public static final String ACTION_UNLOCK_LYRIC = "com.ixuea.courses.mymusic.ACTION_UNLOCK_LYRIC";

    /**
     * 歌词操作
     */
    public static final String ACTION_LYRIC = "com.ixuea.courses.mymusic.ACTION_LYRIC";
    public static final String ACTION_PLAY = "com.ixuea.courses.mymusic.ACTION_PLAY";
    public static final String ACTION_PREVIOUS = "com.ixuea.courses.mymusic.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.ixuea.courses.mymusic.ACTION_NEXT";
    public static final String ACTION_SCAN = "com.ixuea.courses.mymusic.ACTION_SCAN";
    public static final String ACTION_SEARCH = "com.ixuea.courses.mymusic.ACTION_SEARCH";
    public static final String ACTION_CHAT = "com.ixuea.courses.mymusic.ACTION_CHAT";
    public static final String ACTION_PUSH = "com.ixuea.courses.mymusic.ACTION_PUSH";

    /**
     * 保持播放进度间隔（毫秒）
     */
    public static final long SAVE_PROGRESS_TIME = 2000;

    /**
     * LRC歌词
     */
    public static final int LRC = 0;

    /**
     * KSC歌词
     */
    public static final int KSC = 10;

    /**
     * 隐藏歌词拖拽时间
     */
    public static final long LYRIC_HIDE_DRAG_TIME = 4000;

    /**
     * Android媒体库本地音乐查询条件
     * 这里是查询是音乐
     * 并且大于1M
     * 时长大于60秒的文件
     */
    public static final String MEDIA_AUDIO_SELECTION =
            MediaStore.Audio.AudioColumns.IS_MUSIC + " != 0 AND " +
                    MediaStore.Audio.AudioColumns.SIZE + " >= ? AND " +
                    MediaStore.Audio.AudioColumns.DURATION + " >= ?";

    /**
     * 1M
     */
    public static final int MUSIC_FILTER_SIZE = 1 * 1024 * 1024;

    /**
     * 60s
     */
    public static final int MUSIC_FILTER_DURATION = 60 * 1000;

    /**
     * 扫描本地音乐放大镜圆周半径
     */
    public static final double DEFAULT_RADIUS_SCAN_LOCAL_MUSIC_ZOOM = 30;
    public static final String SHEET_ID = "sheet_id";
    public static final String PAGE = "page";
    public static final String USER_ID = "user_id";
    public static final String SEPARATOR = "，";

    /**
     * 搜索接口查询关键字
     */
    public static final String QUERY = "query";

    public static final int REQUEST_OVERLAY_PERMISSION = 100;

    /**
     * 解锁全局歌词Id
     */
    public static final int NOTIFICATION_UNLOCK_LYRIC_ID = 10001;

    /**
     * 每页返回数量
     * <p>
     * 对于我们API，大部分API默认为10
     */
    public static final int DEFAULT_SIZE = 10;

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE = 1;

    //region 平台
    /**
     * android
     */
    public static final int ANDROID = 0;

    /**
     * ios
     */
    public static final int IOS = 10;

    /**
     * web
     */
    public static final int WEB = 20;

    /**
     * wap
     */
    public static final int WAP = 30;
    //endregion

    //region 地图应用包名
    /**
     * 腾讯地图
     */
    public static final String PACKAGE_MAP_TENCENT = "com.tencent.map";

    /**
     * 百度地图
     */
    public static final String PACKAGE_MAP_BAIDU = "com.baidu.BaiduMap";

    /**
     * 高德地图
     */
    public static final String PACKAGE_MAP_AMAP = "com.autonavi.minimap";

    /**
     * 搜狗地图
     */
    public static final String PACKAGE_MAP_SOGOU = "com.tencent.map";
    //endregion

    //region 订单状态
    /**
     * 待支付
     */
    public static final int WAIT_PAY = 0;

    /**
     * 订单关闭
     */
    public static final int CLOSE = 10;

    /**
     * 待发货
     */
    public static final int WAIT_SHIPPED = 500;

    /**
     * 待收货
     */
    public static final int WAIT_RECEIVED = 510;

    /**
     * 待评价
     */
    public static final int WAIT_COMMENT = 520;

    /**
     * 完成
     */
    public static final int COMPLETE = 530;
    //endregion

    /**
     * 支付宝
     */
    public static final int ALIPAY = 10;

    /**
     * 微信
     */
    public static final int WECHAT = 20;

    /**
     * 花呗分期
     */
    public static final int HUABEI_STAGE = 30;

    /**
     * 左侧（其他人）文本消息
     */
    public static final int TEXT_LEFT = 100;

    /**
     * 右侧（我的）文本消息
     */
    public static final int TEXT_RIGHT = 110;

    /**
     * 左侧（其他人）图片消息
     */
    public static final int IMAGE_LEFT = 120;

    /**
     * 右侧（我的）图片消息
     */
    public static final int IMAGE_RIGHT = 130;

    /**
     * 聊天消息每页获取数量
     */
    public static final int DEFAULT_MESSAGE_COUNT = 10;

    /**
     * 加盐格式化字符串
     */
    public static final String SALT_START = "wt5j1URZ1H6RDtt";

    /**
     * 加盐格式化字符串
     */
    public static final String SALT_END = "uWg7x2E0Mr5Xwzm";

    /**
     * 请求/响应签名key
     */
    public static final String HEADER_SIGN = "Sign";
}

