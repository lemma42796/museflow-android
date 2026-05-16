package com.ixuea.courses.mymusic.component.user.activity;

import static autodispose2.AutoDispose.autoDisposable;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.chat.activity.ChatActivity;
import com.ixuea.courses.mymusic.component.login.model.event.LoginStatusChangedEvent;
import com.ixuea.courses.mymusic.component.profile.activity.ProfileActivity;
import com.ixuea.courses.mymusic.component.user.adapter.UserDetailAdapter;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.component.user.model.event.UserChangedEvent;
import com.ixuea.courses.mymusic.databinding.ActivityUserDetailBinding;
import com.ixuea.courses.mymusic.model.BaseId;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.superui.util.SuperDelayUtil;
import com.ixuea.superui.util.SuperViewUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 用户详情界面
 */
public class UserDetailActivity extends BaseTitleActivity<ActivityUserDetailBinding> {

    private String id;
    private String nickname;
    private User data;
    private MenuItem editMenuItem;
    private UserDetailAdapter adapter;
    private long startTime;

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void initViews() {
        super.initViews();
        //启动骨架控件动画，因为使用了多个骨架控件，现在有个小问题就是动画会不一样
        //如果要解决该问题，目前能想到的就是自定义该框架，提供一个方法，支持传入多个控件
        //内部统一用一个动画，这样就没有该问题
        //提示：使用了骨架屏后，肯定要耗费一点性能，不过目前大部分手机配置也很高了
        // 也不在乎这点消耗，主要是能提供用户体系那
        // 当然例如像带屏幕的温度传感器这类设备，如果是充电或者用电池这种，那能耗更重要
        binding.userSkeleton.startShimmerAnimation();
        binding.indicatorSkeleton.startShimmerAnimation();
        binding.listSkeleton.startShimmerAnimation();
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        id = extraId();

        //判断id是否为空
        if (TextUtils.isEmpty(id)) {
            //如果为空就给他设置一个默认值
            //这是和服务端协商好的
            id = "-1";
        }

        //获取昵称
        nickname = extraString(Constant.NICKNAME);

        loadData();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        binding.follow.setOnClickListener(v -> loginAfter(() -> {
            followClick();
        }));

        binding.sendMessage.setOnClickListener(v -> loginAfter(() -> startActivityExtraId(ChatActivity.class, data.getId())));
    }

    private void followClick() {
        if (data.isFollowing()) {
            //已经关注了

            //取消关注
            DefaultRepository.getInstance()
                    .deleteFollow(data.getId())
                    .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe(new HttpObserver<DetailResponse<BaseId>>() {
                        @Override
                        public void onSucceeded(DetailResponse<BaseId> d) {
                            //取消关注成功
                            data.setFollowing(null);

                            //刷新关注状态
                            showFollowStatus();
                        }
                    });
        } else {
            //没有关注

            //关注
            DefaultRepository.getInstance()
                    .follow(data.getId())
                    .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe(new HttpObserver<DetailResponse<BaseId>>() {
                        @Override
                        public void onSucceeded(DetailResponse<BaseId> d) {
                            //关注成功
                            data.setFollowing("1");

                            //刷新关注状态
                            showFollowStatus();
                        }
                    });
        }
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);

        //记录开始时间，目的是始终要当前界面最低延迟1秒在显示内容
        //这样用户才能看到骨架屏
        startTime = System.currentTimeMillis();

        DefaultRepository.getInstance()
                .userDetail(id, nickname)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<User>>() {
                    @Override
                    public void onSucceeded(DetailResponse<User> data) {
                        //结束时间
                        long endTime = System.currentTimeMillis();

                        //网络请求消耗的时间
                        long consumeTime = endTime - startTime;
                        if (consumeTime < 1000) {
                            //小于1秒钟，要延迟
                            SuperDelayUtil.delay(1000 - consumeTime, () -> showData(data.getData()));
                        } else {
                            showData(data.getData());
                        }
                    }
                });
    }

    /**
     * 显示数据
     *
     * @param data
     */
    private void showData(User data) {
        this.data = data;

        if (binding.userSkeleton.getParent() != null) {
            //停止骨架动画
            binding.userSkeleton.stopShimmerAnimation();
            binding.indicatorSkeleton.stopShimmerAnimation();
            binding.listSkeleton.stopShimmerAnimation();

            //移除骨架布局
            SuperViewUtil.removeFromParent(binding.userSkeleton);
            SuperViewUtil.removeFromParent(binding.indicatorSkeleton);
            SuperViewUtil.removeFromParent(binding.listSkeleton);

            //显示内容容器
            SuperViewUtil.show(binding.userContainer);
            SuperViewUtil.show(binding.tab);
            SuperViewUtil.show(binding.list);
        }

        ImageUtil.showAvatar(getHostActivity(), binding.icon, data.getIcon());
        binding.nickname.setText(data.getNickname());

        String info = getResources().getString(R.string.user_friend_info,
                data.getFollowingsCount(),
                data.getFollowersCount());
        binding.info.setText(info);

        //显示关注状态
        showFollowStatus();

        //显示编辑用户信息按钮状态
        editMenuItem.setVisible(data.getId().equals(sp.getUserId()));

        initUI();
    }

    private void initUI() {
        adapter = new UserDetailAdapter(getHostActivity(), getSupportFragmentManager(), id);
        binding.list.setAdapter(adapter);

        adapter.setDatum(Arrays.asList(0, 1, 2));

        //将TabLayout和ViewPager绑定
        binding.tab.setupWithViewPager(binding.list);
    }

    private void showFollowStatus() {
        if (data.getId().equals(sp.getUserId())) {
            //自己

            //隐藏关注按钮，发送消息按钮
            SuperViewUtil.gone(binding.follow);
            SuperViewUtil.gone(binding.sendMessage);
        } else {
            //判断我是否关注了该用户
            SuperViewUtil.show(binding.follow);

            if (sp.isLogin() && data.isFollowing()) {
                //已经关注了
                binding.follow.setText(R.string.cancel_follow);
                binding.follow.setBackgroundResource(R.drawable.shape_second_border_radius_15);
                binding.follow.setTextColor(getColor(R.color.black80));

                //显示发送消息按钮
                SuperViewUtil.show(binding.sendMessage);
            } else {
                //没有关注
                binding.follow.setText(R.string.follow);
                binding.follow.setBackgroundResource(R.drawable.shape_color_primary);
                binding.follow.setTextColor(getColor(R.color.white));

                //隐藏发送消息按钮
                SuperViewUtil.gone(binding.sendMessage);
            }
        }
    }

    /**
     * 返回菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    /**
     * 准备显示按钮
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //查找编辑按钮
        editMenuItem = menu.findItem(R.id.edit);

        //隐藏
        editMenuItem.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 菜单点击了回调
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                startActivity(ProfileActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 根据昵称显示用户详情
     *
     * @param context
     * @param nickname
     */
    public static void startWithNickname(Context context, String nickname) {
        start(context, null, nickname);
    }

    /**
     * 根据用户id显示用户详情
     *
     * @param context
     * @param id
     */
    public static void startWithId(Context context, String id) {
        start(context, id, null);
    }

    /**
     * 启动界面
     *
     * @param context
     * @param id
     * @param nickname
     */
    private static void start(Context context, String id, String nickname) {
        //创建intent
        Intent intent = new Intent(context, UserDetailActivity.class);

        //如果id有值再添加
        if (!TextUtils.isEmpty(id)) {
            intent.putExtra(Constant.ID, id);
        }

        //如果有昵称再添加
        if (!TextUtils.isEmpty(nickname)) {
            intent.putExtra(Constant.NICKNAME, nickname);
        }

        //启动界面
        context.startActivity(intent);
    }

    /**
     * 登录状态改变了事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginStatusChangedEvent(LoginStatusChangedEvent event) {
        loadData();
    }

    /**
     * 用户信息改变了
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userChangedEvent(UserChangedEvent event) {
        loadData();
    }

    @Override
    protected String pageId() {
        return "UserDetail";
    }
}