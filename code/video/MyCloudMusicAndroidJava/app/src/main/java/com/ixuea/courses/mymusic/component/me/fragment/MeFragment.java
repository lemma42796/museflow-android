package com.ixuea.courses.mymusic.component.me.fragment;

import static autodispose2.AutoDispose.autoDisposable;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.download.activity.DownloadActivity;
import com.ixuea.courses.mymusic.component.login.model.event.LoginStatusChangedEvent;
import com.ixuea.courses.mymusic.component.me.adapter.MeAdapter;
import com.ixuea.courses.mymusic.component.me.model.event.CreateSheetClickEvent;
import com.ixuea.courses.mymusic.component.me.model.ui.MeGroup;
import com.ixuea.courses.mymusic.component.music.activity.LocalMusicActivity;
import com.ixuea.courses.mymusic.component.observer.ObserverAdapter;
import com.ixuea.courses.mymusic.component.sheet.activity.SheetDetailActivity;
import com.ixuea.courses.mymusic.component.sheet.model.Sheet;
import com.ixuea.courses.mymusic.component.sheet.model.event.SheetChangedEvent;
import com.ixuea.courses.mymusic.databinding.FragmentMeBinding;
import com.ixuea.courses.mymusic.databinding.HeaderMeBinding;
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.model.response.ListResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.ExceptionHandlerUtil;
import com.ixuea.superui.dialog.SuperDialog;
import com.ixuea.superui.toast.SuperToast;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.BiFunction;
import timber.log.Timber;

/**
 * 首页-我界面
 */
public class MeFragment extends BaseViewModelFragment<FragmentMeBinding> implements ExpandableListView.OnChildClickListener {

    private MeAdapter adapter;
    private HeaderMeBinding headerBinding;
    private long startTime;
    private String userId;

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void initViews() {
        super.initViews();
        headerBinding = HeaderMeBinding.inflate(getLayoutInflater());

        binding.list.addHeaderView(headerBinding.getRoot());
    }

    public static MeFragment newInstance() {
        return newInstance(null);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        binding.list.setOnChildClickListener(this);

        headerBinding.localMusic.setOnClickListener(v -> startActivity(LocalMusicActivity.class));
        headerBinding.downloadManager.setOnClickListener(v -> startActivity(DownloadActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    public static MeFragment newInstance(String userId) {

        Bundle args = new Bundle();
        if (StringUtils.isNotBlank(userId)) {
            args.putString(Constant.ID, userId);
        }

        MeFragment fragment = new MeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        userId = extraId();

        adapter = new MeAdapter(getHostActivity());
        binding.list.setAdapter(adapter);
    }

    /**
     * 展开所有组
     */
    private void expandedAll() {
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            binding.list.expandGroup(i);
        }
    }

    /**
     * 歌单改变了事件
     * <p>
     * 例如：在歌单详情，收藏或取消了收藏
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sheetChangedEvent(SheetChangedEvent event) {
        loadData();
    }

    /**
     * 登录状态改变了事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginStatusChangedEvent(LoginStatusChangedEvent event) {
        loadData();
    }

    /**
     * 点击了歌单创建按钮事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void createSheetClickEvent(CreateSheetClickEvent event) {
        //如果在Fragment中显示fragment
        //要使用getChildFragmentManager方法
        SuperDialog superDialog = SuperDialog.newInstance(getChildFragmentManager());
        superDialog
                .setTitleRes(R.string.created_sheet)
                .titleInputConfirmStyle()
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String data = superDialog.getInputView().getText().toString().trim();
                        if (StringUtils.isBlank(data)) {
                            SuperToast.show(R.string.hint_enter_sheet_name);
                            return;
                        }

                        createSheet(data);
                    }
                }).show();
    }

    private void createSheet(String data) {
        Sheet param = new Sheet();

        //这里不要传用户id
        //不然这就是一个漏洞
        //就可以给任何人创建歌单
        //而是服务端根据token获取用户信息
        param.setTitle(data);

        DefaultRepository.getInstance()
                .createSheet(param)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<Sheet>>() {
                    @Override
                    public void onSucceeded(DetailResponse<Sheet> data) {
                        SuperToast.success(R.string.success_create_sheet);

                        EventBus.getDefault().post(new SheetChangedEvent());
                    }
                });

    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        if (StringUtils.isBlank(userId)) {
            if (!sp.isLogin()) {
                return;
            }
            userId = sp.getUserId();
        }
        next();
    }

    private void next() {
        //数据列表
//        ArrayList<MeGroup> datum = new ArrayList<>();

        //创建歌单请求
        Observable<ListResponse<Sheet>> createSheetsApi = DefaultRepository.getInstance()
                .createSheets(userId);

        //收藏的歌单请求
        Observable<ListResponse<Sheet>> collectSheetsApi = DefaultRepository.getInstance()
                .collectSheets(userId);

        //普通的方式
        //请求是线性
//        createSheetsApi
//                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
//                .subscribe(new HttpObserver<ListResponse<Sheet>>() {
//                    @Override
//                    public void onSucceeded(ListResponse<Sheet> data) {
//                        //添加数据
//                        datum.add(new MeGroup(R.string.created_sheet, data.getData().getData(), true));
//
//                        //请求收藏的歌单
//                        collectSheetsApi
//                                .to(autoDisposable(AndroidLifecycleScopeProvider.from(MeFragment.this)))
//                                .subscribe(new HttpObserver<ListResponse<Sheet>>() {
//                                    @Override
//                                    public void onSucceeded(ListResponse<Sheet> data) {
//                                        //添加数据
//                                        datum.add(new MeGroup(R.string.collected_sheet, data.getData().getData(), false));
//
//                                        adapter.setDatum(datum);
//
//                                        //展开所有组
//                                        expandedAll();
//                                    }
//                                });
//                    }
//                });

        startTime = System.currentTimeMillis();

        //使用RxAndroid
        //将两个请求实现为并发
        //两个请求都请求成功后回调
        Observable.zip(createSheetsApi, collectSheetsApi, new BiFunction<ListResponse<Sheet>, ListResponse<Sheet>, List<MeGroup>>() {
            /**
             * 所有任务都执行成功了回调
             *
             * @param createSheets
             * @param collectSheets
             * @return
             * @throws Exception
             */
            @Override
            public List<MeGroup> apply(ListResponse<Sheet> createSheets, ListResponse<Sheet> collectSheets) throws Throwable {
                //请求成功了

                //只要有一个请求失败了
                //就直接走到onError

                //将数据放到列表中

                //也可以包装为对象
                //或者使用操作符
                //添加数据
                List<MeGroup> datum = new ArrayList<>();

                //添加创建的歌单
                datum.add(new MeGroup(R.string.created_sheet, createSheets.getData().getData(), true));

                //添加收藏的歌单
                datum.add(new MeGroup(R.string.collected_sheet, collectSheets.getData().getData(), false));

                return datum;
            }
        })
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(MeFragment.this)))
                .subscribeWith(new ObserverAdapter<List<MeGroup>>() {
                    @Override
                    public void onNext(List<MeGroup> datum) {
                        super.onNext(datum);
                        adapter.setDatum(datum);

                        //展开所有组
                        expandedAll();

                        //结束时间
                        long endTime = System.currentTimeMillis();

                        //网络请求消耗的时间
                        long consumeTime = endTime - startTime;
                        Timber.d("onNext: " + consumeTime);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        //使用工具类处理错误
                        //这就是为什么要将错误处理
                        //封装到工具类的原因
                        ExceptionHandlerUtil.handleException(e, null);
                    }
                });
    }

    /**
     * 子元素点击回调
     *
     * @param parent
     * @param v
     * @param groupPosition
     * @param childPosition
     * @param id
     * @return true:表示我们处理了事件
     */
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        //获取点击的数据
        Sheet data = (Sheet) adapter.getChild(groupPosition, childPosition);

        startActivityExtraId(SheetDetailActivity.class, data.getId());
        return true;
    }
}
