package com.ixuea.selector.region;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.util.SuperDarkUtil;
import com.ixuea.selector.region.db.RegionDao;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 地区选择
 */
public class RegionSelectActivity extends AppCompatActivity implements OnItemClickListener {

    private RecyclerView mRecyclerView;
    private RegionAdapter mAdapter;
    private RegionDao mRegionDao;

    private List<Region> mList;

    private List<Region> mProvinceList;
    private List<Region> mCityList;
    private List<Region> mAreaList;
    private int state = 0;

    private Region mProvince;
    private Region mCity;
    private Region mArea;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_region);

        if (SuperDarkUtil.isDark(this)) {
            //状态栏文字白色
            QMUIStatusBarHelper.setStatusBarDarkMode(this);
        } else {
            //状态栏文字黑色
            QMUIStatusBarHelper.setStatusBarLightMode(this);
        }

        toolbar = findViewById(R.id.toolbar);
        //初始化Toolbar
        setSupportActionBar(toolbar);

        //是否显示返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegionDao = new RegionDao(this);

        mList = new ArrayList<>();
        mAdapter = new RegionAdapter(mList);
        mAdapter.setOnItemClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);


        setTitle(R.string.select_province);
        mProvinceList = mRegionDao.loadProvinceList();
        mAdapter.addData(mProvinceList);
    }

    /**
     * 完成
     */
    private void finishSelect() {
        Intent data = new Intent();
        data.putExtra(RegionSelector.REGION_PROVINCE, mProvince);
        data.putExtra(RegionSelector.REGION_CITY, mCity);
        data.putExtra(RegionSelector.REGION_AREA, mArea);

        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (state == 0) {
            super.onBackPressed();
        }

        if (state == 1) {
            setTitle(R.string.select_province);
            mList.clear();
            mAdapter.addData(mProvinceList);
            state--;
        } else if (state == 2) {
            setTitle(R.string.select_city);
            mList.clear();
            mAdapter.addData(mCityList);
            state--;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Region region = mAdapter.getItem(position);

        if (state == 0) {
            setTitle(R.string.select_area);
            mCityList = mRegionDao.loadCityList(region.getId());

            mList.clear();
            mAdapter.addData(mCityList);

            mProvince = region;
            state++;
        } else if (state == 1) {
            setTitle(R.string.select_area);
            mAreaList = mRegionDao.loadCountyList(region.getId());
            mCity = region;

            if (mAreaList.size() == 0) {
                //防止有的城市没有县级
                finishSelect();

            } else {
                mList.clear();
                mAdapter.addData(mAreaList);

                state++;
            }


        } else if (state == 2) {
            mArea = region;
            state++;

            finishSelect();
        }
    }

    /**
     * 菜单点击了回调
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Toolbar返回按钮点击
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
