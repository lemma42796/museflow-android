package com.ixuea.courses.mymusic.component.comment.activity;

import static com.ixuea.courses.mymusic.util.Constant.SHEET_ID;
import static autodispose2.AutoDispose.autoDisposable;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.adapter.TextWatcherAdapter;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.comment.adapter.CommentAdapter;
import com.ixuea.courses.mymusic.component.comment.fragment.CommentMoreDialogFragment;
import com.ixuea.courses.mymusic.component.comment.model.Comment;
import com.ixuea.courses.mymusic.component.login.model.event.LoginStatusChangedEvent;
import com.ixuea.courses.mymusic.component.user.activity.UserActivity;
import com.ixuea.courses.mymusic.component.user.activity.UserDetailActivity;
import com.ixuea.courses.mymusic.component.user.model.event.SelectedFriendEvent;
import com.ixuea.courses.mymusic.databinding.ActivityCommentBinding;
import com.ixuea.courses.mymusic.model.Base;
import com.ixuea.courses.mymusic.model.BaseId;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.model.response.ListResponse;
import com.ixuea.courses.mymusic.model.response.Meta;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.RichUtil;
import com.ixuea.superui.toast.SuperToast;
import com.ixuea.superui.util.KeyboardUtil;
import com.ixuea.superui.util.SuperClipboardUtil;
import com.ixuea.superui.util.SuperRecyclerViewUtil;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 评论界面
 */
public class CommentActivity extends BaseTitleActivity<ActivityCommentBinding> {

    private String sheetId;
    private CommentAdapter adapter;
    private boolean isRefresh;
    private Meta<Comment> pageMeta;
    private String parentId;

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    public static void startWithSheetId(Context context, String id) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra(Constant.SHEET_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void initViews() {
        super.initViews();
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list, true);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        sheetId = extraString(Constant.SHEET_ID);

        adapter = new CommentAdapter(R.layout.item_comment);
        binding.list.setAdapter(adapter);

        loadData();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        adapter.addChildClickViewIds(R.id.icon, R.id.user_container, R.id.like_container, R.id.content, R.id.reply_content);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            Comment data = (Comment) adapter.getItem(position);
            switch (view.getId()) {
                case R.id.icon:
                case R.id.user_container:
                    startActivityExtraId(UserDetailActivity.class, data.getUser().getId());
                    break;
                case R.id.like_container:
                    likeClick(data);
                    break;
                case R.id.content:
                case R.id.reply_content:
                    showCommentMoreDialog(data);
                    break;
            }
        });

        adapter.setOnItemClickListener((adapter, view, position) -> showCommentMoreDialog((Comment) adapter.getItem(position)));

        //下拉刷新监听器
        binding.refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadData();
            }
        });

        //上拉加载更多
        binding.refresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                loadMore();
            }
        });

        binding.input.send.setOnClickListener(v -> sendClick());

        //添加列表滚动监听器
        binding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             * 滚动了
             *
             * @param recyclerView
             * @param dx
             * @param dy
             */
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (Math.abs(dy) > 10) {
                    //y轴滚动方法大于10

                    if (StringUtils.isEmpty(binding.input.content.getText().toString().trim())) {
                        //还没有输入内容

                        //才清除回复
                        clearInputContent();
                    }
                }
            }
        });

        binding.input.content.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);

                String data = s.toString();
                if (data.endsWith(RichUtil.MENTION)) {
                    //输入了@

                    //跳转到选择好友界面
                    UserActivity.start(getHostActivity(), Constant.STYLE_FRIEND_SELECT);
                }
            }
        });
    }

    private void sendClick() {
        String content = binding.input.content.getText().toString().trim();

        if (StringUtils.isBlank(content)) {
            SuperToast.show(R.string.hint_comment);
            return;
        }

        Comment param = new Comment();
        param.setContent(content);
        param.setSheetId(sheetId);
        param.setParentId(parentId);

        DefaultRepository.getInstance()
                .createComment(param)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<Comment>>() {
                    @Override
                    public void onSucceeded(DetailResponse<Comment> data) {
                        SuperToast.success(R.string.comment_create_success);

                        loadData();

                        //清空输入框
                        clearInputContent();

                        //关闭键盘
                        KeyboardUtil.hideKeyboard(getHostActivity(), binding.input.content);
                    }
                });
    }

    /**
     * 清空输入框
     */
    private void clearInputContent() {
        parentId = null;
        binding.input.content.setText("");
        binding.input.content.setHint(R.string.hint_comment);
    }


    /**
     * 显示评论更多对话框
     *
     * @param data
     */
    private void showCommentMoreDialog(Comment data) {
        CommentMoreDialogFragment.showDialog(getSupportFragmentManager(), (dialog, which) -> {
            //关闭对话框
            dialog.dismiss();

            //处理点击事件
            processClick(which, data);
        });
    }

    /**
     * 处理评论更多对话框点击事件
     *
     * @param which
     * @param data
     */
    private void processClick(int which, Comment data) {
        switch (which) {
            case 0:
                //回复评论
                parentId = data.getId();
                binding.input.content.setHint(getResources().getString(R.string.reply_hint, data.getUser().getNickname()));
                break;
            case 1:
                //TODO 分享评论
                break;
            case 2:
                //复制评论
                SuperClipboardUtil.copyText(getHostActivity(), data.getContent());

                SuperToast.success(R.string.copy_success);
                break;
            case 3:
                //TODO 举报评论
                break;
        }
    }

    private void likeClick(Comment data) {
        if (!sp.isLogin()) {
            toLogin();
            return;
        }

        if (data.isLiked()) {
            //已经点赞了

            //取消点赞

            //传递的是评论Id，当然服务端也可以实现为，传递关系id
            DefaultRepository.getInstance()
                    .cancelCommentLike(data.getId())
                    .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe(new HttpObserver<DetailResponse<Base>>() {
                        @Override
                        public void onSucceeded(DetailResponse<Base> d) {
                            //可以调用接口，也可以在本地加减
                            addCount(data, 1);

                            //清空点赞id
                            data.setLikeId(null);

                            adapter.notifyDataSetChanged();
                        }
                    });
        } else {
            //没有点赞

            //点赞
            DefaultRepository.getInstance()
                    .commentLike(data.getId())
                    .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe(new HttpObserver<DetailResponse<BaseId>>() {
                        @Override
                        public void onSucceeded(DetailResponse<BaseId> d) {
                            //将数量+1
                            addCount(data, -1);
                            data.setLikeId(d.getData().getId());
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    private void addCount(Comment data, int count) {
        data.setLikesCount(data.getLikesCount() - count);

        if (data.getLikesCount() < 0) {
            data.setLikesCount(0);
        }
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        isRefresh = true;
        pageMeta = null;

        loadMore();
    }

    private void loadMore() {
        //查询参数
        HashMap<String, String> query = getQuery();

        //添加分页参数
        query.put(Constant.PAGE, String.valueOf(Meta.nextPage(pageMeta)));

        //最新评论
        DefaultRepository.getInstance()
                .comments(query)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<ListResponse<Comment>>() {
                    @Override
                    public void onSucceeded(ListResponse<Comment> data) {
                        pageMeta = data.getData();

                        //传入false表示刷新失败
                        binding.refresh.finishRefresh(2000, true, false);

                        //next=null，表示没有更多数据了
                        binding.refresh.finishLoadMore(2000, true, pageMeta.getNext() == null);

                        if (isRefresh) {
                            isRefresh = false;

                            if (CollectionUtils.isEmpty(data.getData().getData())) {
                                //没有数据
                                adapter.clearData();
                            } else {
                                adapter.setNewInstance(data.getData().getData());
                            }
                        } else {
                            adapter.addData(data.getData().getData());
                        }
                    }
                });
    }

    /**
     * 获取查询参数
     *
     * @return
     */
    private HashMap<String, String> getQuery() {
        //创建map
        HashMap<String, String> query = new HashMap<>();

        //添加歌单id
        if (StringUtils.isNotBlank(sheetId)) {
            query.put(SHEET_ID, sheetId);
        }

        return query;
    }

    /**
     * 登录状态改变了事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginStatusChangedEvent(LoginStatusChangedEvent event) {
        loadData();
    }

    /**
     * 选择好友事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectedFriendEvent(SelectedFriendEvent event) {
        //添加用户
        // 当然如何要实现的更好，就是添加到用户光标位置
        //如果有选中，就是替换，这里就不再实现了，因为还挺麻烦，在微聊项目课程中有实现
        binding.input.content.append(event.getData().getNickname());

        //添加结尾符号
        binding.input.content.append(" ");

        //高亮文本
        highlightText();
    }

    /**
     * 高亮文本
     */
    private void highlightText() {
        //高亮文本
        binding.input.content.setText(RichUtil.processHighlight(getHostActivity(), binding.input.content.getText().toString()));

        //将光标位置移动到最后
        binding.input.content.setSelection(binding.input.content.getText().toString().length());
    }
}