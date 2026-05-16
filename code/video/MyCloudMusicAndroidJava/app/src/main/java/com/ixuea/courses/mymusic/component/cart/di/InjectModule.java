package com.ixuea.courses.mymusic.component.cart.di;

import com.ixuea.courses.mymusic.component.cart.api.ShopCartService;
import com.ixuea.courses.mymusic.component.cart.repository.ShopCartRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

/**
 * 提供依赖注入对象
 */
@Module
@InstallIn(SingletonComponent.class)
public class InjectModule {
    /**
     * 提供Repository
     *
     * @return
     */
    @Provides
    @Singleton
    public static ShopCartRepository provideShopCartRepository(ShopCartService service) {
        return new ShopCartRepository(service);
    }

    /**
     * 提供Service
     *
     * @return
     */
    @Provides
    @Singleton
    public static ShopCartService provideShopCartService(Retrofit retrofit) {
        return retrofit.create(ShopCartService.class);
    }
}
