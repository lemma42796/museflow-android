package com.ixuea.courses.mymusic.component.music.activity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.music.fragment.MusicSortDialogFragment;
import com.ixuea.courses.mymusic.component.music.model.event.ScanLocalMusicCompleteEvent;
import com.ixuea.courses.mymusic.component.sheet.adapter.SongAdapter;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.databinding.ActivityLocalMusicBinding;
import com.ixuea.superui.toast.SuperToast;
import com.ixuea.superui.util.SuperRecyclerViewUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 本地音乐界面
 */
public class LocalMusicActivity extends BaseTitleActivity<ActivityLocalMusicBinding> {

    private SongAdapter adapter;
    private MenuItem editMenuItem;

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void initViews() {
        super.initViews();
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        adapter = new SongAdapter(R.layout.item_song, 1, getSupportFragmentManager());
        binding.list.setAdapter(adapter);

        loadData();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> a, @NonNull View view, int position) {
                if (adapter.isEditing()) {
                    //编辑模式
                    if (adapter.isSelected(position)) {
                        //选中

                        //设置为没选中
                        adapter.setSelected(position, false);
                    } else {
                        //没选中

                        //设置为选中
                        adapter.setSelected(position, true);
                    }

                    //显示按钮状态
                    showButtonStatus();
                } else {
                    play(position);
                }

            }
        });

        //选择按钮点击事件
        binding.select.setOnClickListener(v -> {
            selectClick();
        });

        //删除按钮点击事件
        binding.delete.setOnClickListener(v -> {
            deleteClick();
        });
    }

    private void selectClick() {
        boolean selected = isSelected();

        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (selected) {
                //有选中

                //取消选中
                adapter.setSelected(i, false);
            } else {
                //没有选中

                //选中所有
                adapter.setSelected(i, true);
            }
        }

        //刷新按钮状态
        showButtonStatus();
    }

    private void deleteClick() {
        //获取选中的索引
        List<Integer> selectedIndexes = adapter.getSelectedIndexes();

        //保存要删除对象的id
        List<String> deleteIndexes = new ArrayList<>();

        Song data = null;
        for (int index : selectedIndexes) {
            data = adapter.getItem(index);

            //将要删除对象的id保存到列表中
            deleteIndexes.add(data.getId());
        }

        //删除数据源
        List<Song> datum = adapter.getData();

        Iterator<Song> iterator = datum.iterator();
        while (iterator.hasNext()) {
            Song song = iterator.next();
            if (deleteIndexes.contains(song.getId())) {
                //从列表中删除
                iterator.remove();

                //从数据库中删除
                getOrm().deleteSongById(song.getId());
            }
        }

        //通知数据改变了
        adapter.notifyDataSetChanged();

        //退出编辑模式
        exitEditMode();
    }

    /**
     * 是否有选中
     *
     * @return
     */
    private boolean isSelected() {
        return adapter.getSelectedIndexes().size() > 0;
    }

    /**
     * 刷新按钮状态
     */
    private void showButtonStatus() {
        if (isSelected()) {
            //有选中

            binding.select.setText(R.string.cancel_select_all);
            binding.delete.setEnabled(true);
        } else {
            //没有选中
            defaultButtonStatus();
        }
    }

    private void play(int position) {
        //获取当前点击音乐
        Song data = (Song) adapter.getItem(position);

        //设置播放列表
        getMusicListManager().setDatum(adapter.getData());

        //播放点击的音乐
        getMusicListManager().play(data);

        //跳转到播放界面
        startMusicPlayerActivity();
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);

        List<Song> datum = getOrm().queryLocalMusic(sp.getLocalMusicSortIndex());

        if (datum.size() > 0) {
            //有本地音乐

            //设置到适配器
            adapter.setNewInstance(datum);
        } else {
            //没有本地音乐

            //跳转到扫描本地音乐界面
            toScanLocalMusic();
        }
    }

    private void toScanLocalMusic() {
        startActivity(ScanLocalMusicActivity.class);
    }

    /**
     * 扫描本地音乐完成事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanMusicCompleteEvent(ScanLocalMusicCompleteEvent event) {
        loadData();
    }

    /**
     * 返回菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_local_music, menu);

        //查找编辑按钮
        editMenuItem = menu.findItem(R.id.edit);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 菜单点击了
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                //批量编辑按钮点击了
                onEditClick();
                break;
            case R.id.scan_local_music:
                //扫描本地音乐
                toScanLocalMusic();
                break;
            case R.id.sort:
                //排序
                showSortDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 编辑按钮点击了
     */
    private void onEditClick() {
        //判断是否有数据
        if (adapter.getItemCount() == 0) {
            SuperToast.show(R.string.no_local_music);
            return;
        }

        if (adapter.isEditing()) {
            //在编辑模式下

            //退出编辑模式
            exitEditMode();
        } else {
            //没有进入编辑模式

            //进入编辑模式

            //设置编辑按钮标题
            editMenuItem.setTitle(R.string.cancel_edit);

            //显示编辑容器
            binding.controlContainer.setVisibility(View.VISIBLE);

            //适配器进入编辑状态
            adapter.setEditing(true);
        }
    }

    /**
     * 退出编辑模式
     */
    private void exitEditMode() {
        //设置编辑按钮标题
        editMenuItem.setTitle(R.string.batch_edit);

        //隐藏编辑容器
        binding.controlContainer.setVisibility(View.GONE);

        //重置编辑按钮状态
        defaultButtonStatus();

        //适配器退出编辑模式
        adapter.setEditing(false);
    }

    /**
     * 重置编辑按钮状态
     */
    private void defaultButtonStatus() {
        //选择按钮标题
        binding.select.setText(R.string.select_all);

        //禁用删除按钮
        binding.delete.setEnabled(false);
    }

    /**
     * 显示排序对话框
     */
    private void showSortDialog() {
        MusicSortDialogFragment.show(getSupportFragmentManager(), sp.getLocalMusicSortIndex(), (dialog, which) -> {
            //关闭对话框
            dialog.dismiss();

            //保存排序索引
            sp.setLocalMusicSortIndex(which);

            loadData();
        });
    }

    @Override
    public void onBackPressed() {
        if (adapter.isEditing()) {
            exitEditMode();
            return;
        }
        super.onBackPressed();
    }
}