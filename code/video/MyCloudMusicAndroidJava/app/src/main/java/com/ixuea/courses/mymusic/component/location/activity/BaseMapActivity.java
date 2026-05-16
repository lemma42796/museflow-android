package com.ixuea.courses.mymusic.component.location.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.util.SuperDarkUtil;

import org.jetbrains.annotations.NotNull;

/**
 * 通用map界面
 * 主要是复用公共逻辑
 */
public class BaseMapActivity<VB extends ViewBinding> extends BaseTitleActivity<VB> {
    /**
     * 获取map对象
     * AMap 类是地图的控制器类，用来操作地图。
     * 它所承载的工作包括：地图图层切换（如卫星图、黑夜地图）、
     * 改变地图状态（地图旋转角度、俯仰角、中心点坐标和缩放级别）、
     * 添加点标记（Marker）、绘制几何图形(Polyline、Polygon、Circle)、
     * 各类事件监听(点击、手势等)等，AMap 是地图 SDK 最重要的核心类，诸多操作都依赖它完成。
     */
    private MapView mapView;

    /**
     * 初始化map
     */
    @SuppressLint("WrongViewCast")
    protected void onCreateMap(Bundle savedInstanceState) {
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView = findViewById(R.id.map_view);

        mapView.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        super.initViews();
        //显示当前位置点
        //官方文档：https://lbs.amap.com/api/android-sdk/guide/create-map/mylocation
        MyLocationStyle myLocationStyle = new MyLocationStyle(); //初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。

        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.showMyLocation(true);

        mapView.getMap().setMyLocationStyle(myLocationStyle); //设置定位蓝点的Style

        UiSettings uiSettings = mapView.getMap().getUiSettings();

        //设置默认定位按钮是否显示，非必需设置。
        uiSettings.setMyLocationButtonEnabled(false);

        //隐藏缩放按钮
        uiSettings.setZoomControlsEnabled(false);

        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mapView.getMap().setMyLocationEnabled(true);

        //设置缩放级别
        //值越大，看的越详细
        //这里设置为，大概能看到小区名称
        mapView.getMap().moveCamera(CameraUpdateFactory.zoomTo(15F));

        if (SuperDarkUtil.isDark(this)) {
            //夜间模式
            mapView.getMap().setMapType(AMap.MAP_TYPE_NIGHT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //我的位置按钮点击
        findViewById(R.id.my_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.getMap().moveCamera(CameraUpdateFactory.changeLatLng(
                        new LatLng(
                                mapView.getMap().getMyLocation().getLatitude(),
                                mapView.getMap().getMyLocation().getLongitude()
                        )
                ));
            }
        });
    }
}
