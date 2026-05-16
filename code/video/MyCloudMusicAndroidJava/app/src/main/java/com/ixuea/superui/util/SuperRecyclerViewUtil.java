package com.ixuea.superui.util;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SuperRecyclerViewUtil {
    /**
     * 初始化垂直方向LinearLayoutManager RecyclerView
     *
     * @param view
     * @param isAddDivider
     */
    public static void initVerticalLinearRecyclerView(RecyclerView view) {
        initVerticalLinearRecyclerView(view, false);
    }

    /**
     * 初始化垂直方向LinearLayoutManager RecyclerView
     *
     * @param view
     * @param isAddDivider
     */
    public static void initVerticalLinearRecyclerView(RecyclerView view, boolean isAddDivider) {
        //尺寸固定
        view.setHasFixedSize(true);

        //布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        view.setLayoutManager(layoutManager);

        if (isAddDivider) {
            //分割线
            DividerItemDecoration decoration =
                    new DividerItemDecoration(view.getContext(), RecyclerView.VERTICAL);
            view.addItemDecoration(decoration);
        }

    }
}
