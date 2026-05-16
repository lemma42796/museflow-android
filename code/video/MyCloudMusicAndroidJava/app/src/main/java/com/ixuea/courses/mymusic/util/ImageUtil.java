package com.ixuea.courses.mymusic.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ixuea.courses.mymusic.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * 图片相关工具类
 */
public class ImageUtil {
    /**
     * 显示相对路径图片
     *
     * @param context
     * @param view
     * @param data
     */
    public static void show(Context context, ImageView view, String data) {
        if (TextUtils.isEmpty(data)) {
            //没有图片

            //显示默认图片
            view.setImageResource(R.drawable.placeholder);
            return;
        }

        if (data.contains("/files/Music")) {
            showLocalImage(context, view, data);
            return;
        }

        //将图片地址转为绝对路径
        data = ResourceUtil.resourceUri(data);

        showFull(context, view, data);
    }

    /**
     * 显示绝对路径图片
     *
     * @param context
     * @param view
     * @param data
     */
    public static void showFull(Context context, ImageView view, String data) {
        //获取功能配置
        RequestOptions options = getCommonRequestOptions();

        //显示图片
        Glide.with(context)
                .load(data)
                .apply(options)
                .into(view);

    }

    /**
     * 获取公共配置
     *
     * @return
     */
    public static RequestOptions getCommonRequestOptions() {
        //创建配置选项
        RequestOptions options = new RequestOptions();

        //占位图
//        options.placeholder(R.drawable.placeholder);

        //出错后显示的图片
        //包括：图片不存在等情况
        options.error(R.drawable.placeholder_error);

        //从中心裁剪
//        options.centerCrop();

        return options;
    }

    /**
     * 显示头像
     *
     * @param activity
     * @param view
     * @param uri
     */
    public static void showAvatar(Activity activity, ImageView view, String uri) {
        if (TextUtils.isEmpty(uri)) {
            //没有头像

            //显示默认头像
            show(activity, view, R.drawable.default_avatar);
        } else {
            //有头像

            if (uri.startsWith("http")) {
                //绝对路径
                showFull(activity, view, uri);
            } else {
                //相对路径
                show(activity, view, uri);
            }
        }
    }

    /**
     * 显示圆形相对路径图片
     *
     * @param activity
     * @param view
     * @param uri
     */
    public static void showCircle(Activity activity, ImageView view, String uri) {
        //将相对资源路径转为绝对路径
        uri = ResourceUtil.resourceUri(uri);

        //显示图片
        showCircleFull(activity, view, uri);
    }

    /**
     * 显示圆形绝对路径图片
     *
     * @param activity
     * @param view
     * @param uri
     */
    public static void showCircleFull(Activity activity, ImageView view, String uri) {
        //获取圆形通用的配置
        RequestOptions options = getCircleCommonRequestOptions();

        //显示图片
        Glide.with(activity)
                .load(uri)
                .apply(options)
                .into(view);
    }

    /**
     * 获取圆形通用的配置
     *
     * @return
     */
    private static RequestOptions getCircleCommonRequestOptions() {
        //获取通用配置
        RequestOptions options = getCommonRequestOptions();

        //圆形裁剪
        options.circleCrop();

        return options;
    }

    /**
     * 显示圆形资源目录图片
     *
     * @param activity
     * @param view
     * @param resourceId
     */
    public static void show(Activity activity, ImageView view, @RawRes @DrawableRes @Nullable int resourceId) {
        RequestOptions options = getCommonRequestOptions();

        //显示图片
        Glide.with(activity)
                .load(resourceId)
                .apply(options)
                .into(view);
    }

    /**
     * 显示圆形资源目录图片
     *
     * @param activity
     * @param view
     * @param resourceId
     */
    public static void showCircle(Activity activity, ImageView view, @RawRes @DrawableRes @Nullable int resourceId) {
        //获取公共圆形配置
        RequestOptions options = getCircleCommonRequestOptions();

        //显示图片
        Glide.with(activity)
                .load(resourceId)
                .apply(options)
                .into(view);
    }

    /**
     * 显示本地图片
     *
     * @param context
     * @param view
     * @param data
     */
    public static void showLocalImage(Context context, ImageView view, String data) {
        //获取通用配置
        RequestOptions options = getCommonRequestOptions();

        //使用Glide显示图片
        Glide.with(context)
                .load(data)
                .apply(options)
                .into(view);
    }

    /**
     * 获取图片宽高
     */
    public static int[] getImageSize(String data) {
        try {
            FileInputStream is = new FileInputStream(data);
            BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(is, null, onlyBoundsOptions);

            int originalWidth = onlyBoundsOptions.outWidth;
            int originalHeight = onlyBoundsOptions.outHeight;

            return new int[]{originalWidth, originalHeight};
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
