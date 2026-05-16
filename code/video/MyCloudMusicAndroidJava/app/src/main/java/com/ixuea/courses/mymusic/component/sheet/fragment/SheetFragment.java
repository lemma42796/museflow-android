package com.ixuea.courses.mymusic.component.sheet.fragment;

import static autodispose2.AutoDispose.autoDisposable;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.discovery.adapter.SheetAdapter;
import com.ixuea.courses.mymusic.component.search.fragment.BaseSearchFragment;
import com.ixuea.courses.mymusic.component.sheet.activity.SheetDetailActivity;
import com.ixuea.courses.mymusic.component.sheet.model.Sheet;
import com.ixuea.courses.mymusic.model.response.ListResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.superui.decoration.GridDividerItemDecoration;
import com.ixuea.superui.util.DensityUtil;
import com.ixuea.superui.util.SuperViewUtil;

import org.apache.commons.collections4.CollectionUtils;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 歌单搜索fragment
 */
public class SheetFragment extends BaseSearchFragment {

    private SheetAdapter adapter;

    public static SheetFragment newInstance(int data) {

        Bundle args = new Bundle();
        args.putInt(Constant.STYLE, data);

        SheetFragment fragment = new SheetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        //设置显示3列
        GridLayoutManager layoutManager = new GridLayoutManager(getHostActivity(), 3);
        binding.list.setLayoutManager(layoutManager);

        GridDividerItemDecoration itemDecoration = new GridDividerItemDecoration(getContext(), (int) DensityUtil.dip2px(getContext(), 5F));
        binding.list.addItemDecoration(itemDecoration);

    }

    @Override
    protected void initDatum() {
        super.initDatum();
        adapter = new SheetAdapter(R.layout.item_sheet);
        binding.list.setAdapter(adapter);
    }

    @Override
    protected void initListeners() {
        super.initListeners();

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Sheet data = (Sheet) adapter.getItem(position);
                startActivityExtraId(SheetDetailActivity.class, data.getId());
            }
        });
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        DefaultRepository
                .getInstance()
                .searchSheets(query)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<ListResponse<Sheet>>() {
                    @Override
                    public void onSucceeded(ListResponse<Sheet> data) {
                        if (CollectionUtils.isEmpty(data.getData().getData())) {
                            SuperViewUtil.show(binding.placeholderView);
                            SuperViewUtil.gone(binding.list);
                        } else {
                            SuperViewUtil.gone(binding.placeholderView);
                            SuperViewUtil.show(binding.list);

                            adapter.setNewInstance(data.getData().getData());
                        }
                    }
                });
    }
}
