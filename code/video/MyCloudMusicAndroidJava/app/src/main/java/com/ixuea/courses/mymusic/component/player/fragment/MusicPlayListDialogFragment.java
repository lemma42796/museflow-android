package com.ixuea.courses.mymusic.component.player.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.player.adapter.MusicPlayListAdapter;
import com.ixuea.courses.mymusic.databinding.FragmentDialogAudioPlayListBinding;
import com.ixuea.courses.mymusic.fragment.BaseViewModelBottomSheetDialogFragment;
import com.ixuea.courses.mymusic.util.PlayListUtil;

/**
 * 播放列表对话框
 */
public class MusicPlayListDialogFragment extends BaseViewModelBottomSheetDialogFragment<FragmentDialogAudioPlayListBinding> {

    private MusicPlayListAdapter adapter;

    public static MusicPlayListDialogFragment newInstance() {

        Bundle args = new Bundle();

        MusicPlayListDialogFragment fragment = new MusicPlayListDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 显示
     *
     * @param fragmentManager
     */
    public static void show(FragmentManager fragmentManager) {
        MusicPlayListDialogFragment fragment = newInstance();
        fragment.show(fragmentManager, "MusicPlayListDialogFragment");
    }

    @Override
    protected void initViews() {
        super.initViews();

        //固定尺寸
        binding.list.setHasFixedSize(true);

        //分割线
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), RecyclerView.VERTICAL);
        binding.list.addItemDecoration(decoration);

    }

    @Override
    protected void initDatum() {
        super.initDatum();
        adapter = new MusicPlayListAdapter(R.layout.item_play_list, getMusicListManager());
        binding.list.setAdapter(adapter);

        adapter.setList(getMusicListManager().getDatum());

        showLoopModel();

        showCount();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //删除所有按钮点击
        binding.deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭对话框
                dismiss();

                //删除全部音乐
                getMusicListManager().deleteAll();
            }
        });

        //item中子控件点击
        //删除按钮点击
        adapter.addChildClickViewIds(R.id.delete);

        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                //由于这里只有一个按钮点击
                //所以可以不判断
                if (R.id.delete == view.getId()) {
                    //删除按钮点击
                    removeItem(position);
                }
            }
        });

        //循环模式点击
        binding.loopModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更改循环模式
                getMusicListManager().changeLoopModel();

                //显示循环模式
                showLoopModel();

            }
        });

        //设置item点击事件
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //关闭dialog
                //可以根据具体的业务逻辑来决定是否关闭
                dismiss();

                //播放点击的这首音乐
                getMusicListManager().play(getMusicListManager().getDatum().get(position));
            }
        });

        //实现滑动删除
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            /**
             * 获取移动参数，主要就是告诉他是否开启滑动，什么方向可以滑动
             *
             * @param recyclerView
             * @param viewHolder
             * @return
             */
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //第一个参数控制拖拽，第二个参数控制滑动
                //开启了从右边滑动到左边
                return makeMovementFlags(0, ItemTouchHelper.LEFT);
            }

            /**
             * 当拖拽条目时，回调
             *
             * @param recyclerView
             * @param viewHolder
             * @param target
             * @return
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            /**
             * 当前侧滑时回调
             *
             * @param viewHolder
             * @param direction
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem(viewHolder.getLayoutPosition());
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }
        });
        touchHelper.attachToRecyclerView(binding.list);
    }

    private void removeItem(int position) {
        adapter.removeAt(position);

        //从列表管理器中删除
        getMusicListManager().delete(position);

        showCount();
    }

    /**
     * 显示循环模式
     */
    private void showLoopModel() {
        PlayListUtil.showLoopModel(getMusicListManager().getLoopModel(), binding.loopModel);
    }

    private void showCount() {
        binding.count.setText(String.format("(%d)", getMusicListManager().getDatum().size()));
    }
}
