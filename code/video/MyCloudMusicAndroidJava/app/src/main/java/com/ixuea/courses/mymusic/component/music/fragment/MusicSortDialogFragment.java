package com.ixuea.courses.mymusic.component.music.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.ixuea.courses.mymusic.R;

/**
 * 歌曲排序对话框
 */
public class MusicSortDialogFragment extends DialogFragment {
    /**
     * 点击事件
     */
    private DialogInterface.OnClickListener onClickListener;

    /**
     * 排序索引
     */
    private int sortIndex;

    /**
     * 创建方法
     *
     * @return
     */
    public static MusicSortDialogFragment newInstance() {

        Bundle args = new Bundle();

        MusicSortDialogFragment fragment = new MusicSortDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 显示对话框
     *
     * @param fragmentManager
     * @param onClickListener
     */
    public static void show(FragmentManager fragmentManager, int sortIndex, DialogInterface.OnClickListener onClickListener) {
        //创建对话框
        MusicSortDialogFragment fragment = newInstance();

        //排序索引
        fragment.sortIndex = sortIndex;

        //点击监听器
        fragment.onClickListener = onClickListener;

        //显示
        fragment.show(fragmentManager, "MusicSortDialogFragment");
    }

    /**
     * 创建对话框
     *
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                //设置标题
                .setTitle(R.string.sort)

                //设置单选按钮
                .setSingleChoiceItems(R.array.dialog_song_sort, sortIndex, onClickListener);

        //创建对话框
        return builder.create();
    }
}
