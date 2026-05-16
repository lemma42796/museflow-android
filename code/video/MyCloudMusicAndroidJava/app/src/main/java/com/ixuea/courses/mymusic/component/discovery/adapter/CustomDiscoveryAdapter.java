package com.ixuea.courses.mymusic.component.discovery.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.component.discovery.model.ui.CustomDiscoveryItem;
import com.ixuea.courses.mymusic.databinding.ItemCustomDiscoveryBinding;

/**
 * 自定义发现界面适配器
 */
public class CustomDiscoveryAdapter extends BaseRecyclerViewAdapter<CustomDiscoveryItem, CustomDiscoveryAdapter.ViewHolder> {
    private final ItemTouchHelper touchHelper;

    public CustomDiscoveryAdapter(Context context, ItemTouchHelper touchHelper) {
        super(context);
        this.touchHelper = touchHelper;
    }

    /**
     * 创建ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCustomDiscoveryBinding.inflate(getInflater(), parent, false));
    }

    /**
     * 绑定数据
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull CustomDiscoveryAdapter.ViewHolder holder, int position) {
        holder.bind(getData(position), position);
    }

    /**
     * ViewHolder
     * 保存Item相关信息
     */
    public class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        private final ItemCustomDiscoveryBinding binding;

        public ViewHolder(ItemCustomDiscoveryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.more.setOnTouchListener((v, event) -> {
                touchHelper.startDrag(this);
                return true;
            });
        }

        public void bind(CustomDiscoveryItem data, int position) {
            binding.title.setText(data.getTitle());
        }
    }
}
