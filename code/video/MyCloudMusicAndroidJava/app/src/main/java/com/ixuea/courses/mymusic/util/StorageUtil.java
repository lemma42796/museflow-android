package com.ixuea.courses.mymusic.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 存储相关工具栏
 */
public class StorageUtil {
    /**
     * MediaStore，ContentProviders内容提供者data列
     */
    private static final String COLUMN_DATA = "_data";

    /**
     * mp3
     */
    public static final String MP3 = "mp3";

    /**
     * 获取应用扩展sdcard中的路径
     *
     * @param context
     * @param userId  用户id
     * @param title   标题
     * @param suffix  后缀
     * @return
     */
    public static File getExternalPath(Context context, String userId, String title, String suffix) {
        //获取下载文件类型目录
        //该路径下的文件卸载应用后会清空
        //在Android 10路径为：
        // /storage/emulated/0/Android/data/com.ixuea.courses.mymusic/files/Download/
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        //格式化路径
        String path = String.format("MyCloudMusic/%s/%s/%s.%s", userId, suffix, title, suffix);

        //创建文件对象
        File file = new File(dir, path);

        if (!file.getParentFile().exists()) {
            //如果上层目录不存在

            //就创建
            file.getParentFile().mkdirs();
        }

        //返回文件的绝对路径
        return file;
    }

    /**
     * 根据id获取audio content uri
     *
     * @param id
     * @return
     */
    public static String getAudioContentUri(long id) {
        //创建一个builder
        //并将原来的参数拷贝到新的builder
        return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.buildUpon()

                //添加路径
                .appendPath(String.valueOf(id))

                //创建为uri
                .build()

                //转为字符串
                .toString();
    }

    /**
     * 保存图片到系统相册
     *
     * @param context
     * @return
     */
    public static Uri savePicture(Context context, Bitmap data) {
        //创建媒体路径
        Uri uri = insertPictureMediaStore(context, String.format("ixuea_code_%s.jpg", SuperDateUtil.nowyyyyMMddHHmmss()));

        if (uri == null) {
            //获取路径失败
            return null;
        }

        //将原来的图片保存到目标uri
        //也就是保存到系统图片媒体库
        return saveFile(context, data, uri);
    }

    /**
     * 创建相册图片路径
     *
     * @param context
     * @return
     */
    private static Uri insertPictureMediaStore(Context context, String name) {
        //创建内容集合
        ContentValues contentValues = new ContentValues();

        //媒体显示的名称
        //设置为文件的名称
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name);

        //媒体类型
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //拍摄媒体的时间
            contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        }

        return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }

    /**
     * 保存文件到uri对应的路径
     *
     * @param context
     * @return
     */
    private static Uri saveFile(Context context, Bitmap data, Uri uri) {
        try {
            //获取uri对应的文件描述器
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "w");

            try (FileOutputStream out = new FileOutputStream(parcelFileDescriptor.getFileDescriptor())) {
                //保存bitmap
                data.compress(Bitmap.CompressFormat.JPEG, 90, out);

                return uri;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取MediaStore uri的路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getMediaStorePath(Context context, Uri uri) {
        return getDataColumn(context, uri);
    }

    /**
     * 获取uri对应的data列值
     * 其实就是文件路径
     * 这种写法支持MediaStore，ContentProviders
     */
    private static String getDataColumn(Context context, Uri uri) {
        try (Cursor cursor = context
                //获取内容提供者
                .getContentResolver()

                //查询数据
                /**
                 * @param uri 以content://开通的地址
                 * @param projection 返回哪些列
                 * @param selection 查询条件，类似sql中where条件
                 * @param selectionArgs 查询条件参数
                 * @param sortOrder 排序参数
                 */
                .query(uri,
                        new String[]{COLUMN_DATA},
                        null,
                        null,
                        null)) {
            if (cursor != null && cursor.moveToFirst()) {
                //有数据

                //获取这一列的索引
                int index = cursor.getColumnIndexOrThrow(COLUMN_DATA);

                //获取这一列字符类型数据
                return cursor.getString(index);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }
}
