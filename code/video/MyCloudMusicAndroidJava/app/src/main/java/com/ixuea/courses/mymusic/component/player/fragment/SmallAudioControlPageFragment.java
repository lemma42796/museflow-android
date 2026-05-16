package com.ixuea.courses.mymusic.component.player.fragment;

import android.media.MediaPlayer;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.component.login.model.event.LoginStatusChangedEvent;
import com.ixuea.courses.mymusic.component.player.adapter.SmallAudioControlAdapter;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.databinding.FragmentSmallAudioControlPageBinding;
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment;
import com.ixuea.courses.mymusic.manager.MusicPlayerListener;
import com.ixuea.courses.mymusic.manager.MusicPlayerManager;
import com.ixuea.courses.mymusic.manager.model.event.MusicPlayListChangedEvent;
import com.ixuea.courses.mymusic.service.MusicPlayerService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 小的音频播放控制fragment
 * 就是在主界面底部显示的那个小控制条，可以左右滚动切换音乐
 */
public class SmallAudioControlPageFragment extends BaseViewModelFragment<FragmentSmallAudioControlPageBinding> implements MusicPlayerListener {

    private SmallAudioControlAdapter adapter;
    private MusicPlayerManager musicPlayerManager;

    /**
     * 歌曲滚动监听器
     */
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        /**
         *  页面滚动完成了
         *  用这个方法，会导致第一次进入应用就开始播放了
         *  因为要调用scrollPosition方法滚动到当前音乐，用onPageScrollStateChanged不会有这问题
         * @param position
         */
        @Override
        public void onPageSelected(int position) {
            if (position < 0 || position >= getMusicListManager().getDatum().size()) {
                return;
            }

            Song song = getMusicListManager().getDatum().get(position);
            //播放当前位置的音乐
            getMusicListManager().play(song);
        }

        /**
         * 滚动状态改变了，例如：现在是静止的，开始滚动了；或者现在是滚动，停止滚动了
         * @param state
         */
        @Override
        public void onPageScrollStateChanged(int state) {
            if (ViewPager.SCROLL_STATE_IDLE == state) {
                //空闲状态
                int currentItem = binding.list.getCurrentItem();
                if (currentItem < 0 || currentItem >= getMusicListManager().getDatum().size()) {
                    return;
                }

                //判断黑胶唱片位置对应的音乐是否和现在播放的是一首
                Song song = getMusicListManager().getDatum().get(currentItem);
                Song currentSong = getMusicListManager().getData();
                if (currentSong == null || !currentSong.getId().equals(song.getId())) {
                    //不一样

                    //播放当前位置的音乐
                    getMusicListManager().play(song);
                }
            }
        }
    };

    @Override
    protected void initDatum() {
        super.initDatum();
        //初始化音乐播放管理器
        musicPlayerManager = MusicPlayerService.getMusicPlayerManager(AppContext.getInstance().getApplicationContext());

        adapter = new SmallAudioControlAdapter(getHostActivity(), getChildFragmentManager());
        binding.list.setAdapter(adapter);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        binding.play.setOnClickListener(v -> {
            if (musicPlayerManager.isPlaying()) {
                getMusicListManager().pause();
            } else {
                getMusicListManager().resume();
            }
        });

        // 迷你播放控制器 播放列表按钮点击
        binding.listButton.setOnClickListener(v -> {
            MusicPlayListDialogFragment.show(getChildFragmentManager());
        });
    }

    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        showMusicInfo();

        //添加播放管理器监听器
        musicPlayerManager.addMusicPlayerListener(this);

        binding.list.addOnPageChangeListener(onPageChangeListener);
    }

    /**
     * 界面隐藏了
     */
    @Override
    public void onPause() {
        super.onPause();
        //移除播放管理器监听器
        musicPlayerManager.removeMusicPlayerListener(this);

        binding.list.removeOnPageChangeListener(onPageChangeListener);
    }

    private void showMusicInfo() {
        adapter.setDatum(getMusicListManager().getDatum());

        if (getMusicListManager().getDatum() != null && getMusicListManager().getDatum().size() > 0) {
            //有音乐

            //显示迷你控制器
            binding.container.setVisibility(View.VISIBLE);

            //有音乐
            Song data = getMusicListManager().getData();
            if (data == null) {
                data = getMusicListManager().getDatum().get(0);
            }

            //选中当前播放的音乐
            scrollPosition(data);

            //显示音乐时长
            showDuration(data);

            //显示播放进度
            showProgress(data);

            //显示播放状态
            showMusicPlayStatus();
        } else {
            //隐藏迷你控制器
            binding.container.setVisibility(View.GONE);
        }
    }

    /**
     * 显示时长
     *
     * @param data
     */
    private void showDuration(Song data) {
        //设置到进度条
        binding.progress.setMax((int) data.getDuration());
    }

    /**
     * 显示播放进度
     *
     * @param data
     */
    private void showProgress(Song data) {

        //设置到进度条
        binding.progress.setProgress((int) data.getProgress());
    }

    /**
     * 显示播放状态
     */
    private void showMusicPlayStatus() {
        if (musicPlayerManager.isPlaying()) {
            showPauseStatus();
        } else {
            showPlayStatus();
        }
    }

    /**
     * 显示播放状态
     */
    private void showPlayStatus() {
        //这种图片切换可以使用Selector来实现
        binding.play.setSelected(false);
    }

    /**
     * 显示暂停状态
     */
    private void showPauseStatus() {
        binding.play.setSelected(true);
    }

    /**
     * 滚动到当前音乐位置
     *
     * @param data
     */
    private void scrollPosition(Song data) {
        //选中当前播放的音乐

        //使用post方法是
        //将方法放到了消息循环
        //如果不这样做，在onCreate这样的方法中滚动无效
        //因为这时候列表的数据还没有显示完成
        //具体的这部分我们在《详解View》课程中讲解了
//        binding.list.post(new Runnable() {
//            @Override
//            public void run() {
        int index = getMusicListManager().getDatum().indexOf(data);
        if (index != -1) {
            //滚动到该位置
            binding.list.setCurrentItem(index, false);
        }
//            }
//        });
    }

    @Override
    public void onPaused(Song data) {
        showPlayStatus();
    }

    @Override
    public void onPlaying(Song data) {
        showPauseStatus();
    }

    @Override
    public void onPrepared(MediaPlayer mp, Song data) {
        //显示时长
        showDuration(data);

        //选中当前音乐
        scrollPosition(data);
    }

    @Override
    public void onProgress(Song data) {
        //设置到进度条
        binding.progress.setProgress((int) data.getProgress());
    }

    /**
     * 登录状态改变了事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginStatusChangedEvent(LoginStatusChangedEvent event) {
        //这里只处理了播放控制器用户状态变更了
        //当前就我们这个项目，要处理的位置还有很多
        //例如：如果用户正在下载界面，在其他设备登录了
        //虽然会显示弹窗，用户点击确认后，登录信息也退出了
        //但下载列表的数据不会刷新，因为没有处理
        //如果要处理和现在处理播放控制器类似
        showMusicInfo();
    }

    /**
     * 登录状态改变了事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void musicPlayListChangedEvent(MusicPlayListChangedEvent event) {
        showMusicInfo();
    }
}
