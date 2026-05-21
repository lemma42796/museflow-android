package com.ixuea.courses.mymusic.util

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ixuea.courses.mymusic.R
import java.io.FileInputStream
import java.io.FileNotFoundException

/**
 * 图片相关工具类
 */
@Suppress("DEPRECATION")
object ImageUtil {
    /**
     * 显示相对路径图片。
     */
    @JvmStatic
    fun show(context: Context, view: ImageView, data: String?) {
        if (TextUtils.isEmpty(data)) {
            view.setImageResource(R.drawable.placeholder)
            return
        }

        val imagePath = data.orEmpty()
        if (imagePath.contains("/files/Music")) {
            showLocalImage(context, view, imagePath)
            return
        }

        showFull(context, view, ResourceUtil.resourceUri(imagePath))
    }

    /**
     * 显示绝对路径图片。
     */
    @JvmStatic
    fun showFull(context: Context, view: ImageView, data: String?) {
        Glide.with(context)
            .load(data)
            .apply(getCommonRequestOptions())
            .into(view)
    }

    /**
     * 获取公共配置。
     */
    @JvmStatic
    fun getCommonRequestOptions(): RequestOptions {
        return RequestOptions()
            .error(R.drawable.placeholder_error)
    }

    /**
     * 显示头像。
     */
    @JvmStatic
    fun showAvatar(activity: Activity, view: ImageView, uri: String?) {
        if (TextUtils.isEmpty(uri)) {
            show(activity, view, R.drawable.default_avatar)
            return
        }

        val imageUri = uri.orEmpty()
        if (imageUri.startsWith("http")) {
            showFull(activity, view, imageUri)
        } else {
            show(activity, view, imageUri)
        }
    }

    /**
     * 显示圆形相对路径图片。
     */
    @JvmStatic
    fun showCircle(activity: Activity, view: ImageView, uri: String?) {
        showCircleFull(activity, view, ResourceUtil.resourceUri(uri))
    }

    /**
     * 显示圆形绝对路径图片。
     */
    @JvmStatic
    fun showCircleFull(activity: Activity, view: ImageView, uri: String?) {
        Glide.with(activity)
            .load(uri)
            .apply(getCircleCommonRequestOptions())
            .into(view)
    }

    /**
     * 显示资源目录图片。
     */
    @JvmStatic
    fun show(activity: Activity, view: ImageView, @RawRes @DrawableRes resourceId: Int) {
        Glide.with(activity)
            .load(resourceId)
            .apply(getCommonRequestOptions())
            .into(view)
    }

    /**
     * 显示圆形资源目录图片。
     */
    @JvmStatic
    fun showCircle(activity: Activity, view: ImageView, @RawRes @DrawableRes resourceId: Int) {
        Glide.with(activity)
            .load(resourceId)
            .apply(getCircleCommonRequestOptions())
            .into(view)
    }

    /**
     * 显示本地图片。
     */
    @JvmStatic
    fun showLocalImage(context: Context, view: ImageView, data: String?) {
        Glide.with(context)
            .load(data)
            .apply(getCommonRequestOptions())
            .into(view)
    }

    /**
     * 获取图片宽高。
     */
    @JvmStatic
    fun getImageSize(data: String): IntArray {
        return try {
            FileInputStream(data).use { inputStream ->
                val onlyBoundsOptions = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(inputStream, null, onlyBoundsOptions)
                intArrayOf(onlyBoundsOptions.outWidth, onlyBoundsOptions.outHeight)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            intArrayOf(0, 0)
        }
    }

    private fun getCircleCommonRequestOptions(): RequestOptions {
        return getCommonRequestOptions().circleCrop()
    }
}
