package com.ixuea.superui.util;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;

/**
 * bitmap相关工具方法
 */
public class BitmapUtil {
    /**
     * 将bitmap保存到文件
     *
     * @param bitmap
     * @param file
     * @return
     */
    public static boolean saveToFile(Bitmap bitmap, File file) {
        ////java对流的常规写法
        //
        //FileOutputStream out =null;
        //try {
        //    //创建输出流
        //    out = new FileOutputStream(file);
        //
        //    //保存bitmap
        //    bitmap.compress(Bitmap.CompressFormat.JPEG,90,out);
        //
        //    return true;
        //} catch (FileNotFoundException e) {
        //    e.printStackTrace();
        //} finally {
        //    //关闭流
        //    //标准的关闭流写法还是比较复杂的
        //    //同时由于这不是Java基础课程
        //    //所以使用了apache的common io工具包关闭
        //
        //    //可以看到该方法以及过时了
        //    //后面会讲解新的写法
        //    IOUtils.closeQuietly(out);
        //}

        //使用try-with-resource
        //他是JDK7实现了一个语法糖
        try (FileOutputStream out = new FileOutputStream(file)) {
            //保存bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
