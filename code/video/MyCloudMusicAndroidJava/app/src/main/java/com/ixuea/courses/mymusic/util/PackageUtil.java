package com.ixuea.courses.mymusic.util;

import android.content.Context;

public class PackageUtil {

    /**
     * 判断应用是否安装
     *
     * @param context
     * @param data
     * @return
     */
    public static boolean isInstalled(Context context, String data) {
        try {
            context.getPackageManager().getPackageInfo(data, 0);
        } catch (Exception x) {
            return false;
        }
        return false;
    }
}
