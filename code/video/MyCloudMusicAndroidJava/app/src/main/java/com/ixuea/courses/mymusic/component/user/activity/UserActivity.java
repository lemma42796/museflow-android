package com.ixuea.courses.mymusic.component.user.activity;

import static autodispose2.AutoDispose.autoDisposable;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.user.adapter.UserAdapter;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.component.user.model.event.SelectedFriendEvent;
import com.ixuea.courses.mymusic.component.user.model.ui.UserResult;
import com.ixuea.courses.mymusic.databinding.ActivityUserBinding;
import com.ixuea.courses.mymusic.model.response.ListResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.DataUtil;
import com.ixuea.superui.util.SuperRecyclerViewUtil;
import com.ixuea.superui.util.SuperViewUtil;
import com.xm.letterindex.LetterIndexView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.core.Observable;

/**
 * 我的好友，粉丝界面
 */
public class UserActivity extends BaseTitleActivity<ActivityUserBinding> {

    private int style;
    private List<User> datum;
    private UserAdapter adapter;
    private UserResult userResult;
    private LinearLayoutManager layoutManager;

    /**
     * 启动界面
     *
     * @param context
     * @param style
     */
    public static void start(Context context, int style) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(Constant.STYLE, style);
        context.startActivity(intent);
    }

    @Override
    protected void initViews() {
        super.initViews();
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list, true);
        layoutManager = (LinearLayoutManager) binding.list.getLayoutManager();
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        style = extraInt(Constant.STYLE);

        adapter = new UserAdapter();
        binding.list.setAdapter(adapter);

        loadData();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        binding.letterIndex.setOnStateChangeListener(new LetterIndexView.OnStateChangeListener() {
            @Override
            public void onStateChange(int eventAction, int position, String letter, int itemCenterY) {
                layoutManager.scrollToPositionWithOffset(userResult.getIndexes()[position], 0);
            }
        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Object data = adapter.getItem(position);
                if (data instanceof User) {
                    if (Constant.STYLE_FRIEND_SELECT == style) {
                        EventBus.getDefault().post(new SelectedFriendEvent((User) data));

                        //关闭界面
                        finish();
                    } else {
                        startActivityExtraId(UserDetailActivity.class, ((User) data).getId());
                    }
                }
            }
        });
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        Observable<ListResponse<User>> api;
        if (isFriend()) {
            setTitle(R.string.my_friend);
            api = DefaultRepository.getInstance().friends(sp.getUserId());
        } else {
            setTitle(R.string.my_fans);
            api = DefaultRepository.getInstance().fans(sp.getUserId());
        }

        api.to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<ListResponse<User>>() {
                    @Override
                    public void onSucceeded(ListResponse<User> data) {
                        datum = data.getData().getData();

                        //获取测试数据
                        datum = DataUtil.getTestUserData();

                        //处理拼音
                        datum = DataUtil.processUserPinyin(datum);

                        //设置数据
                        setData(datum);
                    }
                });

    }

    private void setData(List<User> datum) {
        //处理数据
        userResult = DataUtil.processUser(datum);

        adapter.setNewInstance(userResult.getDatum());

        SuperViewUtil.show(binding.letterIndex);
        binding.letterIndex.setLetterList(userResult.getLetters());
    }

    /**
     * 创建菜单方法
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //返回menu布局
        getMenuInflater().inflate(R.menu.search, menu);

        //查找搜索按钮
        MenuItem searchItem = menu.findItem(R.id.action_search);

        //在搜索按钮里面查询搜索控件
        SearchView searchView = (SearchView) searchItem.getActionView();

        //在这里就可以配置SearchView

        //设置搜索监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * 提交了搜索
             *
             * 在输入框中按回车
             * 点击了键盘上的搜索按钮
             * @param query
             * @return
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearchTextChanged(query);
                return true;
            }

            /**
             * 搜索框内容改变了
             * @param newText
             * @return
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                onSearchTextChanged(newText);
                return true;
            }
        });

        //是否进入界面就打开搜索栏，false为默认打开，默认为true
        if (Constant.STYLE_FRIEND_SELECT == style) {
            searchView.setIconified(false);
        } else {
            searchView.setIconified(true);
        }

        //搜索提示
        searchView.setQueryHint(getString(R.string.hint_search_user));

        return true;
    }

    private void onSearchTextChanged(String query) {
        if (TextUtils.isEmpty(query)) {
            //没有关键字

            //显示完整数据
            setData(datum);
        } else {
            //有关键字

            //过滤数据
            List<User> data = DataUtil.filterUser(datum, query.toLowerCase());
            setData(data);
        }
    }

    private boolean isFriend() {
        return style == Constant.STYLE_FRIEND || style == Constant.STYLE_FRIEND_SELECT;
    }
}