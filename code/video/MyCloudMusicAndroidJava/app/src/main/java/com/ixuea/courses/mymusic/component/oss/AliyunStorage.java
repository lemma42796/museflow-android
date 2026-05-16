package com.ixuea.courses.mymusic.component.oss;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.ixuea.courses.mymusic.config.Config;

/**
 * 阿里云OSS
 */
public class AliyunStorage {

    private static AliyunStorage instance;
    private final Context context;
    private final ClientConfiguration config;
    private final OSSClient ossClient;

    public AliyunStorage(Context context) {
        this.context = context;

        //推荐使用OSSAuthCredentialsProvider
        //因为他有token过期可以及时更新

        //请勿泄漏该key
        //和非法使用
        @SuppressWarnings("deprecation")
        OSSPlainTextAKSKCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(
                Config.ALIYUN_AK,
                Config.ALIYUN_SK
        );

        //该配置类如果不设置
        //会有默认配置
        //具体可看该类
        config = new ClientConfiguration();

        //连接超时，默认15秒
        config.setConnectionTimeout(15 * 1000);

        //socket超时，默认15秒
        config.setSocketTimeout(15 * 1000);

        //最大并发请求数，默认5个
        config.setMaxConcurrentRequest(5);

        //失败后最大重试次数，默认2次
        config.setMaxErrorRetry(2);

        if (Config.DEBUG) {
            //这个开启会支持写入手机sd卡中的一份日志文件位置在SDCard_path\OSSLog\logs.csv
            OSSLog.enableLog();
        }

        //初始化oss客户端
        ossClient = new OSSClient(
                context, Config.BUCKET_ENDPOINT,
                credentialProvider
        );
    }

    public synchronized static AliyunStorage getInstance(Context context) {
        if (instance == null) {
            instance = new AliyunStorage(context.getApplicationContext());
        }
        return instance;
    }

    public OSSClient getOssClient() {
        return ossClient;
    }
}
