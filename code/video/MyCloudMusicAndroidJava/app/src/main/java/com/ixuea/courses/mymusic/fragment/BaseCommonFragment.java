package com.ixuea.courses.mymusic.fragment;

import android.content.Intent;
import android.text.TextUtils;

import com.ixuea.courses.mymusic.util.Constant;

/**
 * 通用公共Fragment
 */
public abstract class BaseCommonFragment extends BaseFragment {
    /**
     * 启动界面
     *
     * @param clazz
     */
    protected void startActivity(Class<?> clazz) {
        startActivity(new Intent(getHostActivity(), clazz));
    }

    /**
     * 启动界面并关闭当前界面
     *
     * @param clazz
     */
    protected void startActivityAfterFinishThis(Class<?> clazz) {
        Intent intent = new Intent(getHostActivity(), clazz);
        startActivity(intent);

        getHostActivity().finish();
    }

    /**
     * 启动界面，可以传递一个字符串参数
     *
     * @param clazz
     * @param id
     */
    protected void startActivityExtraId(Class<?> clazz, String id) {
        //创建Intent
        Intent intent = new Intent(getHostActivity(), clazz);

        //传递数据
        if (!TextUtils.isEmpty(id)) {
            //不为空才传递
            intent.putExtra(Constant.ID, id);
        }

        //启动界面
        startActivity(intent);
    }


    /**
     * 获取data对象
     *
     * @return
     */
    protected <T> T extraData() {
        return getArguments().getParcelable(Constant.DATA);
    }

    /**
     * 获取字符串
     *
     * @param key
     * @return
     */
    protected String extraString(String key) {
        return getArguments().getString(key);
    }

    /**
     * 获取字符串类型Id
     *
     * @return
     */
    protected String extraId() {
        return extraString(Constant.ID);
    }

    protected int extraInt(String key) {
        return getArguments().getInt(key, -1);
    }
}
