package com.ixuea.courses.mymusic.component.profile.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.Calendar;
import java.util.Locale;

/**
 * 选择日期对话框
 */
public class DateDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "DateDialogFragment";

    /**
     * 回调监听器
     */
    private DateListener dateListener;

    /**
     * 显示对话框
     *
     * @param fragmentManager
     * @param dateListener
     */
    public static void show(FragmentManager fragmentManager, DateListener dateListener) {
        //创建fragment
        DateDialogFragment fragment = newInstance();

        //设置监听器
        fragment.dateListener = dateListener;

        //显示
        fragment.show(fragmentManager, "DateDialogFragment");
    }

    /**
     * 创建方法
     *
     * @return
     */
    public static DateDialogFragment newInstance() {

        Bundle args = new Bundle();

        DateDialogFragment fragment = new DateDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 返回对话框
     *
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //获取日历控件
        Calendar calendar = Calendar.getInstance();

        //获取年
        int year = calendar.get(Calendar.YEAR);

        //月
        int month = calendar.get(Calendar.MONTH);

        //获取天
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                com.google.android.material.R.style.Theme_AppCompat_Light_Dialog_Alert,
                this,
                year, month, day);

        return datePickerDialog;
    }

    /**
     * 选择了回调
     *
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        //格式化为字符串
        String dateString = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);

        //回调监听器
        dateListener.onSelected(dateString);
    }

    /**
     * 日期回调监听器
     */
    public interface DateListener {
        /**
         * 选择了
         *
         * @param data
         */
        void onSelected(String data);
    }
}
