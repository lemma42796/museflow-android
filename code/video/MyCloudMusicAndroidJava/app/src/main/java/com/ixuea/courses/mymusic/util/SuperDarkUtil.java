package com.ixuea.courses.mymusic.util;

import android.content.Context;
import android.content.res.Configuration;

/**
 * 深色模型工具类
 */
public class SuperDarkUtil {
    /**
     * 是否是深色模型
     */
    public static boolean isDark(Context context) {
        return (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
}
