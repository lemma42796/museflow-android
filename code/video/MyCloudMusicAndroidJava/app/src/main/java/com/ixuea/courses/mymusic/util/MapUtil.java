package com.ixuea.courses.mymusic.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ixuea.courses.mymusic.R;
import com.ixuea.superui.toast.SuperToast;

/**
 * 地图导航工具类
 */
public class MapUtil {
    /**
     * 使用高德地图路径规划
     *
     * @param context
     * @param slat    起点纬度
     * @param slon    起点经度
     * @param sname   起点名称 可不填（0,0，null）
     * @param dlat    终点纬度
     * @param dlon    终点经度
     * @param dname   终点名称 必填
     *                官方文档：https://lbs.amap.com/api/amap-mobile/guide/android/route
     */
    public static void openAmapRoute(
            Context context,
            double slat,
            double slon,
            String sname,
            double dlat,
            double dlon,
            String dname
    ) {
        StringBuilder builder = new StringBuilder("amapuri://route/plan?");
        //第三方调用应用名称
        builder.append("sourceApplication=");
        builder.append(context.getString(R.string.app_name));

        //开始信息
        if (slat != 0.0) {
            builder.append("&sname=").append(sname);
            builder.append("&slat=").append(slat);
            builder.append("&slon=").append(slon);
        }

        //结束信息
        builder.append("&dlat=").append(dlat)
                .append("&dlon=").append(dlon)
                .append("&dname=").append(dname)
                .append("&dev=0")
                .append("&t=0");

        startActivity(context, Constant.PACKAGE_MAP_AMAP, builder.toString());
    }

    /**
     * 使用百度地图导航(默认坐标点是高德地图，需要转换)
     *
     * @param context
     * @param slat    起点纬度
     * @param slon    起点经度
     * @param sname   起点名称 可不填（0,0，null）
     * @param dlat    终点纬度
     * @param dlon    终点经度
     * @param dname   终点名称 必填
     *                官方文档：http://lbsyun.baidu.com/index.php?title=uri/api/android
     */
    public static void openBaiDuRoute(
            Context context,
            double slat,
            double slon,
            String sname,
            double dlat,
            double dlon,
            String dname
    ) {
        String uriString = null;
        //终点坐标转换
//        此方法需要百度地图的BaiduLBS_Android.jar包
//        LatLng destination = new LatLng(dlat,dlon);
//        LatLng destinationLatLng = GCJ02ToBD09(destination);
//        dlat = destinationLatLng.latitude;
//        dlon = destinationLatLng.longitude;
        double[] destination = gaoToBaidu(dlat, dlon);
        dlat = destination[0];
        dlon = destination[1];

        StringBuilder builder = new StringBuilder("baidumap://map/direction?mode=driving&");
        if (slat != 0.0) {
            //起点坐标转换

//            LatLng origin = new LatLng(slat,slon);
//            LatLng originLatLng = GCJ02ToBD09(origin);
//            slat = originLatLng.latitude;
//            slon = originLatLng.longitude;
            double[] origin = gaoToBaidu(slat, slon);
            slat = origin[0];
            slon = origin[1];
            builder.append("origin=latlng:")
                    .append(slat)
                    .append(",")
                    .append(slon)
                    .append("|name:")
                    .append(sname);
        }
        builder.append("&destination=latlng:")
                .append(dlat)
                .append(",")
                .append(dlon)
                .append("|name:")
                .append(dname);

        startActivity(context, Constant.PACKAGE_MAP_BAIDU, builder.toString());
    }

    /**
     * 打开腾讯路径规划
     *
     * @param context
     * @param slat    起点纬度
     * @param slon    起点经度
     * @param sname   起点名称 可不填（0,0，null）
     * @param dlat    终点纬度
     * @param dlon    终点经度
     * @param dname   终点名称 必填
     *                驾车：type=drive，policy有以下取值
     *                0：较快捷
     *                1：无高速
     *                2：距离
     *                policy的取值缺省为0
     *                &from=" + dqAddress + "&fromcoord=" + dqLatitude + "," + dqLongitude + "
     *                官方文档：https://lbs.qq.com/webApi/uriV1/uriGuide/uriMobileRoute
     */
    public static void openTencentMap(
            Context context,
            double slat,
            double slon,
            String sname,
            double dlat,
            double dlon,
            String dname
    ) {

        StringBuilder builder = new StringBuilder("qqmap://map/routeplan?type=drive&policy=0&referer=zhongshuo");
        if (slat != 0.0) {
            builder.append("&from=").append(sname)
                    .append("&fromcoord=").append(slat)
                    .append(",")
                    .append(slon);
        }
        builder.append("&to=").append(dname)
                .append("&tocoord=").append(dlat)
                .append(",")
                .append(dlon);

        startActivity(context, Constant.PACKAGE_MAP_TENCENT, builder.toString());
    }

    private static void startActivity(Context context, String packageName, String data) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage(packageName);
            intent.setData(Uri.parse(data));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            SuperToast.show(R.string.not_install_map);
        }
    }

    /**
     * 高德、腾讯转百度
     *
     * @param gd_lon
     * @param gd_lat
     * @return
     */
    private static double[] gaoToBaidu(double gd_lon, double gd_lat) {
        double PI = Math.PI * 3000.0 / 180.0;
        double z = Math.sqrt(gd_lon * gd_lon + gd_lat * gd_lat) + 0.00002 * Math.sin(
                gd_lat * PI
        );
        double theta = Math.atan2(gd_lat, gd_lon) + 0.000003 * Math.cos(gd_lon * PI);
        return new double[]{z * Math.cos(theta) + 0.0065, z * Math.sin(theta) + 0.006};
    }
}
