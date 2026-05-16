package com.ixuea.courses.mymusic.component.search.fragment;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.search.model.event.SearchEvent;
import com.ixuea.courses.mymusic.databinding.FragmentSheetBinding;
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment;
import com.ixuea.courses.mymusic.util.Constant;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 通用搜索fragment
 */
public class BaseSearchFragment extends BaseViewModelFragment<FragmentSheetBinding> {
    protected String query;
    private int index;

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void initViews() {
        super.initViews();
        binding.placeholderView.show(R.string.no_data, R.drawable.no_data);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        index = extraInt(Constant.STYLE);
    }

    /**
     * 搜索事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void searchEvent(SearchEvent event) {
        query = event.getData();
        if (event.getSelectedIndex() == index) {
            //只有索引一样才搜索
            //这样可以避免同时搜索多个界面
            loadData();
        }
    }
}
