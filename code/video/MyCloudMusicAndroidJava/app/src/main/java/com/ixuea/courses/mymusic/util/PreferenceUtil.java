package com.ixuea.courses.mymusic.util;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ixuea.courses.mymusic.component.ad.model.Ad;
import com.ixuea.superui.util.DensityUtil;
import com.tencent.mmkv.MMKV;

import org.apache.commons.lang3.StringUtils;

/**
 * 偏好设置工具类
 */
public class PreferenceUtil {
    /**
     * 是否显示引导界面key
     */
    private static final String SHOW_GUIDE = "SHOW_GUIDE";
    private static final String SESSION = "session";
    private static final String USER_ID = "user_id";
    private static final String USER_CHAT_TOKEN = "user_chat_token";
    private static final String SPLASH_AD = "SPLASH_AD";
    private static final String KEY_LOCAL_MUSIC_SORT = "LOCAL_MUSIC_SORT";
    /**
     * 最后播放音乐id key
     */
    private static final String LAST_PLAY_SONG_ID = "LAST_PLAY_SONG_ID";

    /**
     * 全局歌词颜色 key
     */
    private static final String KEY_GLOBAL_LYRIC_TEXT_COLOR = "GLOBAL_LYRIC_TEXT_COLOR";

    /**
     * 全局歌词大小 key
     */
    private static final String KEY_GLOBAL_LYRIC_TEXT_SIZE = "GLOBAL_LYRIC_TEXT_SIZE";

    /**
     * 全局歌词y坐标 key
     */
    private static final String KEY_GLOBAL_LYRIC_VIEW_Y = "GLOBAL_LYRIC_VIEW_Y";

    /**
     * 是否显示全局歌词 key
     */
    private static final String KEY_SHOW_GLOBAL_LYRIC = "SHOW_GLOBAL_LYRIC";

    /**
     * 全局歌词锁定 key
     */
    private static final String KEY_GLOBAL_LYRIC_LOCK = "GLOBAL_LYRIC_LOCK";

    private static PreferenceUtil instance;
    private final Context context;
    private final MMKV preference;

    /**
     * 构造方法
     *
     * @param context
     */
    public PreferenceUtil(Context context) {
        //保存上下文
        this.context = context.getApplicationContext();

        //初始化腾讯开源的高性能key,value存储，可以写到单独的类中
        preference = MMKV.defaultMMKV();
    }

    /**
     * 获取偏好设置单例
     *
     * @param context
     * @return
     */
    public synchronized static PreferenceUtil getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceUtil(context);
        }
        return instance;
    }

    /**
     * 是否显示引导界面
     *
     * @return
     */
    public boolean isShowGuide() {
        return getBoolean(SHOW_GUIDE, true);
    }

    /**
     * 设置是否显示引导界面
     *
     * @param value
     */
    public void setShowGuide(boolean value) {
        putBoolean(SHOW_GUIDE, value);
    }

    /**
     * 获取排序
     *
     * @param style
     * @return
     */
    public int getSort(int style) {
        return getInt(getSortKey(style), style);
    }

    @NonNull
    private String getSortKey(int style) {
        return String.format("sort_%d", style);
    }

    public void setSort(int style, int data) {
        putInt(getSortKey(style), data);
    }

    /**
     * 获取登录session
     *
     * @return
     */
    public String getSession() {
        return preference.getString(SESSION, null);
    }

    /**
     * 保存登录session
     *
     * @param value
     */
    public void setSession(String value) {
        putString(SESSION, value);
    }

    /**
     * 获取用户Id
     *
     * @return
     */
    public String getUserId() {
        return preference.getString(USER_ID, Constant.ANONYMOUS);
    }

    /**
     * 设置用户Id
     *
     * @param value
     */
    public void setUserId(String value) {
        putString(USER_ID, value);
    }

    /**
     * 获取聊天token
     *
     * @return
     */
    public String getChatToken() {
        return preference.getString(USER_CHAT_TOKEN, null);
    }

    /**
     * 设置聊天token，可以加密后在保存
     *
     * @param value
     */
    public void setChatToken(String value) {
        putString(USER_CHAT_TOKEN, value);
    }

    /**
     * 是否登录了
     *
     * @return
     */
    public boolean isLogin() {
        return !Constant.ANONYMOUS.equals(getUserId());
    }

    public void logout() {
        delete(USER_ID);
        delete(SESSION);
        delete(USER_CHAT_TOKEN);
    }

    /**
     * 获取启动界面广告
     *
     * @return
     */
    public Ad getSplashAd() {
        String result = preference.getString(SPLASH_AD, null);
        if (StringUtils.isBlank(result)) {
            return null;
        }

        return JSONUtil.fromJSON(result, Ad.class);
    }

    /**
     * 设置启动界面广告
     *
     * @param data 如果为空，就是删除本地广告
     */
    public void setSplashAd(Ad data) {
        if (null == data) {
            delete(SPLASH_AD);
        } else {
            putString(SPLASH_AD, JSONUtil.toJSON(data));
        }
    }

    /**
     * 获取最后播放的音乐Id
     *
     * @return
     */
    public String getLastPlaySongId() {
        return preference.getString(getLastPlaySongIdKey(), null);
    }

    /**
     * 设置当前播放音乐的id
     */
    public void setLastPlaySongId(String data) {
        preference.edit().putString(getLastPlaySongIdKey(), data).apply();
    }

    private String getLastPlaySongIdKey() {
        return String.format("%s%s", getUserId(), LAST_PLAY_SONG_ID);
    }

    /**
     * 本地音乐排序
     *
     * @return
     */
    public int getLocalMusicSortIndex() {
        return getInt(KEY_LOCAL_MUSIC_SORT, 0);
    }

    /**
     * 设置本地音乐排序
     *
     * @param data
     */
    public void setLocalMusicSortIndex(int data) {
        putInt(KEY_LOCAL_MUSIC_SORT, data);
    }

    /**
     * 获取全局歌词颜色索引
     *
     * @return
     */
    public int getGlobalLyricTextColorIndex() {
        return getInt(KEY_GLOBAL_LYRIC_TEXT_COLOR, 0);
    }

    /**
     * 设置全局歌词颜色索引
     */
    public void setGlobalLyricTextColorIndex(int index) {
        putInt(KEY_GLOBAL_LYRIC_TEXT_COLOR, index);
    }

    /**
     * 获取全局歌词大小
     * 默认18sp
     *
     * @return
     */
    public int getGlobalLyricTextSize() {
        return getInt(KEY_GLOBAL_LYRIC_TEXT_SIZE, (int) DensityUtil.dip2px(context, 18));
    }

    /**
     * 设置全局歌词大小
     *
     * @param data
     */
    public void setGlobalLyricTextSize(int data) {
        putInt(KEY_GLOBAL_LYRIC_TEXT_SIZE, data);
    }

    /**
     * 获取全局歌词y坐标
     *
     * @return
     */
    public int getGlobalLyricViewY() {
        return getInt(KEY_GLOBAL_LYRIC_VIEW_Y, 0);
    }

    /**
     * 设置全局歌词y坐标
     *
     * @param data
     */
    public void setGlobalLyricViewY(int data) {
        putInt(KEY_GLOBAL_LYRIC_VIEW_Y, data);
    }

    /**
     * 是否显示全局歌词
     *
     * @return
     */
    public boolean isShowGlobalLyric() {
        return getBoolean(KEY_SHOW_GLOBAL_LYRIC, false);
    }

    /**
     * 设置是否显示全局歌词
     *
     * @param data
     */
    public void setShowGlobalLyric(boolean data) {
        putBoolean(KEY_SHOW_GLOBAL_LYRIC, data);
    }

    /**
     * 获取全局歌词是否锁定
     *
     * @return
     */
    public boolean isGlobalLyricLock() {
        return getBoolean(KEY_GLOBAL_LYRIC_LOCK, false);
    }

    /**
     * 设置全局歌词是否锁定
     */
    public void setGlobalLyricLock(boolean data) {
        putBoolean(KEY_GLOBAL_LYRIC_LOCK, data);
    }

    //region 辅助方法

    /**
     * 获取boolean
     *
     * @param key
     * @param defaultValue
     * @return
     */
    private boolean getBoolean(String key, boolean defaultValue) {
        return preference.getBoolean(key, defaultValue);
    }

    /**
     * 保存boolean
     *
     * @param key
     * @param value
     */
    private void putBoolean(String key, boolean value) {
        preference.edit().putBoolean(key, value).apply();
    }

    private int getInt(String key, int defaultValue) {
        return preference.getInt(key, defaultValue);
    }

    private void putInt(String key, int data) {
        preference.edit().putInt(key, data).apply();
    }

    private void putString(String key, String value) {
        preference.edit().putString(key, value).apply();
    }

    private void delete(String data) {
        preference.edit().remove(data).commit();
    }
    //endregion
}
