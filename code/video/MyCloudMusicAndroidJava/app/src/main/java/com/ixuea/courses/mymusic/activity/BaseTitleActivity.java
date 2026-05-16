package com.ixuea.courses.mymusic.activity;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;

import com.ixuea.courses.mymusic.R;

/**
 * 通用标题界面
 */
public class BaseTitleActivity<VB extends ViewBinding> extends BaseViewModelActivity<VB> {
    protected Toolbar toolbar;

    @Override
    protected void initViews() {
        super.initViews();
        toolbar = findViewById(R.id.toolbar);

        //初始化Toolbar
        setSupportActionBar(toolbar);

        //是否显示返回按钮
        if (isShowBackMenu()) {
            showBackMenu();
        }
    }

    /**
     * 是否显示返回按钮
     *
     * @return
     */
    protected boolean isShowBackMenu() {
        return true;
    }

    /**
     * 显示返回按钮
     */
    protected void showBackMenu() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Toolbar返回按钮点击
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
