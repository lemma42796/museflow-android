package com.ixuea.courses.mymusic.component.user.fragment;

import static autodispose2.AutoDispose.autoDisposable;

import android.os.Bundle;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.observer.ObserverAdapter;
import com.ixuea.courses.mymusic.component.sheet.model.Sheet;
import com.ixuea.courses.mymusic.component.user.adapter.UserDetailSheetAdapter;
import com.ixuea.courses.mymusic.component.user.model.ui.TitleData;
import com.ixuea.courses.mymusic.databinding.FragmentUserDetailSheetBinding;
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment;
import com.ixuea.courses.mymusic.model.response.ListResponse;
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.ExceptionHandlerUtil;

import java.util.ArrayList;
import java.util.List;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.BiFunction;

/**
 * 用户详情-歌单界面
 */
public class UserDetailSheetFragment extends BaseViewModelFragment<FragmentUserDetailSheetBinding> {

    private String id;
    private UserDetailSheetAdapter adapter;

    public static UserDetailSheetFragment newInstance(String id) {

        Bundle args = new Bundle();
        args.putString(Constant.ID, id);

        UserDetailSheetFragment fragment = new UserDetailSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        id = extraId();

        adapter = new UserDetailSheetAdapter();
        binding.list.setAdapter(adapter);

        loadData();
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        //创建歌单请求
        Observable<ListResponse<Sheet>> createSheetsApi = DefaultRepository.getInstance()
                .createSheets(id);

        //收藏的歌单请求
        Observable<ListResponse<Sheet>> collectSheetsApi = DefaultRepository.getInstance()
                .collectSheets(id);


        //使用RxAndroid
        //将两个请求实现为并发
        //两个请求都请求成功后回调
        Observable.zip(createSheetsApi, collectSheetsApi, new BiFunction<ListResponse<Sheet>, ListResponse<Sheet>, List<BaseMultiItemEntity>>() {
            /**
             * 所有任务都执行成功了回调
             *
             * @param createSheets
             * @param collectSheets
             * @return
             * @throws Exception
             */
            @Override
            public List<BaseMultiItemEntity> apply(ListResponse<Sheet> createSheets, ListResponse<Sheet> collectSheets) throws Throwable {
                List<BaseMultiItemEntity> datum = new ArrayList<>();

                datum.add(new TitleData(R.string.created_sheet));
                datum.addAll(createSheets.getData().getData());

                datum.add(new TitleData(R.string.collected_sheet));
                datum.addAll(collectSheets.getData().getData());

                return datum;
            }
        })
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(UserDetailSheetFragment.this)))
                .subscribeWith(new ObserverAdapter<List<BaseMultiItemEntity>>() {
                    @Override
                    public void onNext(List<BaseMultiItemEntity> datum) {
                        super.onNext(datum);
                        adapter.setNewInstance(datum);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ExceptionHandlerUtil.handleException(e, null);
                    }
                });
    }
}
