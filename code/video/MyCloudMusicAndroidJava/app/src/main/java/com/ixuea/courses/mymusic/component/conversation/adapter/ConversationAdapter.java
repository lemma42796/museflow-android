package com.ixuea.courses.mymusic.component.conversation.adapter;

import android.app.Activity;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.manager.UserManager;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.MessageUtil;
import com.ixuea.courses.mymusic.util.StringUtil;
import com.ixuea.courses.mymusic.util.SuperDateUtil;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;

/**
 * 会话适配器
 */
public class ConversationAdapter extends BaseQuickAdapter<Conversation, BaseViewHolder> {
    public ConversationAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, Conversation data) {
        //获取头像控件
        ImageView icon = holder.getView(R.id.icon);

        //由于消息上没有用户信息，只有id，所以还需要从用户管理器中获取用户信息
        //当然也可以每条消息上带上用户信息，但这肯定会更耗资源
        String targetId = data.getTargetId();

        UserManager.getInstance(getContext())
                .getUser(targetId, new UserManager.UserListener() {
                    @Override
                    public void onGetUserSuccess(User userData) {
                        //显示头像
                        ImageUtil.showAvatar((Activity) getContext(), icon, userData.getIcon());

                        //昵称
                        holder.setText(R.id.nickname, userData.getNickname());
                    }
                });

        //获取最后一条消息
        MessageContent latestMessage = data.getLatestMessage();

        if (latestMessage == null) {
            //清空日期
            holder.setText(R.id.time, "");

            //清空消息内容
            holder.setText(R.id.info, "");
        } else {
            //获取消息时间
            long time = data.getReceivedTime();

            //格式化日期
            holder.setText(R.id.time, SuperDateUtil.commonFormat(time));

            //显示消息
            holder.setText(R.id.info, MessageUtil.getContent(latestMessage));
        }

        //未读消息数
        int count = data.getUnreadMessageCount();
        if (count > 0) {
            //显示未读消息数控件
            holder.setVisible(R.id.count, true);

            holder.setText(R.id.count, StringUtil.formatMessageCount(count));
        } else {
            //隐藏未读消息数控件
            holder.setVisible(R.id.count, false);
        }
    }
}
