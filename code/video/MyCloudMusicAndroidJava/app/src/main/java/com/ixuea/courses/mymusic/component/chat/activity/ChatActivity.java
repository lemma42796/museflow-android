package com.ixuea.courses.mymusic.component.chat.activity;

import android.content.Context;
import android.net.Uri;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.chat.adapter.ChatAdapter;
import com.ixuea.courses.mymusic.component.chat.model.event.MessageUnreadCountChangedEvent;
import com.ixuea.courses.mymusic.component.chat.repository.ChatClient;
import com.ixuea.courses.mymusic.component.chat.repository.ConversationRepository;
import com.ixuea.courses.mymusic.component.chat.repository.MessageRepository;
import com.ixuea.courses.mymusic.component.conversation.model.event.NewMessageEvent;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.config.glide.GlideEngine;
import com.ixuea.courses.mymusic.databinding.ActivityChatBinding;
import com.ixuea.courses.mymusic.manager.UserManager;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.superui.toast.SuperToast;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import timber.log.Timber;
import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;

/**
 * 聊天界面
 */
public class ChatActivity extends BaseTitleActivity<ActivityChatBinding> {
    private String targetId;
    private ChatAdapter adapter;

    /**
     * 用户管理器
     */
    private UserManager userManager;

    private int oldMessageId = -1;

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void initViews() {
        super.initViews();
        //刷新箭头颜色
        binding.refresh.setColorSchemeResources(R.color.primary);

        //刷新圆圈颜色
        binding.refresh.setProgressBackgroundColorSchemeResource(R.color.white);
    }

    @Override
    protected void initDatum() {
        super.initDatum();

        //初始化用户管理器
        userManager = UserManager.getInstance(getHostActivity());

        //获取id
        targetId = extraId();

        adapter = new ChatAdapter(getHostActivity());
        binding.list.setAdapter(adapter);

        loadData();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        binding.refresh.setOnRefreshListener(() -> loadMore());
        binding.selectImage.setOnClickListener(v -> selectImage());
        binding.send.setOnClickListener(v -> sendTextMessage());
    }

    private void selectImage() {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setMaxSelectNum(1)// 最大图片选择数量 int
                .setMinSelectNum(1)// 最小选择数量 int
                .setImageSpanCount(3)// 每行显示个数 int
                .setSelectionMode(SelectModeConfig.SINGLE)// 多选 or 单选 MULTIPLE or SINGLE
                .isPreviewImage(true)// 是否可预览图片 true or false
                .isDisplayCamera(true)// 是否显示拍照按钮 true or false
                .setCameraImageFormat(PictureMimeType.JPEG)// 拍照保存图片格式后缀,默认jpeg
                //压缩
                .setCompressEngine(new CompressFileEngine() {
                    @Override
                    public void onStartCompress(Context context, ArrayList<Uri> source, OnKeyValueResultCallbackListener call) {
                        Luban.with(context).load(source).ignoreBy(100)
                                .setCompressListener(new OnNewCompressListener() {
                                    @Override
                                    public void onStart() {

                                    }

                                    @Override
                                    public void onSuccess(String source, File compressFile) {
                                        if (call != null) {
                                            call.onCallback(source, compressFile.getAbsolutePath());
                                        }
                                    }

                                    @Override
                                    public void onError(String source, Throwable e) {
                                        if (call != null) {
                                            call.onCallback(source, null);
                                        }
                                    }
                                }).launch();
                    }
                })
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        sendImageMessage(result.get(0).getCompressPath());
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //由于我们这里没有区分消息具体的状态
        //比如：已读还是未读，所以只要进入了会话界面，该会话下面的所有消息都表示已读
        ConversationRepository.INSTANCE.clearUnread(targetId, new ChatClient.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                //发送消息未读数改变了通知
                EventBus.getDefault().post(new MessageUnreadCountChangedEvent());
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    /**
     * 发送图片消息
     *
     * @param path
     */
    private void sendImageMessage(String path) {
        MessageRepository.INSTANCE.sendImage(targetId, path, sp.getUserId(), new ChatClient.ImageSendCallback() {
            @Override
            public void onAttached(Message message) {
                Timber.d("sendImageMessage onAttached %s", message);
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                Timber.e("sendImageMessage onError %s %s", message, errorCode);
            }

            @Override
            public void onSuccess(Message message) {
                addMessage(message);
                Timber.d("sendImageMessage success %s", message);
            }

            @Override
            public void onProgress(Message message, int progress) {
                Timber.d("sendImageMessage progress %d %s", progress, message);
            }
        });
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);

        userManager.getUser(targetId, new UserManager.UserListener() {
            @Override
            public void onGetUserSuccess(User data) {
                setTitle(data.getNickname());
            }
        });

        loadMore();
    }

    private void loadMore() {
        MessageRepository.INSTANCE.getHistoryMessages(
                targetId,
                oldMessageId,
                Constant.DEFAULT_MESSAGE_COUNT,
                new ChatClient.Callback<List<Message>>() {
                    @Override
                    public void onSuccess(List<Message> messages) {
                        binding.refresh.setRefreshing(false);

                        if (messages != null && messages.size() > 0) {
                            adapter.addData(0, messages);

                            if (oldMessageId == -1) {
                                //如果有下拉操作，就不滚动，当前用可以根据具体的业务逻辑来
                                scrollBottom();
                            }

                            //排序后第一条就是最久的一条，加载更多也就是用这个id
                            oldMessageId = messages.get(0).getMessageId();
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        binding.refresh.setRefreshing(false);
                    }
                });
    }

    private void sendTextMessage() {
        String content = binding.input.getText().toString().trim();
        if (StringUtils.isEmpty(content)) {
            SuperToast.show(R.string.hint_enter_message);
            return;
        }

        MessageRepository.INSTANCE.sendText(targetId, content, sp.getUserId(), new ChatClient.SendCallback() {
            @Override
            public void onAttached(Message message) {
                // 消息成功存到本地数据库的回调
                Timber.d("sendTextMessage onAttached %s", message);
            }

            @Override
            public void onSuccess(Message message) {
                // 消息发送成功的回调
                Timber.d("sendTextMessage success %s", message);

                //清空输入框
                clearInput();

                addMessage(message);
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                // 消息发送失败的回调
                Timber.e("sendTextMessage onError %s %s", message, errorCode);
            }
        });

    }

    /**
     * 添加消息到列表
     *
     * @param data
     */
    private void addMessage(Message data) {
        //将消息添加到列表后面
        adapter.addData(data);

        //滚动到底部
        smoothScrollBottom();
    }

    /**
     * 清空输入框
     */
    private void clearInput() {
        binding.input.setText("");
    }


    /**
     * 滚动到底部
     */
    private void scrollBottom() {
        binding.list.post(new Runnable() {
            @Override
            public void run() {
                //使用动画滚动到底部
                binding.list.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    /**
     * 动画滚动到底部
     */
    private void smoothScrollBottom() {
        binding.list.post(new Runnable() {
            @Override
            public void run() {
                //使用动画滚动到底部
                binding.list.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    /**
     * 有新消息了
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessageEvent(NewMessageEvent event) {
        Message message = event.getData();
        if (!message.getSenderUserId().equals(targetId)) {
            return;
        }

        //既然在当前界面，那收到的消息也是已读的
        MessageRepository.INSTANCE.markRead(message);

        addMessage(message);
    }
}
