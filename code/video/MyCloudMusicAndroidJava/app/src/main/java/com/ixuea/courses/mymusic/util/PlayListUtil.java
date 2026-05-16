package com.ixuea.courses.mymusic.util;

import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.manager.MusicListManager;

/**
 * 播放列表工具类
 */
public class PlayListUtil {
    /**
     * 显示循环模式
     *
     * @param loopModel
     * @param view
     */
    public static void showLoopModel(int loopModel, TextView view) {
        switch (loopModel) {
            case MusicListManager.MODEL_LOOP_LIST:
                view.setText("列表循环");
                break;
            case MusicListManager.MODEL_LOOP_RANDOM:
                view.setText("随机循环");
                break;
            default:
                view.setText("单曲循环");
                break;
        }
    }

    /**
     * 根据循环模式返回对应的图标
     *
     * @param data
     * @return
     */
    public static int getLoopModelIcon(int data) {
        switch (data) {
            case MusicListManager.MODEL_LOOP_RANDOM:
                return R.drawable.music_repeat_random;
            case MusicListManager.MODEL_LOOP_ONE:
                return R.drawable.music_repeat_one;
            default:
                return R.drawable.music_repeat_list;
        }
    }
}
