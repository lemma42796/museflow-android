package com.ixuea.courses.mymusic.component.conversation.activity;

import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.chat.activity.ChatActivity;
import com.ixuea.courses.mymusic.component.conversation.adapter.ConversationAdapter;
import com.ixuea.courses.mymusic.component.conversation.model.event.NewMessageEvent;
import com.ixuea.courses.mymusic.databinding.ActivityConversationBinding;
import com.ixuea.superui.util.SuperDelayUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import timber.log.Timber;

/**
 * 我的消息（会话界面）
 */
public class ConversationActivity extends BaseTitleActivity<ActivityConversationBinding> {

    private ConversationAdapter adapter;
    private SuperDelayUtil delayUtil;


    @Override
    protected void initDatum() {
        super.initDatum();
        delayUtil = new SuperDelayUtil();

        adapter = new ConversationAdapter(R.layout.item_conversation);
        binding.list.setAdapter(adapter);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        adapter.setOnItemClickListener((adapter, view, position) -> {
            Conversation data = (Conversation) adapter.getItem(position);
            startActivityExtraId(ChatActivity.class, data.getTargetId());
        });

        //长按事件
        adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull @NotNull BaseQuickAdapter adapter, @NonNull @NotNull View view, int position) {
                //删除会话，真实项目中一般要弹出一个菜单
                Conversation data = (Conversation) adapter.getItem(position);
//                RongIMClient.getInstance().removeConversation(data.getConversationType(), data.getTargetId(), new  RongIMClient.ResultCallback<Boolean>() {
//                    /**
//                     * 成功回调
//                     */
//                    @Override
//                    public void onSuccess(Boolean success) {
//                        adapter.removeAt(position);
//                    }
//                    /**
//                     * 失败回调
//                     * @param errorCode 错误码
//                     */
//                    @Override
//                    public void onError(RongIMClient.ErrorCode errorCode) {
//
//                    }
//                });

                //删除该会话下所有消息，真实项目中，一般是在聊天详情实现，这里写到这里的目只是测试这个功能
                RongIMClient.getInstance().deleteMessages(data.getConversationType(), data.getTargetId(), new RongIMClient.ResultCallback<Boolean>() {
                    /**
                     * 删除消息成功回调
                     */
                    @Override
                    public void onSuccess(Boolean bool) {
                        loadData(false);
                    }

                    /**
                     * 删除消息失败回调
                     * @param errorCode 错误码
                     */
                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        //这里获取所有会话，真实项目中，可以分页获取
        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                adapter.setNewInstance(conversations);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {

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
        //过滤1000毫秒内的重复回调
        //目的是，防止一瞬间收到很多条消息，界面重复刷新
        Timber.d("onNewMessageEvent %s", event.getData().getMessageId());
        delayUtil.throttle(1000, new SuperDelayUtil.SuperDelayListener() {
            @Override
            public void onRun() {
                loadData();
            }
        });
    }
}