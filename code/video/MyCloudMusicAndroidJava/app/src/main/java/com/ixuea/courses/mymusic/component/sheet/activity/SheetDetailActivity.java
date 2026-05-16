package com.ixuea.courses.mymusic.component.sheet.activity;

import static autodispose2.AutoDispose.autoDisposable;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.comment.activity.CommentActivity;
import com.ixuea.courses.mymusic.component.login.activity.LoginHomeActivity;
import com.ixuea.courses.mymusic.component.sheet.adapter.SongAdapter;
import com.ixuea.courses.mymusic.component.sheet.model.Sheet;
import com.ixuea.courses.mymusic.component.sheet.model.event.SheetChangedEvent;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.component.user.activity.UserDetailActivity;
import com.ixuea.courses.mymusic.databinding.ActivitySheetDetailBinding;
import com.ixuea.courses.mymusic.databinding.HeaderSheetDetailBinding;
import com.ixuea.courses.mymusic.model.Base;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.ResourceUtil;
import com.ixuea.superui.toast.SuperToast;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 歌单详情界面
 */
public class SheetDetailActivity extends BaseTitleActivity<ActivitySheetDetailBinding> implements View.OnClickListener {
    private String id;
    private Sheet data;
    private SongAdapter adapter;
    private HeaderSheetDetailBinding headerBinding;
    private MenuItem deleteMenuItem;

    @Override
    protected void initViews() {
        super.initViews();
        //状态栏文字白色
        QMUIStatusBarHelper.setStatusBarDarkMode(this);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            //通过隐士意图打开的，可以是应用中，也可以是在网页打开的
            Uri data = getIntent().getData();
            if (data != null) {
                id = data.getQueryParameter(Constant.ID);
            }
        } else {
            //使用重构后的方法
            id = extraId();
        }

        //创建适配器
        adapter = new SongAdapter(R.layout.item_song);

        //添加头部
        adapter.addHeaderView(createHeaderView());

        //设置适配器
        binding.list.setAdapter(adapter);

        loadData();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //收藏按钮单击事件
        headerBinding.collect.setOnClickListener(this);

        //设置item点击事件
        adapter.setOnItemClickListener((adapter, view, position) -> play(position));
    }

    /**
     * 创建头部
     *
     * @return
     */
    private View createHeaderView() {
        headerBinding = HeaderSheetDetailBinding.inflate(LayoutInflater.from(getHostActivity()));

        //播放全部点击
        headerBinding.controlContainer.setOnClickListener(v -> {
            //播放全部点击
            play(0);
        });

        //用户容器点击
        headerBinding.userContainer.setOnClickListener(v -> {
            //用户容器点击
            startActivityExtraId(UserDetailActivity.class, data.getUser().getId());
        });

        headerBinding.commentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentActivity.startWithSheetId(getHostActivity(), data.getId());
            }
        });

        //返回view
        return headerBinding.getRoot();
    }

    private void play(int position) {
        //获取当前位置播放的音乐
        Song data = adapter.getItem(position);

        //把当前歌单所有音乐设置到播放列表
        getMusicListManager().setDatum(adapter.getData());

        //播放当前音乐
        getMusicListManager().play(data);

        startMusicPlayerActivity();
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        DefaultRepository.getInstance()
                .sheetDetail(id)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<Sheet>>(getHostActivity(), true) {
                    @Override
                    public void onSucceeded(DetailResponse<Sheet> data) {
                        showData(data.getData());
                    }
                });
    }

    private void showData(Sheet data) {
        this.data = data;

        if (data.getSongs() != null && data.getSongs().size() > 0) {
            //有音乐才设置

            //设置数据
            adapter.setNewInstance(data.getSongs());
        } else {
            adapter.setNewInstance(new ArrayList<>());
        }

        if (StringUtils.isBlank(data.getIcon())) {
            //图片为空

            //使用默认图片
            headerBinding.icon.setImageResource(R.drawable.placeholder);

            setDefaultColor();
        } else {
            //有图片

            //获取图片绝对路径
            String uri = ResourceUtil.resourceUri(data.getIcon());

            //加载
            GlidePalette<Drawable> glidePalette = GlidePalette

                    //再把地址传到GlidePalette
                    .with(uri)

                    //使用VIBRANT颜色样板
                    .use(GlidePalette.Profile.VIBRANT)

                    //设置到控件背景
//                    .intoBackground(toolbar, GlidePalette.Swatch.RGB)
//                    .intoBackground(headerBinding.header, GlidePalette.Swatch.RGB)

                    //设置回调
                    //用回调的目的是
                    //要设置状态栏和导航栏
                    .intoCallBack(new BitmapPalette.CallBack() {
                        @Override
                        public void onPaletteLoaded(@Nullable Palette palette) {
                            //获取 有活力 的颜色
                            Palette.Swatch swatch = palette.getVibrantSwatch();

                            //可能没有值所以要判断
                            if (swatch != null) {
                                //获取颜色的rgb
                                int rgb = swatch.getRgb();

                                setHeaderBackground(rgb);
                            } else {
                                setDefaultColor();
                            }
                        }
                    })

                    //淡入
                    .crossfade(true);

            //使用Glide
            Glide.with(getHostActivity())

                    //加载图片
                    .load(uri)

                    //加载完成监听器
                    .listener(glidePalette)

                    //将图片设置到图片控件
                    .into(headerBinding.icon);

        }

        //显示标题
        headerBinding.title.setText(data.getTitle());

        //头像
        ImageUtil.showAvatar(getHostActivity(), headerBinding.avatar, data.getUser().getIcon());

        //昵称
        headerBinding.nickname.setText(data.getUser().getNickname());

        //评论数
        headerBinding.commentCount.setText(String.valueOf(data.getCommentsCount()));

        //音乐数
        headerBinding.count.setText(getResources().getString(R.string.music_count, data.getSongs().size()));

        //显示收藏状态
        showCollectStatus();

        headerBinding.commentCount.post(() -> {
            //显示删除按钮状态
            deleteMenuItem.setVisible(data.getUser().getId().equals(sp.getUserId()));
        });
    }

    /**
     * 显示收藏状态
     */
    private void showCollectStatus() {
        if (data.isCollect()) {
            //收藏了

            //将按钮文字改为取消
            headerBinding.collect.setText(getResources()
                    .getString(R.string.cancel_collect, data.getCollectsCount()));

            //弱化取消收藏按钮
            //因为我们的本质是想让用户收藏歌单
            //所以去掉背景
            headerBinding.collect.setBackground(null);

            //设置文字颜色为灰色
            headerBinding.collect.setTextColor(getResources().getColor(R.color.black80));
        } else {
            //没有收藏

            //将按钮文字改为收藏
            headerBinding.collect.setText(getResources().getString(R.string.collect, data.getCollectsCount()));

            //设置按钮颜色为主色调
            headerBinding.collect.setBackgroundColor(getResources().getColor(R.color.primary));

            //将文字颜色设置为白色
            headerBinding.collect.setTextColor(getResources().getColor(R.color.white));
        }
    }

    /**
     * 创建菜单方法
     * 有点类似显示布局要在onCreate方法中
     * 使用setContentView设置布局
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加载按钮布局
        getMenuInflater().inflate(R.menu.menu_sheet_detail, menu);

        //查找按钮
        deleteMenuItem = menu.findItem(R.id.delete);

        //隐藏
        deleteMenuItem.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 菜单点击了回调
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //获取点击的菜单id
        int id = item.getItemId();

        if (R.id.search == id) {
            //搜索按钮点击了
            return true;
        } else if (R.id.sort == id) {
            //排序按钮点击了
            return true;
        } else if (R.id.delete == id) {
            //删除按钮点击了
            deleteSheet();

            return true;
        } else if (R.id.report == id) {
            //举报按钮点击了
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * TODO 删除歌单
     */
    private void deleteSheet() {

    }

    private void setDefaultColor() {
        setHeaderBackground(getColor(R.color.primary));
    }

    /**
     * 设置状态栏，导航栏，toolbar，header背景
     *
     * @param color
     */
    private void setHeaderBackground(int color) {
        setStatusBarColor(color);

        toolbar.setBackgroundColor(color);
        headerBinding.header.setBackgroundColor(color);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.collect:
                //收藏歌单按钮点击了
                processCollectClick();
                break;
        }
    }

    /**
     * 处理收藏和取消收藏逻辑
     */
    private void processCollectClick() {
        if (!sp.isLogin()) {
            startActivity(LoginHomeActivity.class);
        }

        if (data.isCollect()) {
            //已经收藏了

            //取消收藏
            DefaultRepository.getInstance()
                    .deleteCollect(data.getId())
                    .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe(new HttpObserver<DetailResponse<Base>>() {
                        @Override
                        public void onSucceeded(DetailResponse<Base> d) {
                            //弹出提示
                            SuperToast.success(R.string.cancel_success);

                            //重新加载数据
                            //目的是显示新的收藏状态
                            //loadData();

                            //取消收藏成功
                            data.setCollectId(null);

                            //收藏数-1
                            data.setCollectsCount(data.getCollectsCount() - 1);

                            //刷新状态
                            showCollectStatus();

                            //发布歌单改变了事件
                            publishSheetChangedEvent();
                        }
                    });
        } else {
            //没有收藏

            //收藏
            DefaultRepository.getInstance()
                    .collect(data.getId())
                    .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe(new HttpObserver<DetailResponse<Base>>() {
                        @Override
                        public void onSucceeded(DetailResponse<Base> d) {
                            //弹出提示
                            SuperToast.success(R.string.collection_success);

                            //重新加载数据
                            //目的是显示新的收藏状态
                            //loadData();

                            //收藏状态变更后
                            //可以重新调用歌单详情界面接口
                            //获取收藏状态
                            //但对于收藏来说
                            //收藏数可能没那么重要
                            //所以不用及时刷新
                            data.setCollectId("1");

                            //收藏数+1
                            data.setCollectsCount(data.getCollectsCount() + 1);

                            //刷新状态
                            showCollectStatus();

                            //发布歌单改变了事件
                            publishSheetChangedEvent();
                        }
                    });
        }
    }

    /**
     * 发布歌单改变了事件
     */
    private void publishSheetChangedEvent() {
        EventBus.getDefault().post(new SheetChangedEvent());
    }
}