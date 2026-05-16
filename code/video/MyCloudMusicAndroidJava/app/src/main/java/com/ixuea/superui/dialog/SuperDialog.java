package com.ixuea.superui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.fragment.BaseDialogFragment;
import com.ixuea.superui.util.SuperViewUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * 对话框封装
 */
public class SuperDialog extends BaseDialogFragment {
    private final FragmentManager fragmentManager;
    private boolean isShowCancelButton = true;

    /**
     * 标题
     */
    private String title;
    private int titleRes;

    /**
     * 提示内容
     */
    private String message;
    private int messageRes;

    /**
     * 确认按钮颜色
     */
    private int confirmButtonColorRes;

    /**
     * 确认按钮文本
     */
    private int confirmButtonTextRes;

    /**
     * 取消按钮文本
     */
    private int cancelButtonTextRes;

    /**
     * 确认点击回调方法
     */
    private View.OnClickListener onClickListener;

    /**
     * 取消回调方法
     */
    private View.OnClickListener onCancelClickListener;

    /**
     * 要显示的布局
     */
    private int layoutResource = R.layout.super_dialog;

    private TextView titleView;
    private TextView messageView;
    private TextView cancelView;
    private TextView confirmView;
    private EditText inputView;

    /**
     * 是否显示输入框
     */
    private boolean isShowInput;

    private SuperDialog(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public static SuperDialog newInstance(FragmentManager fragmentManager) {

        Bundle args = new Bundle();

        SuperDialog fragment = new SuperDialog(fragmentManager);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        titleView = findViewById(R.id.title);
        messageView = findViewById(R.id.message);
        inputView = findViewById(R.id.input);
        cancelView = findViewById(R.id.cancel);
        confirmView = findViewById(R.id.confirm);
    }

    protected void initDatum() {
        super.initDatum();
        setCancelable(false);

        //设置数据
        titleView.setText(title != null ? title : getString(titleRes));

        if (StringUtils.isNotBlank(message) || messageRes != 0) {
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(message != null ? message : getString(messageRes));
        } else {
            messageView.setVisibility(View.GONE);
        }

        SuperViewUtil.gone(inputView, !isShowInput);

        cancelView.setVisibility(isShowCancelButton ? View.VISIBLE : View.GONE);

        if (confirmButtonColorRes != 0) {
            confirmView.setTextColor(getResources().getColor(confirmButtonColorRes));
        }

        if (confirmButtonTextRes != 0) {
            confirmView.setText(confirmButtonTextRes);
        }

        if (cancelButtonTextRes != 0) {
            cancelView.setText(cancelButtonTextRes);
        }
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //取消按钮点击
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onCancelClickListener != null) {
                    onCancelClickListener.onClick(v);
                }
            }
        });

        //确认按钮点击
        confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onClickListener != null) {
                    onClickListener.onClick(v);
                }
            }
        });
    }

    /**
     * 显示对话框
     *
     * @return
     */
    public SuperDialog show() {
        show(fragmentManager, "SuperDialog");
        return this;
    }

    /**
     * 配置删除样式
     * 确认按钮颜色为红色，文本更改为删除
     */
    public SuperDialog deleteStyle() {
        confirmButtonColorRes = R.color.warning;
        confirmButtonTextRes = R.string.delete;
        return this;
    }

    /**
     * 显示提示样式
     * 只有确认按钮
     */
    public SuperDialog alertStyle() {
        isShowCancelButton = false;
        return this;
    }

    /**
     * 标题，输入框，确认按钮
     *
     * @return
     */
    public SuperDialog titleInputConfirmStyle() {
        isShowInput = true;
        return this;
    }

    public SuperDialog setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public SuperDialog setOnClickListener(View.OnClickListener onClickListener, int titleRes) {
        this.onClickListener = onClickListener;
        this.confirmButtonTextRes = titleRes;
        return this;
    }

    public SuperDialog setOnCancelClickListener(View.OnClickListener onCancelClickListener, int titleRes) {
        this.onCancelClickListener = onCancelClickListener;
        this.cancelButtonTextRes = titleRes;
        return this;
    }

    public SuperDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public SuperDialog setTitleRes(int titleRes) {
        this.titleRes = titleRes;
        return this;
    }

    public SuperDialog setCancelButtonTextRes(int data) {
        this.cancelButtonTextRes = data;
        return this;
    }

    public SuperDialog setMessage(String message) {
        this.message = message;
        return this;
    }

    public SuperDialog setMessageRes(int messageRes) {
        this.messageRes = messageRes;
        return this;
    }

    public EditText getInputView() {
        return inputView;
    }

    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getLayoutInflater().inflate(layoutResource, null);
    }
}
