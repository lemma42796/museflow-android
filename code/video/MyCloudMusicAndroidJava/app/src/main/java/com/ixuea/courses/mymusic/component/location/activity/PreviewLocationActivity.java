package com.ixuea.courses.mymusic.component.location.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.location.model.PreviewLocationPagedData;
import com.ixuea.courses.mymusic.databinding.ActivityPreviewLocationBinding;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.MapUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.ArrayList;

/**
 * 预览位置（位置详情）界面
 */
public class PreviewLocationActivity extends BaseMapActivity<ActivityPreviewLocationBinding> {
    private PreviewLocationPagedData pagedData;

    public static void start(Context context, PreviewLocationPagedData data) {
        //创建intent
        Intent intent = new Intent(context, PreviewLocationActivity.class);

        intent.putExtra(Constant.DATA, data);

        //启动界面
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateMap(savedInstanceState);
    }

    @Override
    protected void initViews() {
        super.initViews();
        //设置沉浸式状态栏，内容显示到状态栏
        QMUIStatusBarHelper.translucent(this);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        pagedData = extraData();

        //显示目标点
        //添加位置marker
        MarkerOptions markerOption = new MarkerOptions();
        LatLng latLng = new LatLng(
                pagedData.getLatitude(),
                pagedData.getLongitude()
        );
        markerOption.position(
                latLng
        );

        markerOption.icon(
                BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory
                                .decodeResource(getResources(), R.drawable.ic_location_point)
                )
        );

        Marker marker = binding.mapView.getMap().addMarker(markerOption);
        marker.setVisible(true);

        //移动该位置到中心点
        binding.mapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

        //显示其他信息
        binding.title.setText(pagedData.getTitle());
        binding.address.setText(pagedData.getAddress());
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //导航按钮点击
        binding.navigation.setOnClickListener(v -> showNavigationDialog());
    }

    private void showNavigationDialog() {
        //让系统弹出选择界面
        //            val uri = Uri.parse("geo:${data.latitude},${data.longitude}?q=${data.title}")
        //            val mIntent = Intent(Intent.ACTION_VIEW, uri)
        //            startActivity(mIntent)

        ArrayList<String> titles = new ArrayList<>();

        //判断是否安装了地图，只有安装了才显示
        //部分地址没有安装，会打开网页打开
//        if (PackageUtil.isInstalled(getHostActivity(),Constant.PACKAGE_MAP_TENCENT)) {
        titles.add(getString(R.string.map_tencent));
//        }

        titles.add(getString(R.string.map_baidu));
        titles.add(getString(R.string.map_amap));

        //手动实现选择界面，类似微信效果
        new AlertDialog.Builder(this)
                .setTitle(R.string.select_location_navigation)
                .setItems(titles.toArray(new String[titles.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        processNavigation(which);
                    }
                })
                .show();
    }

    private void processNavigation(int position) {
        if (position == 0) {
            //点击腾讯地图
            MapUtil.openTencentMap(
                    getHostActivity(), 0.0, 0.0, null,
                    pagedData.getLatitude(), pagedData.getLongitude(), pagedData.getTitle()
            );
        } else if (position == 1) {
            MapUtil.openBaiDuRoute(
                    getHostActivity(), 0.0, 0.0, null,
                    pagedData.getLatitude(), pagedData.getLongitude(), pagedData.getTitle()
            );
        } else if (position == 2) {
            //不传递开始位置信息，就是从当前位置
            MapUtil.openAmapRoute(
                    getHostActivity(), 0.0, 0.0, null,
                    pagedData.getLatitude(), pagedData.getLongitude(), pagedData.getTitle()
            );
        }
    }
}