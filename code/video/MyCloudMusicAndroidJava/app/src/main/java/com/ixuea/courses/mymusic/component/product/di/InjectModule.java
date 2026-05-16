package com.ixuea.courses.mymusic.component.product.di;

import com.ixuea.courses.mymusic.component.product.api.ProductService;
import com.ixuea.courses.mymusic.component.product.repository.ProductRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

/**
 * 提供依赖注入对象
 * <p>
 * 例如商品相关Repository
 */
@Module
@InstallIn(SingletonComponent.class)
public class InjectModule {
    /**
     * 提供ProductService
     *
     * @return
     */
    @Provides
    @Singleton
    public static ProductService provideProductService(Retrofit retrofit) {
        return retrofit.create(ProductService.class);
    }

    /**
     * 提供ProductRepository
     *
     * @return
     */
    @Provides
    @Singleton
    public static ProductRepository provideProductRepository(ProductService service) {
        return new ProductRepository(service);
    }
}
