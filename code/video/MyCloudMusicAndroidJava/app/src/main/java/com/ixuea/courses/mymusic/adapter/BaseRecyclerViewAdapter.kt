package com.ixuea.courses.mymusic.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ixuea.courses.mymusic.component.download.listener.OnItemClickListener

/**
 * 通用RecyclerViewAdapter
 * 主要实现了一些通用方法
 */
abstract class BaseRecyclerViewAdapter<D, VH : BaseRecyclerViewAdapter.ViewHolder<*>>(
    protected val context: Context,
) : RecyclerView.Adapter<VH>() {
    val inflater: LayoutInflater = LayoutInflater.from(context)

    /**
     * 数据列表
     */
    val datum: MutableList<D> = ArrayList()

    private var onItemClickListener: OnItemClickListener? = null

    override fun getItemCount(): Int {
        return datum.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        onItemClickListener?.let { listener ->
            holder.itemView.setOnClickListener {
                listener.onItemClick(holder, holder.layoutPosition)
            }
        }
    }

    fun getData(position: Int): D {
        return datum[position]
    }

    /**
     * 设置数据
     */
    fun setDatum(datum: List<D>?) {
        this.datum.clear()
        if (datum != null) {
            this.datum.addAll(datum)
        }

        notifyDataSetChanged()
    }

    /**
     * 删除指定位置的数据
     */
    fun removeData(position: Int) {
        datum.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * 清除数据
     */
    fun clearData() {
        datum.clear()
        notifyDataSetChanged()
    }

    fun addData(index: Int, data: List<D>) {
        datum.addAll(index, data)
        notifyDataSetChanged()
    }

    /**
     * 添加数据
     */
    fun addData(data: D) {
        datum.add(data)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    /**
     * 通用VH
     */
    abstract class ViewHolder<D>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         * 绑定数据
         */
        open fun bind(data: D) = Unit

        fun getContext(): Context {
            return itemView.context
        }
    }
}
