package com.ixuea.superui.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * 网络工具类
 * <p>
 * 可以判断是否有网络链接，以及是WiFi，还是移动网络
 */
public class SuperNetworkUtil {
    /**
     * 网络是否连接了
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = getConnectivityManager(context);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null) {
                    return networkInfo.isAvailable();
                }
            } else {
                //获取当前激活的网络
                Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork == null) {
                    return false;
                }

                //获取激活的网络详细信息
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                if (networkCapabilities == null) {
                    return false;
                }

                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    //可以正常上网
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * WiFi网络是否连接了
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        return getConnectedType(context) == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 移动网络是否连接了
     *
     * @param context
     * @return 这些方法返回的信息是有优先级的，例如：同时连接了移动网络，Wifi，这个方法返回false
     */
    public static boolean isMobileConnected(Context context) {
        return getConnectedType(context) == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * 获取网络连接类型
     *
     * @param context
     * @return
     */
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = getConnectivityManager(context);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                return networkInfo.getType();
            }
        }
        return -1;
    }

    public static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
