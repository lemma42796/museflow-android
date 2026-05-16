package com.ixuea.courses.mymusic.component.user.fragment;

import static autodispose2.AutoDispose.autoDisposable;

import android.os.Bundle;

import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.search.fragment.BaseSearchFragment;
import com.ixuea.courses.mymusic.component.user.activity.UserDetailActivity;
import com.ixuea.courses.mymusic.component.user.adapter.UserAdapter;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.model.response.ListResponse;
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.superui.util.SuperRecyclerViewUtil;
import com.ixuea.superui.util.SuperViewUtil;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 用户搜索结果fragment
 */
public class UserFragment extends BaseSearchFragment {
    private UserAdapter adapter;

    public static UserFragment newInstance(int data) {

        Bundle args = new Bundle();
        args.putInt(Constant.STYLE, data);

        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        adapter = new UserAdapter();
        binding.list.setAdapter(adapter);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        adapter.setOnItemClickListener((adapter, view, position) -> {
            User data = (User) adapter.getItem(position);
            startActivityExtraId(UserDetailActivity.class, data.getId());
        });
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        DefaultRepository
                .getInstance()
                .searchUsers(query)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<ListResponse<User>>() {
                    @Override
                    public void onSucceeded(ListResponse<User> data) {
                        if (CollectionUtils.isEmpty(data.getData().getData())) {
                            SuperViewUtil.show(binding.placeholderView);
                            SuperViewUtil.gone(binding.list);
                        } else {
                            SuperViewUtil.gone(binding.placeholderView);
                            SuperViewUtil.show(binding.list);

                            ArrayList<BaseMultiItemEntity> results = new ArrayList<>();
                            results.addAll(data.getData().getData());
                            adapter.setNewInstance(results);
                        }
                    }
                });
    }
}
