package com.ixuea.courses.mymusic.component.discovery.activity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.discovery.adapter.CustomDiscoveryAdapter;
import com.ixuea.courses.mymusic.component.discovery.model.event.SortChangedEvent;
import com.ixuea.courses.mymusic.component.discovery.model.ui.CustomDiscoveryItem;
import com.ixuea.courses.mymusic.databinding.ActivityCustomDiscoveryBinding;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.superui.util.SuperRecyclerViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

/**
 * 自定义发现界面
 * <p>
 * 长按item进入拖拽排序
 */
public class CustomDiscoveryActivity extends BaseTitleActivity<ActivityCustomDiscoveryBinding> {

    private CustomDiscoveryAdapter adapter;
    private ItemTouchHelper touchHelper;
    private boolean useDefaultSort;

    @Override
    protected void initViews() {
        super.initViews();
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list, false);
    }

    @Override
    protected void initDatum() {
        super.initDatum();

        initListDrag();

        adapter = new CustomDiscoveryAdapter(getHostActivity(), touchHelper);
        binding.list.setAdapter(adapter);

        loadData();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        binding.resetDefaultSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //不考虑排序，用默认添加顺序
                useDefaultSort = true;

                loadData();
            }
        });
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        List<CustomDiscoveryItem> datum = Arrays.asList(
                createItem(
                        Constant.STYLE_BANNER,
                        R.string.top_banner
                ), createItem(
                        Constant.STYLE_BUTTON,
                        R.string.quick_button
                ), createItem(
                        Constant.STYLE_SHEET,
                        R.string.recommend_sheet
                ), createItem(
                        Constant.STYLE_SONG,
                        R.string.recommend_song
                )
        );

        Collections.sort(datum);

        adapter.setDatum(datum);
    }

    private CustomDiscoveryItem createItem(int style, int title) {
        return new CustomDiscoveryItem(style,
                title,
                useDefaultSort ? style : sp.getSort(style)
        );
    }

    /**
     * 初始化列表拖拽排序功能
     * <p>
     * 滑动删除也类似，详细的在《高级Android就业班》课程讲解
     */
    private void initListDrag() {

        //实现拖拽排序
        touchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            /**
             * 获取移动参数，主要就是告诉他是否开启滑动，什么方向可以滑动
             * @param recyclerView
             * @param viewHolder
             * @return
             */
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //    第一个参数控制拖拽，第二个参数控制滑动
                //    开启了上下拖拽
                //第一个参数是拖拽的配置，传入0等于禁用，第二个就是滑动方向了，可以用|同时开启两个方向。
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
            }

            /**
             * 当拖拽条目时，回调
             * @param recyclerView
             * @param viewHolder
             * @param target
             * @return
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //如果条目类型不同，就不交换，比如上面添加了Header
                if (viewHolder.getItemViewType() != target.getItemViewType()) {
                    return false;
                }

                //改变集合真实数据位置
                int sourcePosition = viewHolder.getLayoutPosition();
                int targetPosition = target.getLayoutPosition();
                Collections.swap(adapter.getDatum(), sourcePosition, targetPosition);

                //通知adapter,这两个位置的item交换位置，这样界面就能看到交换效果
                adapter.notifyItemMoved(sourcePosition, targetPosition);

                //返回true，表示移动成功
                return true;
            }

            /**
             * 当前侧滑时回调
             * @param viewHolder
             * @param direction
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        touchHelper.attachToRecyclerView(binding.list);
    }

    /**
     * 返回要显示的菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveClick();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveClick() {
        Timber.d("saveClick");
        //保存类型和对应的位置
        CustomDiscoveryItem data;
        for (int i = 0; i < adapter.getDatum().size(); i++) {
            data = adapter.getData(i);
            sp.setSort(data.getStyle(), i);
        }

        finish();

        EventBus.getDefault().post(new SortChangedEvent());
    }
}