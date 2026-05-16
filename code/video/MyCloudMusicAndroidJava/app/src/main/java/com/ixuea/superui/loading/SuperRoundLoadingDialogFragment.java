package com.ixuea.superui.loading;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.fragment.BaseDialogFragment;
import com.ixuea.courses.mymusic.util.Constant;

/**
 * 类似iOS的圆角加载对话框
 * <p>
 * 基于该框架修改：https://github.com/ydstar/loadingdialog
 */
public class SuperRoundLoadingDialogFragment extends BaseDialogFragment implements DialogInterface.OnKeyListener {

    private TextView messageView;

    public static SuperRoundLoadingDialogFragment newInstance(String message) {

        Bundle args = new Bundle();
        args.putString(Constant.ID, message);

        SuperRoundLoadingDialogFragment fragment = new SuperRoundLoadingDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static SuperRoundLoadingDialogFragment newInstance() {
        Bundle args = new Bundle();
        SuperRoundLoadingDialogFragment fragment = new SuperRoundLoadingDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        String message = getArguments().getString(Constant.ID);
        if (message == null) {
            message = getString(R.string.loading);
        }
        messageView.setText(message);
    }

    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Dialog dialog = getDialog();

        // 设置背景透明
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // 去掉标题
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCanceledOnTouchOutside(false);

        View view = inflater.inflate(R.layout.super_round_dialog_loading, container);
        messageView = view.findViewById(R.id.message);
//        messageView.setText();

        //不响应返回键
        dialog.setOnKeyListener(this);

        return view;
    }


    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            return true;
        }
        return false;
    }
}
