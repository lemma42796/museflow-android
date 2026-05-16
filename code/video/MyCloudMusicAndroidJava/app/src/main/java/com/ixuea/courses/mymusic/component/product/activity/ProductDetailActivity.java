package com.ixuea.courses.mymusic.component.product.activity;

import static autodispose2.AutoDispose.autoDisposable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseViewModelActivity;
import com.ixuea.courses.mymusic.component.order.activity.ConfirmOrderActivity;
import com.ixuea.courses.mymusic.component.product.model.Product;
import com.ixuea.courses.mymusic.component.product.ui.ProductDetailViewModel;
import com.ixuea.courses.mymusic.databinding.ActivityProductDetailBinding;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.ResourceUtil;
import com.ixuea.courses.mymusic.util.ScreenUtil;
import com.ixuea.courses.mymusic.util.SuperDarkUtil;
import com.ixuea.courses.mymusic.util.SuperTextUtil;
import com.ixuea.superui.toast.SuperToast;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.Arrays;
import java.util.List;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import dagger.hilt.android.AndroidEntryPoint;
import me.wcy.htmltext.HtmlImageLoader;
import me.wcy.htmltext.HtmlText;
import me.wcy.htmltext.OnTagClickListener;
import timber.log.Timber;

/**
 * 商品详情
 */
@AndroidEntryPoint
public class ProductDetailActivity extends BaseViewModelActivity<ActivityProductDetailBinding> {
    /**
     * 指示器标题
     */
    private static final int[] indicatorTitles = new int[]{R.string.product, R.string.detail};

    private ProductDetailViewModel viewModel;
    private int colorPrimary;
    private int textColor;
    private int colorBackground;

    @Override
    protected void initViews() {
        super.initViews();
        //状态栏透明，内容显示到状态栏
        QMUIStatusBarHelper.translucent(this);

        colorPrimary = ResourceUtil.getColorAttributes(getHostActivity(), com.google.android.material.R.attr.colorPrimary);
        textColor = ResourceUtil.getColorAttributes(getHostActivity(), com.google.android.material.R.attr.colorOnSurface);
        colorBackground = ResourceUtil.getColorAttributes(getHostActivity(), android.R.attr.colorBackground);

        //原价设置删除线
        binding.originPrice.setPaintFlags(binding.originPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        //创建通用指示器
        CommonNavigator commonNavigator = new CommonNavigator(getHostActivity());

        //设置适配器
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            /**
             * 指示器数量
             *
             * @return
             */
            @Override
            public int getCount() {
                return indicatorTitles.length;
            }

            /**
             * 返回当前位置的标题
             *
             * @param context
             * @param index
             * @return
             */
            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                //创建简单的文本控件
                SimplePagerTitleView titleView = new SimplePagerTitleView(context);

                //默认颜色
                titleView.setNormalColor(colorPrimary);

                //选中后的颜色
                titleView.setSelectedColor(textColor);

                //设置显示的文本
                titleView.setText(indicatorTitles[index]);

                //点击回调监听
                titleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                return titleView;
            }

            /**
             * 返回指示器
             * 就是下面那条线
             *
             * @param context
             * @return
             */
            @Override
            public IPagerIndicator getIndicator(Context context) {
                //创建一条线
                LinePagerIndicator indicator = new LinePagerIndicator(context);

                //线的宽度和内容一样
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);

                //高亮颜色
                indicator.setColors(getColor(R.color.primary));

                return indicator;

                //返回null表示不显示指示器
                //return null;
            }
        });

        //如何位置显示不下指示器
        //是否自动调整
        commonNavigator.setAdjustMode(true);

        //设置导航器
        binding.indicator.setNavigator(commonNavigator);

        SuperTextUtil.setLinkColor(binding.detail, getColor(R.color.link));
    }

    @Override
    protected void initDatum() {
        super.initDatum();

        //创建ViewModel
        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);

        loadData();
    }


    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        viewModel.productDetail(extraId())
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(result -> {
                    showData(result.getData());
                });
    }

    private void showData(Product data) {
        //轮播图
        binding.banner.setAdapter(new BannerImageAdapter<String>(Arrays.asList(data.getIcons())) {
            @Override
            public void onBindView(BannerImageHolder holder, String data, int position, int size) {
                //图片加载自己实现
                ImageUtil.show(getHostActivity(), (ImageView) holder.itemView, data);
            }
        })
                .isAutoLoop(false)
                .addBannerLifecycleObserver(this)//添加生命周期观察者
                .setIndicator(new CircleIndicator(getHostActivity()));

        //价格，也可以在代码中格式化
        binding.price.setText(String.format("￥%.2f", data.getPrice()));

        binding.title.setText(data.getTitle());
        binding.highlight.setText(data.getHighlight());

        //购买人数
        binding.buyCount.setText(getResources().getString(R.string.buy_count, data.getOrdersCount()));

        //详情
        HtmlText.from(data.getDetail())
                .setImageLoader(new HtmlImageLoader() {
                    @Override
                    public void loadImage(String url, final Callback callback) {
                        Glide.with(getHostActivity())
                                .asBitmap()
                                .load(url)
                                .into(new CustomTarget<Bitmap>() {

                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        callback.onLoadComplete(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        callback.onLoadFailed();
                                    }
                                });
                    }

                    @Override
                    public Drawable getDefaultDrawable() {
                        return ContextCompat.getDrawable(getHostActivity(), R.drawable.placeholder);
                    }

                    @Override
                    public Drawable getErrorDrawable() {
                        return ContextCompat.getDrawable(getHostActivity(), R.drawable.placeholder_error);
                    }

                    @Override
                    public int getMaxWidth() {
                        return ScreenUtil.getScreenWith(getHostActivity());
                    }

                    @Override
                    public boolean fitWidth() {
                        return true;
                    }
                })
                .setOnTagClickListener(new OnTagClickListener() {
                    @Override
                    public void onImageClick(Context context, List<String> imageUrlList, int position) {
                        // image click
                    }

                    @Override
                    public void onLinkClick(Context context, String url) {
                        // link click
                        Timber.d("onLinkClick %s", url);
                    }
                })
                .into(binding.detail);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        viewModel.loginPage.observe(this, unused -> toLogin());

        viewModel.purchasePage.observe(this, data -> startActivityExtraId(ConfirmOrderActivity.class, data));

        //添加到购物车成功
        viewModel.addToCartSuccess.observe(this, data -> {
            SuperToast.show(R.string.add_cart_success);
        });

        //添加到购物车按钮点击
        binding.addCart.setOnClickListener(v -> {
            viewModel.addCart();
        });

        //监听列表滚动，目的是显示标题栏
        binding.scrollContent.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Timber.d("onScrollChange %d %d", scrollY, oldScrollY);

                int alpha = scrollY;
                if (alpha > 255) {
                    alpha = 255;
                }

                //创建一个白色，默认是完全透明，向上滚动，慢慢变为完全不透明
                int toolbarBackgroundColor;
                if (SuperDarkUtil.isDark(getHostActivity())) {
                    //深色模式；完全不透明后就是黑色
                    toolbarBackgroundColor = Color.argb(alpha, 0, 0, 0);
                } else {
                    //创建一个白色，默认是完全透明，向上滚动，慢慢变为完全不透明
                    toolbarBackgroundColor = Color.argb(alpha, 255, 255, 255);
                }

                binding.toolbarContainer.setBackgroundColor(toolbarBackgroundColor);

                binding.indicator.setAlpha((float) (alpha / 255.0));
            }
        });
        binding.primary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sp.isLogin()) {
                    viewModel.startPurchasePage();
                } else {
                    viewModel.startLoginPage();
                }
            }
        });
    }
}