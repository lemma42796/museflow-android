package com.ixuea.courses.mymusic.component.order.activity;

import android.content.Context;
import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.order.adapter.OrderPageAdapter;
import com.ixuea.courses.mymusic.databinding.ActivityOrderBinding;
import com.ixuea.courses.mymusic.util.Constant;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.Arrays;

/**
 * 我的订单
 */
public class OrderActivity extends BaseTitleActivity<ActivityOrderBinding> {
    private static final int[] indicatorTitles = new int[]{R.string.whole, R.string.wait_pay, R.string.wait_received, R.string.wait_comment};
    private OrderPageAdapter adapter;

    @Override
    protected void initViews() {
        super.initViews();
        binding.list.setOffscreenPageLimit(4);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        adapter = new OrderPageAdapter(getHostActivity());
        binding.list.setAdapter(adapter);

        adapter.setDatum(Arrays.asList(
                Constant.VALUE_NO,
                Constant.WAIT_PAY,
                Constant.WAIT_RECEIVED,
                Constant.WAIT_COMMENT
        ));

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
                titleView.setNormalColor(getResources().getColor(R.color.black80));

                //选中后的颜色
                titleView.setSelectedColor(getResources().getColor(R.color.text_price));

                //设置显示的文本
                titleView.setText(indicatorTitles[index]);

                //点击回调监听
                titleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //让ViewPager跳转到指定位置
                        binding.list.setCurrentItem(index);
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
                indicator.setColors(getColor(R.color.text_price));

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

        binding.list.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                binding.indicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.indicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                binding.indicator.onPageScrollStateChanged(state);
            }
        });
    }
}