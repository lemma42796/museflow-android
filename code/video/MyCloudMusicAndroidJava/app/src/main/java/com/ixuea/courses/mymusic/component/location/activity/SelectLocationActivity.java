package com.ixuea.courses.mymusic.component.location.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.TextWatcherAdapter;
import com.ixuea.courses.mymusic.component.location.adapter.SelectLocationAdapter;
import com.ixuea.courses.mymusic.component.location.model.event.SelectLocationEvent;
import com.ixuea.courses.mymusic.databinding.ActivitySelectLocationBinding;
import com.ixuea.superui.toast.SuperToast;
import com.ixuea.superui.util.KeyboardUtil;
import com.ixuea.superui.util.SuperRecyclerViewUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * 选择位置界面
 */
public class SelectLocationActivity extends BaseMapActivity<ActivitySelectLocationBinding> implements AMap.OnCameraChangeListener, PoiSearch.OnPoiSearchListener {
    private MyLocationStyle myLocationStyle;
    private Marker centerMarker;
    /**
     * 是否搜索poi
     * 只有用户拖拽地图才搜索
     */
    private boolean isSearchPOI = false;
    private SelectLocationAdapter adapter;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置沉浸式状态栏，内容显示到状态栏
        QMUIStatusBarHelper.translucent(this);
        onCreateMap(savedInstanceState);
    }

    @Override
    protected void initViews() {
        super.initViews();
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        adapter = new SelectLocationAdapter(R.layout.item_select_location);
        binding.list.setAdapter(adapter);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //设置地图相机改变了监听器
        binding.mapView.getMap().setOnCameraChangeListener(this);

        adapter.setOnItemClickListener((a, view, position) -> {
            adapter.setSelected(position);

            Object d = adapter.getItem(position);
            if (d instanceof PoiItem) {
                PoiItem data = (PoiItem) d;

                isSearchPOI = false;

                //地图中心点，移动到选择的位置
                binding.mapView.getMap().moveCamera(
                        CameraUpdateFactory.changeLatLng(
                                new LatLng(data.getLatLonPoint().getLatitude(), data.getLatLonPoint().getLongitude())
                        )
                );
            }
        });

        //发送按钮点击
        binding.rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object d = adapter.getSelectedData();

                //把对象返回要用到的界面
                EventBus.getDefault().postSticky(new SelectLocationEvent(d));

                finish();
            }
        });

        //设置输入框焦点监听器
        binding.input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //清空列表
                    adapter.setNewInstance(new ArrayList<>());

                    binding.cancelButton.setVisibility(View.VISIBLE);
                }
            }
        });

        //取消按钮点击
        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空输入框
                binding.input.setText("");

                //输入框失去焦点
                binding.input.clearFocus();

                //隐藏软键盘
                KeyboardUtil.hideKeyboard(getHostActivity(), binding.input);

                binding.cancelButton.setVisibility(View.GONE);

                //显示当前位置
                adapter.setNewInstance(new ArrayList<>());

                searchPOI(
                        new LatLng(
                                binding.mapView.getMap().getMyLocation().getLatitude(),
                                binding.mapView.getMap().getMyLocation().getLongitude()
                        )
                        , null
                );
            }
        });

        binding.input.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                String name = s.toString();
                if (TextUtils.isEmpty(name)) {
                    adapter.setNewInstance(new ArrayList<>());
                } else {
                    //搜索输入的名称
                    searchPOI(
                            null,
                            name
                    );
                }
            }
        });
    }

    @Override
    public void onCameraChange(CameraPosition data) {
        //显示标记
        showMarker(data);
    }

    @Override
    public void onCameraChangeFinish(CameraPosition data) {
        if (!isSearchPOI) {
            isSearchPOI = true;
            return;
        }
        //搜索POI
        searchPOI(data.target, null);
    }

    /**
     * 搜索该位置的poi，方便用户选择，也方便其他人找
     * Point Of Interest，兴趣点）
     */
    private void searchPOI(LatLng data, String keyword) {
        try {
            Timber.d("searchPOI %s %s", data, keyword);
            binding.progress.setVisibility(View.VISIBLE);
            adapter.setNewInstance(new ArrayList<>());

            // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//        val query = RegeocodeQuery(
//            LatLonPoint(data.latitude, data.longitude)
//            , 1000F, GeocodeSearch.AMAP
//        )
//
//        geocoderSearch.getFromLocationAsyn(query)

            //keyWord表示搜索字符串，
            //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
            //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
            PoiSearch.Query query = new PoiSearch.Query(keyword, "");

            query.setPageSize(10); // 设置每页最多返回多少条poiitem

            query.setPageNum(0); //设置查询页码

            PoiSearch poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);

            //设置周边搜索的中心点以及半径
            if (data != null) {
                poiSearch.setBound(new PoiSearch.SearchBound(
                        new LatLonPoint(
                                data.latitude,
                                data.longitude
                        ), 1000
                ));
            }

            poiSearch.searchPOIAsyn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMarker(CameraPosition data) {
        //获取中心位置

        //显示点
        if (centerMarker != null) {
            //如果直接赋值，他会直接跳过去，看着有点生硬
            //            centerMarker.position = data.target
        } else {
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(data.target);

            markerOption.icon(
                    BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory
                                    .decodeResource(getResources(), R.drawable.ic_location_point)
                    )
            );

            centerMarker = binding.mapView.getMap().addMarker(markerOption);
        }

        //移动到该位置
        TranslateAnimation animation = new TranslateAnimation(data.target);
        animation.setDuration(10);
        centerMarker.setAnimation(animation);
        centerMarker.startAnimation();


    }

    /**
     * poi搜索成功
     */
    @Override
    public void onPoiSearched(PoiResult data, int i) {
        binding.progress.setVisibility(View.GONE);

        if (1000 == i) {
            //搜索成功
            List<Object> results = new ArrayList<>();
            results.add(new Integer(R.string.not_show_position));
            results.addAll(data.getPois());
            adapter.setNewInstance(results);
            binding.rightButton.setEnabled(data.getPois().size() != 0);
        } else {
            SuperToast.error(R.string.error_search_poi);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}