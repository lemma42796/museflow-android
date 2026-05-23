package com.ixuea.courses.mymusic.component.api

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.ixuea.courses.mymusic.AppContext
import com.ixuea.courses.mymusic.config.Config
import com.ixuea.courses.mymusic.util.JSONUtil
import com.ixuea.courses.mymusic.util.PreferenceUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 网络相关依赖提供类，例如 OkHttp、Retrofit。
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    /**
     * 提供 OkHttpClient。
     */
    @Provides
    @Singleton
    @JvmStatic
    fun provideOkHttpClient(): OkHttpClient {
        val appContext = AppContext.getInstance()
        val okhttpClientBuilder = OkHttpClient.Builder()
        val cache = Cache(appContext.cacheDir, Config.NETWORK_CACHE_SIZE)
        okhttpClientBuilder.cache(cache)

        okhttpClientBuilder
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)

        okhttpClientBuilder.addInterceptor(NetworkSecurityInterceptor())

        okhttpClientBuilder.addNetworkInterceptor { chain ->
            val sp = PreferenceUtil.getInstance(appContext)
            var request = chain.request()

            if (sp.isLogin) {
                request = request.newBuilder()
                    .addHeader("Authorization", sp.session.orEmpty())
                    .build()
            }

            chain.proceed(request)
        }

        if (Config.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            okhttpClientBuilder.addInterceptor(loggingInterceptor)
            okhttpClientBuilder.addInterceptor(ChuckerInterceptor.Builder(appContext).build())
        }

        return okhttpClientBuilder.build()
    }

    /**
     * 提供 Retrofit 实例。
     */
    @Provides
    @Singleton
    @JvmStatic
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Config.ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create(JSONUtil.createGson()))
            .build()
    }
}
