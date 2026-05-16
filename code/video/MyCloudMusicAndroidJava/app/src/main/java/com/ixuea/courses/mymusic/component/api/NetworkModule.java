package com.ixuea.courses.mymusic.component.api;

import com.chuckerteam.chucker.api.ChuckerInterceptor;
import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.config.Config;
import com.ixuea.courses.mymusic.util.JSONUtil;
import com.ixuea.courses.mymusic.util.PreferenceUtil;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络相关依赖提供类
 * <p>
 * 例如：OkHttp，retrofit依赖
 */
@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    /**
     * 提供OkHttpClient
     */
    @Provides
    @Singleton
    public static OkHttpClient provideOkHttpClient() {
        //初始化okhttp
        OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder();

        //配置缓存
        Cache cache = new Cache(AppContext.getInstance().getCacheDir(), Config.NETWORK_CACHE_SIZE);
        okhttpClientBuilder.cache(cache);

        okhttpClientBuilder.connectTimeout(10, TimeUnit.SECONDS) //连接超时时间
                .writeTimeout(10, TimeUnit.SECONDS) //写，也就是将数据发送到服务端超时时间
                .readTimeout(10, TimeUnit.SECONDS); //读，将服务端的数据下载到本地

        //网络签名加密插件
        okhttpClientBuilder.addInterceptor(new NetworkSecurityInterceptor());

        //公共请求参数
        okhttpClientBuilder.addNetworkInterceptor(chain -> {
            //获取到偏好设置工具类
            PreferenceUtil sp = PreferenceUtil.getInstance(AppContext.getInstance());

            //获取到request
            Request request = chain.request();

            if (sp.isLogin()) {
                //登录了

                //获取token
                String session = sp.getSession();

                request = request.newBuilder()
                        .addHeader("Authorization", session).build();
            }

            //继续执行网络请求
            return chain.proceed(request);
        });

        if (Config.DEBUG) {
            //调试模式

            //创建okhttp日志拦截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

            //设置日志等级
            loggingInterceptor.level(HttpLoggingInterceptor.Level.BASIC);

            //添加到网络框架中
            okhttpClientBuilder.addInterceptor(loggingInterceptor);

            //添加chucker实现应用内显示网络请求信息拦截器
            okhttpClientBuilder.addInterceptor(new ChuckerInterceptor.Builder(AppContext.getInstance()).build());
        }

        return okhttpClientBuilder.build();
    }

    /**
     * 提供Retrofit实例
     *
     * @param okHttpClient
     * @return
     */
    @Provides
    @Singleton
    public static Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()

                //让retrofit使用okhttp
                .client(okHttpClient)

                //api地址
                .baseUrl(Config.ENDPOINT)

                //适配rxjava
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())

                //使用gson解析json
                //包括请求参数和响应
                .addConverterFactory(GsonConverterFactory.create(JSONUtil.createGson()))

                //创建retrofit
                .build();
    }
}
