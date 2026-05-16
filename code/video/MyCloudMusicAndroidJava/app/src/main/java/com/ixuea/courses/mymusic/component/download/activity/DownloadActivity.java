package com.ixuea.courses.mymusic.component.download.activity;

import com.flyco.tablayout.listener.OnTabSelectListener;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.adapter.OnPageChangeListenerAdapter;
import com.ixuea.courses.mymusic.component.download.adapter.DownloadAdapter;
import com.ixuea.courses.mymusic.databinding.ActivityDownloadBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * 下载管理界面
 */
public class DownloadActivity extends BaseTitleActivity<ActivityDownloadBinding> {
    private DownloadAdapter adapter;

    @Override
    protected void initDatum() {
        super.initDatum();
        //创建适配器
        adapter = new DownloadAdapter(getHostActivity(), getSupportFragmentManager());

        //设置适配器
        binding.list.setAdapter(adapter);

        //创建列表
        List<Integer> datum = new ArrayList<>();
        datum.add(0);
        datum.add(1);

        //设置数据
        adapter.setDatum(datum);

        //指示器
        String[] indicatorTitles = new String[]{getString(R.string.download_complete), getString(R.string.downloading)};
        binding.indicator.setTabData(indicatorTitles);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //设置指示器切换监听器
        binding.indicator.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                binding.list.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        //ViewPager切换监听器
        binding.list.addOnPageChangeListener(new OnPageChangeListenerAdapter() {
            @Override
            public void onPageSelected(int position) {
                binding.indicator.setCurrentTab(position);
            }
        });
    }
}