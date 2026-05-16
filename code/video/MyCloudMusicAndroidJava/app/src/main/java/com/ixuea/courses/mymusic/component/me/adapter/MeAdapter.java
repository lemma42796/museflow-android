package com.ixuea.courses.mymusic.component.me.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseActivity;
import com.ixuea.courses.mymusic.component.me.model.event.CreateSheetClickEvent;
import com.ixuea.courses.mymusic.component.me.model.ui.MeGroup;
import com.ixuea.courses.mymusic.component.sheet.model.Sheet;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.superui.util.SuperViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页-我的界面适配器
 */
public class MeAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final LayoutInflater inflater;
    /**
     * 列表数据
     */
    private List<MeGroup> datum = new ArrayList<>();

    public MeAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    /**
     * 返回组数量
     *
     * @return
     */
    @Override
    public int getGroupCount() {
        return datum.size();
    }

    /**
     * 返回子列表数量
     *
     * @param groupPosition
     * @return
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return datum.get(groupPosition).getDatum().size();
    }

    /**
     * 获取组对象
     *
     * @param groupPosition
     * @return
     */
    @Override
    public Object getGroup(int groupPosition) {
        return datum.get(groupPosition);
    }

    /**
     * 获取子元素对象
     *
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return datum.get(groupPosition).getDatum().get(childPosition);
    }

    /**
     * 组id
     *
     * @param groupPosition
     * @return
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * 子元素id
     *
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * childPosition;
    }

    /**
     * 是否有稳定的Id
     * 具体有什么用这里不讲解
     * 详解课程中才会讲解
     *
     * @return
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * 获取组View
     *
     * @param groupPosition
     * @param isExpanded
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //这是ListView这类控件固定写法
        //只有这样写
        //才能用到它的View复用功能
        //RecyclerView在ListView后面才推出的
        //所以RecyclerView的架构更好一点

        //组ViewHolder
        GroupViewHolder viewHolder;

        if (convertView == null) {
            //没有复用的view

            //从view加载一个控件
            convertView = inflater.inflate(R.layout.item_title_small, parent, false);

            //创建一个ViewHolder
            viewHolder = new GroupViewHolder(convertView);

            //将ViewHolder保存到tag
            convertView.setTag(viewHolder);
        } else {
            //有复用的view

            //从tag中取出ViewHolder
            viewHolder = (GroupViewHolder) convertView.getTag();
        }

        //绑定数据
        viewHolder.bind((MeGroup) getGroup(groupPosition), isExpanded);

        //返回view
        return convertView;
    }

    /**
     * 获取子View
     *
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //子ViewHolder
        ChildViewHolder viewHolder;

        if (convertView == null) {
            //没有复用的view

            //从view加载布局
            convertView = inflater.inflate(R.layout.item_topic, parent, false);

            //创建子ViewHolder
            viewHolder = new ChildViewHolder(convertView);

            //将ViewHolder保存到tag
            convertView.setTag(viewHolder);
        } else {
            //有复用的View

            //从tag中取出ViewHolder
            viewHolder = (ChildViewHolder) convertView.getTag();
        }

        //绑定数据
        viewHolder.bind((Sheet) getChild(groupPosition, childPosition));

        return convertView;
    }

    /**
     * 当前元素是否可以选中（点击）
     *
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * 设置数据
     *
     * @param datum
     */
    public void setDatum(List<MeGroup> datum) {
        //清除原来的数据
        this.datum.clear();

        //添加数据
        this.datum.addAll(datum);

        //通知控件数据改变了
        notifyDataSetChanged();
    }

    /**
     * 组ViewHolder
     * 就是一个普通类
     * 保存了控件的引用
     * 避免每次找控件产生的性能消耗
     */
    class GroupViewHolder {
        /**
         * 显示展开状态
         */
        ImageView statusView;

        /**
         * 标题
         */
        TextView titleView;

        /**
         * 更多按钮
         */
        ImageView moreView;

        public GroupViewHolder(View view) {
            statusView = view.findViewById(R.id.status);
            SuperViewUtil.show(statusView);

            titleView = view.findViewById(R.id.title);
            moreView = view.findViewById(R.id.more);

            moreView.setOnClickListener(v -> EventBus.getDefault().post(new CreateSheetClickEvent()));
        }

        /**
         * 绑定数据
         *
         * @param data
         * @param isExpanded
         */
        public void bind(MeGroup data, boolean isExpanded) {
            titleView.setText(data.getTitle());

            //处理展开状态
            if (isExpanded) {
                statusView.setImageResource(R.drawable.chevron_up);
            } else {
                statusView.setImageResource(R.drawable.chevron_down);
            }

            //处理更多按钮状态
            SuperViewUtil.show(moreView, data.isMore());
        }
    }

    /**
     * 子ViewHolder
     */
    class ChildViewHolder {
        /**
         * 封面
         */
        ImageView iconView;

        /**
         * 标题
         */
        TextView titleView;

        /**
         * 更多信息
         */
        TextView infoView;

        public ChildViewHolder(View view) {
            iconView = view.findViewById(R.id.icon);
            titleView = view.findViewById(R.id.title);
            infoView = view.findViewById(R.id.info);
        }

        /**
         * 绑定数据
         *
         * @param data
         */
        public void bind(Sheet data) {
            //显示封面
            ImageUtil.show((BaseActivity) context, iconView, data.getIcon());

            //标题
            titleView.setText(data.getTitle());

            //音乐数量
            infoView.setText(context.getResources().getString(R.string.song_count, data.getSongsCount()));
        }
    }
}
