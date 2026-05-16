package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ixuea.courses.mymusic.component.download.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用RecyclerViewAdapter
 * 主要实现了一些通用方法
 */
public abstract class BaseRecyclerViewAdapter<D, VH extends BaseRecyclerViewAdapter.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected final Context context;
    private final LayoutInflater inflater;
    /**
     * 数据列表
     */
    private List<D> datum = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public BaseRecyclerViewAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return datum.size();
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        //处理item点击监听器
        if (onItemClickListener != null) {
            //给itemView设置点击事件
            holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(holder, holder.getLayoutPosition()));
        }
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public D getData(int position) {
        return datum.get(position);
    }

    /**
     * 设置数据
     *
     * @param datum
     */
    public void setDatum(List<D> datum) {
        if (datum != null && datum.size() > 0) {
            this.datum.clear();
            this.datum.addAll(datum);

            //通知数据改变了
            notifyDataSetChanged();
        }
    }

    public List<D> getDatum() {
        return datum;
    }

    /**
     * 删除指定位置的数据
     *
     * @param position
     */
    public void removeData(int position) {
        datum.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 清除数据
     */
    public void clearData() {
        datum.clear();
        notifyDataSetChanged();
    }

    public void addData(int index, List<D> data) {
        this.datum.addAll(index, data);
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     *
     * @param data
     */
    public void addData(D data) {
        //添加数据
        this.datum.add(data);

        //刷新数据
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 通用VH
     *
     * @param <D>
     */
    public abstract static class ViewHolder<D> extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        /**
         * 绑定数据
         *
         * @param data
         */
        public void bind(D data) {

        }

        public Context getContext() {
            return itemView.getContext();
        }
    }
}
