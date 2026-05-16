package com.ixuea.courses.mymusic.config;

import com.ixuea.courses.mymusic.BuildConfig;

/**
 * 配置文件
 * <p>
 * 例如：API地址，QQ等第三方服务配置信息等
 */
public class Config {
    /**
     * 默认延时时间
     */
    public static final long SPLASH_DEFAULT_DELAY_TIME = 1000;

    /**
     * 是否是调试模式
     */
    public static final boolean DEBUG = BuildConfig.DEBUG;

    /**
     * 端点
     */
    public static String ENDPOINT = BuildConfig.ENDPOINT;

    /**
     * 资源端点
     */
    public static String RESOURCE_ENDPOINT = BuildConfig.RESOURCE_ENDPOINT;

    /**
     * 网络缓存目录大小
     * 100M
     */
    public static final long NETWORK_CACHE_SIZE = 1024 * 1024 * 100;

    //region 阿里云
    /**
     * 阿里云OSS AK
     */
    public static final String ALIYUN_AK = "";

    /**
     * 阿里云OSS SK
     */
    public static final String ALIYUN_SK = "";

    /**
     * 阿里云OSS Bucket
     */
    public static final String ALIYUN_OSS_BUCKET_NAME = "dev-courses-misuc";

    /**
     * 阿里云OSS Bucket 地址
     * https://help.aliyun.com/document_detail/31837.html
     */
    public static final String BUCKET_ENDPOINT = "oss-cn-beijing.aliyuncs.com";

    /**
     * 聊天key
     */
    public static final String IM_KEY = "";

    //endregion

    /**
     * 二维码地址
     * <p>
     * 真实项目中一般设置为应用的下载宣传界面，因为目前没有这样的界面，所以就设置为官网地址
     */
    public static final String QRCODE_URL = "http://www.ixuea.com";

    /**
     * 用户二维码
     */
    public static final String USER_QRCODE_URL = String.format("%s?u=%%s", QRCODE_URL);

}
