package com.ixuea.courses.mymusic.component.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;
import com.ixuea.courses.mymusic.component.chat.model.MediaMessageExtra;
import com.ixuea.courses.mymusic.manager.UserManager;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.JSONUtil;
import com.ixuea.superui.util.DensityUtil;
import com.ixuea.superui.util.SuperViewUtil;

import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

/**
 * 聊天界面适配器
 */
public class ChatAdapter extends BaseRecyclerViewAdapter<Message, ChatAdapter.ViewHolder> {
    /**
     * 用户管理器
     */
    private final UserManager userManager;
    private final int imageMaxWidth;

    public ChatAdapter(Context context) {
        super(context);

        //初始化用户管理器
        userManager = UserManager.getInstance(context);

        /**
         * 最大消息图片宽度
         *
         * 150dp和微信差不多
         * QQ是按照屏幕宽读获取的
         */
        imageMaxWidth = (int) DensityUtil.dip2px(context, 150F);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //这里要区分是我发送的
        //还是其他人发送的

        //我发送的显示在右边
        //其他人发送的显示在左边

        //他们的布局都一样只是方向不一样
        //所以可以用同一个ViewHolder
        switch (viewType) {
            case Constant.IMAGE_LEFT:
                //其他人发送的图片消息
                return new ImageViewHolder(getInflater().inflate(R.layout.item_chat_image_left, parent, false));
            case Constant.IMAGE_RIGHT:
                //我发送的图片消息
                return new ImageViewHolder(getInflater().inflate(R.layout.item_chat_image_right, parent, false));
            case Constant.TEXT_LEFT:
                //其他人发送的文本消息
                return new TextViewHolder(getInflater().inflate(R.layout.item_chat_text_left, parent, false));
            default:
                //我发送的文本消息
                return new TextViewHolder(getInflater().inflate(R.layout.item_chat_text_right, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.bind(getData(position));
    }

    /**
     * 返回view类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        Message data = getData(position);

        Message.MessageDirection messageDirection = data.getMessageDirection();
        MessageContent content = data.getContent();

        if (content instanceof ImageMessage) {
            //图片消息
            return messageDirection == Message.MessageDirection.SEND ? Constant.IMAGE_RIGHT : Constant.IMAGE_LEFT;
        }

        //TODO 其他消息可以继续在这里扩展

        //文本消息
        //我发送的消息在右边
        return messageDirection == Message.MessageDirection.SEND ? Constant.TEXT_RIGHT : Constant.TEXT_LEFT;
    }

    /**
     * 聊天消息公共ViewHolder，比如头像
     */
    class BaseChatViewHolder extends BaseRecyclerViewAdapter.ViewHolder<Message> {

        private final ImageView iconView;

        public BaseChatViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.icon);
        }

        @Override
        public void bind(Message data) {
            super.bind(data);
            //这显示的信息，就显示发送人的信息就行了
            userManager.getUser(data.getSenderUserId(), data1 -> ImageUtil.showAvatar((Activity) context, iconView, data1.getIcon()));
        }
    }

    /**
     * 文本消息VH
     */
    class TextViewHolder extends BaseChatViewHolder {

        private final TextView contentView;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            contentView = itemView.findViewById(R.id.content);
        }

        @Override
        public void bind(Message data) {
            super.bind(data);
            TextMessage content = (TextMessage) data.getContent();
            contentView.setText(content.getContent());

            //真实项目中
            //可能还会实现像评论那边的mention和hashTag
            //因为在评论那边讲解了
            //所以这里就不在重复讲解了
        }
    }

    /**
     * 图片消息VH
     */
    class ImageViewHolder extends BaseChatViewHolder {
        /**
         * 图片控件
         */
        ImageView contentView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            contentView = itemView.findViewById(R.id.content);
        }

        @Override
        public void bind(Message data) {
            super.bind(data);
            ImageMessage imageMessage = ((ImageMessage) data.getContent());

            //解析扩展信息
            MediaMessageExtra mediaMessageExtra = JSONUtil.fromJSON(imageMessage.getExtra(), MediaMessageExtra.class);
            //设置尺寸，尺寸从sdk获取封面图尺寸
            setImageContentContainerSize(
                    mediaMessageExtra.getWidth(),
                    mediaMessageExtra.getHeight());

            if (imageMessage.getRemoteUri() != null) {
                ImageUtil.showFull(context, contentView, imageMessage.getRemoteUri().toString());
            } else if (imageMessage.getLocalUri() != null) {
                //刚发送时，只有本地地址
                ImageUtil.showLocalImage(context, contentView, imageMessage.getLocalUri().toString());
            } else {
                //缩略图
                ImageUtil.showFull(context, contentView, imageMessage.getThumUri().toString());
            }
        }

        /**
         * 设置图片容器宽高
         */
        private void setImageContentContainerSize(int width, int height) {
            int newWidth = 0;
            int newHeight = 0;
            if (width > height) {
                //宽大于高，宽就位最大宽度，动态计算高
                newWidth = imageMaxWidth;
                newHeight = imageMaxWidth * height / width;
            } else {
                newWidth = imageMaxWidth * width / height;
                newHeight = imageMaxWidth;
            }

            SuperViewUtil.resize(contentView, newWidth, newHeight);
        }


    }


}
