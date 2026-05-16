package com.ixuea.courses.mymusic.component.feed.fragment;

import static autodispose2.AutoDispose.autoDisposable;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.google.common.collect.Lists;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.feed.activity.PublishFeedActivity;
import com.ixuea.courses.mymusic.component.feed.adapter.FeedAdapter;
import com.ixuea.courses.mymusic.component.feed.model.Feed;
import com.ixuea.courses.mymusic.component.feed.model.event.FeedChangedEvent;
import com.ixuea.courses.mymusic.component.location.activity.PreviewLocationActivity;
import com.ixuea.courses.mymusic.component.location.model.PreviewLocationPagedData;
import com.ixuea.courses.mymusic.component.user.activity.UserDetailActivity;
import com.ixuea.courses.mymusic.component.user.model.event.UserDetailEvent;
import com.ixuea.courses.mymusic.databinding.FragmentFeedBinding;
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment;
import com.ixuea.courses.mymusic.model.response.ListResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.wanglu.photoviewerlibrary.PhotoViewer;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 首页-动态界面
 */
public class FeedFragment extends BaseViewModelFragment<FragmentFeedBinding> implements FeedAdapter.FeedListener {

    private String userId;
    private FeedAdapter adapter;

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        userId = extraString(Constant.USER_ID);

        adapter = new FeedAdapter(R.layout.item_feed);
        binding.list.setAdapter(adapter);

        loadData();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        adapter.setListener(this);

        binding.primary.setOnClickListener(v -> loginAfter(() -> startActivity(PublishFeedActivity.class)));

        adapter.addChildClickViewIds(R.id.position);
        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Feed data = (Feed) adapter.getItem(position);
                if (view.getId() == R.id.position) {
                    //预览位置
                    PreviewLocationActivity.start(getHostActivity(), new PreviewLocationPagedData(
                            data.getPosition(),
                            data.getAddress(),
                            data.getLongitude(),
                            data.getLatitude()
                    ));
                }
            }
        });
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        DefaultRepository.getInstance()
                .feeds(userId)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<ListResponse<Feed>>() {
                    @Override
                    public void onSucceeded(ListResponse<Feed> data) {
                        adapter.setNewInstance(data.getData().getData());
                    }
                });
    }

    public static FeedFragment newInstance() {
        return newInstance(null);
    }

    public static FeedFragment newInstance(String userId) {

        Bundle args = new Bundle();
        if (StringUtils.isNotBlank(userId)) {
            args.putString(Constant.ID, userId);
        }

        FeedFragment fragment = new FeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 动态图片点击了
     *
     * @param rv
     * @param results
     * @param index
     */
    @Override
    public void onImageClick(RecyclerView rv, List<String> results, int index) {
        //将List转为ArrayList
        //因为图片框架需要的是ArrayList
        //最好就是一步转换为ArrayList
        //避免浪费更多的资源
        ArrayList<String> imagesUris = Lists.newArrayList(results);

        //PhotoViewer框架是Kotlin写的
        //静态的方法要通过INSTANCE字段使用
        PhotoViewer.INSTANCE

                //设置图片数据
                .setData(imagesUris)

                //设置当前位置
                .setCurrentPage(index)

                //设置图片控件容器
                //他需要容器的目的是
                //显示缩放动画
                .setImgContainer(rv)

                //设置图片加载回调
                .setShowImageViewInterface((imageView, url) -> {
                    ImageUtil.show(getHostActivity(), imageView, url);
                })

                //启动界面
                .start(this);
    }

    /**
     * 点赞，评论，里面的用户点击事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userDetailEvent(UserDetailEvent event) {
        UserDetailActivity.startWithId(getHostActivity(), event.getData());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void feedChangedEvent(FeedChangedEvent event) {
        loadData();
    }
}
