package com.ixuea.courses.mymusic.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * 文件工具类
 */
public class FileUtil {
    /**
     * 获取缓存广告目录
     *
     * @param context
     * @param data
     * @return
     */
    public static File adFile(Context context, String data) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath().concat("/ads"), data);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        return file;
    }

    /**
     * 文件大小格式化
     *
     * @param value 文件大小；单位byte
     * @return 格式化的文件大小；例如：1.22M
     */
    public static String formatFileSize(long value) {
        if (value > 0) {
            double size = value;

            double kiloByte = size / 1024;
            if (kiloByte < 1 && kiloByte > 0) {
                //不足1k
                //return  size+"Byte";
                return String.format("%.2fByte", size);
            }

            double megaByte = kiloByte / 1024;
            if (megaByte < 1) {
                //不足1M
                return String.format("%.2fK", kiloByte);
            }

            double gigaByte = megaByte / 1024;
            if (gigaByte < 1) {
                //不足1G
                return String.format("%.2fM", megaByte);
            }

            double teraByte = gigaByte / 1024;
            if (teraByte < 1) {
                //不足1T
                return String.format("%.2fG", gigaByte);
            }

            return String.format("%.2fT", teraByte);

        }
        return "0K";
    }
}
