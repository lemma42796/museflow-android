package com.ixuea.courses.mymusic.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Window;

import com.ixuea.courses.mymusic.util.Constant;

/**
 * 通用界面逻辑
 */
public class BaseCommonActivity extends BaseActivity {
    /**
     * 启动界面
     *
     * @param clazz
     */
    protected void startActivity(Class<? extends Activity> clazz) {
        startActivity(new Intent(this, clazz));
    }

    /**
     * 启动界面并关闭当前界面
     *
     * @param clazz
     */
    protected void startActivityAfterFinishThis(Class<? extends Activity> clazz) {
        startActivity(clazz);

        finish();
    }

    /**
     * 启动界面，可以传递一个字符串参数
     *
     * @param clazz
     * @param id
     */
    protected void startActivityExtraId(Class<?> clazz, String id) {
        //创建Intent
        Intent intent = new Intent(this, clazz);

        //传递数据
        if (!TextUtils.isEmpty(id)) {
            //不为空才传递
            intent.putExtra(Constant.ID, id);
        }

        //启动界面
        startActivity(intent);
    }

    /**
     * 启动界面，可以传递一个对象参数
     *
     * @param clazz
     * @param data
     */
    protected void startActivityExtraData(Class<?> clazz, Parcelable data) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(Constant.DATA, data);
        startActivity(intent);
    }

    /**
     * 获取字符串
     *
     * @param key
     * @return
     */
    protected String extraString(String key) {
        return getIntent().getStringExtra(key);
    }

    protected int extraInt(String key) {
        return getIntent().getIntExtra(key, -1);
    }

    protected <T> T extraData() {
        return getIntent().getParcelableExtra(Constant.DATA);
    }

    /**
     * 获取字符串类型Id
     *
     * @return
     */
    protected String extraId() {
        return extraString(Constant.ID);
    }

    /**
     * 设置状态栏颜色
     *
     * @param data
     */
    protected void setStatusBarColor(int data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //设置状态栏颜色
            Window window = getWindow();

            window.setStatusBarColor(data);

            //设置导航栏颜色
            window.setNavigationBarColor(data);
        }
    }
}
