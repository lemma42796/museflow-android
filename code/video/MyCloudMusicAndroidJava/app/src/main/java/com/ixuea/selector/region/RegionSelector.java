package com.ixuea.selector.region;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.ixuea.selector.region.util.DBCopyUtil;

/**
 * 城市选择器
 */
public class RegionSelector {
    public static final int REQUEST_REGION = 100;

    public static final String REGION_PROVINCE = "region_province";
    public static final String REGION_CITY = "region_city";
    public static final String REGION_AREA = "region_area";
    private static RegionSelector instance;

    public RegionSelector(Context context) {
        DBCopyUtil.copyDataBaseFromAssets(context, "region.db");
    }

    public static RegionSelector getInstance(Context context) {
        if (instance == null) {
            instance = new RegionSelector(context.getApplicationContext());
        }
        return instance;
    }

    public static Region getProvince(Intent data) {
        return (Region) data.getSerializableExtra(REGION_PROVINCE);
    }

    public static Region getCity(Intent data) {
        return (Region) data.getSerializableExtra(REGION_CITY);
    }

    public static Region getArea(Intent data) {
        return (Region) data.getSerializableExtra(REGION_AREA);
    }

    public void start(Activity activity) {
        activity.startActivityForResult(new Intent(activity, RegionSelectActivity.class), REQUEST_REGION);
    }
}
