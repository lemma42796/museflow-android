package com.ixuea.courses.mymusic.component.about.activity;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseLogicActivity;
import com.ixuea.courses.mymusic.config.Config;
import com.ixuea.courses.mymusic.util.IntentUtil;
import com.ixuea.courses.mymusic.util.ResourceUtil;
import com.ixuea.superui.setting.SuperItemSettingView;
import com.ixuea.superui.util.DensityUtil;
import com.ixuea.superui.util.SuperPackageUtil;

/**
 * 纯代码实现关于界面
 */
public class AboutCodeActivity extends BaseLogicActivity {

    private Toolbar toolbar;
    private LinearLayout contentContainer;
    private SuperItemSettingView itemVersionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //这里实现方法和可视化差不多
        //只是这些类是在代码中创建的

        //根布局
        CoordinatorLayout container = new CoordinatorLayout(getHostActivity());

        initToolbar(container);

        //内容容器
        contentContainer = new LinearLayout(getHostActivity());

        //设置方向
        contentContainer.setOrientation(LinearLayout.VERTICAL);

        //内容水平居中
        contentContainer.setGravity(Gravity.CENTER_HORIZONTAL);

        //布局参数
        //宽高；位置都在这个类上面设置
        //他的类型为当前控件所在容器里面的LayoutParams
        CoordinatorLayout.LayoutParams contentLayoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        //设置behavior
        contentLayoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior());

        //将内容容器添加到根布局
        container.addView(contentContainer, contentLayoutParams);

        //region logo
        ImageView logoView = new ImageView(getHostActivity());

        //设置显示的图片
        logoView.setImageResource(R.mipmap.ic_launcher);

        //logo布局参数
        LinearLayout.LayoutParams logoLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //距离顶部边距
        logoLayoutParams.topMargin = getResources().getDimensionPixelOffset(R.dimen.d40);

        //添加logo到容器
        contentContainer.addView(logoView, logoLayoutParams);
        //endregion

        //region 版本容器
        itemVersionView = new SuperItemSettingView(getHostActivity());
        itemVersionView.setIcon(R.drawable.scan);
        itemVersionView.setTitle(R.string.current_version);

        LinearLayout.LayoutParams versionLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        versionLayoutParams.topMargin = (int) DensityUtil.dip2px(getHostActivity(), 40);

        contentContainer.addView(itemVersionView, versionLayoutParams);
        //endregion

        int meddlePadding = getResources().getDimensionPixelOffset(R.dimen.padding_meddle);
        int smallPadding = getResources().getDimensionPixelOffset(R.dimen.divider_small);

        //添加功能介绍item
        addItem(R.string.function_introduction, meddlePadding, null);

        //关于爱学啊
        addItem(R.string.about_us, smallPadding, v -> IntentUtil.startBrowser(getHostActivity(), Config.QRCODE_URL));

        //设置内容
        setContentView(container);
    }

    /**
     * 添加item
     *
     * @param titleResourceId
     * @param marginTop
     * @param onClickListener
     */
    private void addItem(int titleResourceId, int marginTop, View.OnClickListener onClickListener) {
        LinearLayout container = new LinearLayout(getHostActivity());

        //白色背景
        container.setBackgroundResource(R.drawable.selector_surface);

        int leftPadding = (int) DensityUtil.dip2px(getHostActivity(), 16);
        int topPadding = (int) DensityUtil.dip2px(getHostActivity(), 15);

        //内边距
        container.setPadding(leftPadding, topPadding, leftPadding, topPadding);

        //点击事件
        if (onClickListener != null) {
            container.setOnClickListener(onClickListener);
        }

        //region 标题
        TextView titleView = new TextView(getHostActivity());
        titleView.setText(titleResourceId);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelOffset(R.dimen.super_text_large));
        titleView.setTextColor(ResourceUtil.getColorAttributes(getHostActivity(), com.google.android.material.R.attr.colorOnSurface));

        LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);

        //权重
        titleLayoutParams.weight = 1;

        container.addView(titleView, titleLayoutParams);
        //endregion

        //region 更多图标
        ImageView moreIconView = new ImageView(getHostActivity());

        moreIconView.setImageResource(R.drawable.super_chevron_right);

        int iconWidth = (int) DensityUtil.dip2px(getHostActivity(), 15);

        LinearLayout.LayoutParams moreIconLayoutParams = new LinearLayout.LayoutParams(iconWidth, iconWidth);
        container.addView(moreIconView, moreIconLayoutParams);
        //endregion

        LinearLayout.LayoutParams containerLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        containerLayoutParams.topMargin = marginTop;
        contentContainer.addView(container, containerLayoutParams);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        //获取版本名称
        String versionName = SuperPackageUtil.getVersionName(getHostActivity());

        //版本号
        long versionCode = SuperPackageUtil.getVersionCode(getHostActivity());

        String version = getResources().getString(R.string.version_value, versionName, versionCode);
        itemVersionView.setMoreText(version);
    }

    /**
     * 初始化toolbar
     *
     * @param container
     */
    private void initToolbar(CoordinatorLayout container) {
        //添加Toolbar
        //Toolbar也可以手动创建
        //也可以使用布局
        View appBarLayout = View.inflate(getHostActivity(), R.layout.toolbar, container);

        //查找toolbar控件
        toolbar = appBarLayout.findViewById(R.id.toolbar);

        //初始化Toolbar
        setSupportActionBar(toolbar);

        //是否显示返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Toolbar返回按钮点击
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}