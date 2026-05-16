package com.ixuea.courses.mymusic.component.search.activity;

import static autodispose2.AutoDispose.autoDisposable;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager2.widget.ViewPager2;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.search.adapter.SearchCategoryAdapter;
import com.ixuea.courses.mymusic.component.search.adapter.SearchHistoryAdapter;
import com.ixuea.courses.mymusic.component.search.model.SearchHistory;
import com.ixuea.courses.mymusic.component.search.model.Suggest;
import com.ixuea.courses.mymusic.component.search.model.SuggestItem;
import com.ixuea.courses.mymusic.component.search.model.event.SearchEvent;
import com.ixuea.courses.mymusic.databinding.ActivitySearchBinding;
import com.ixuea.courses.mymusic.databinding.HeaderSearchBinding;
import com.ixuea.courses.mymusic.databinding.ItemTagBinding;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.SuperTabUtil;
import com.ixuea.superui.util.KeyboardUtil;
import com.ixuea.superui.util.SuperRecyclerViewUtil;
import com.ixuea.superui.util.SuperViewUtil;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 搜索界面
 */
public class SearchActivity extends BaseTitleActivity<ActivitySearchBinding> {
    /**
     * 标题数据
     */
    private Integer[] indicatorTitles = {
            R.string.sheet,
            R.string.user,
            R.string.single,
            R.string.video,
            R.string.singer,
            R.string.album,
            R.string.radio_station};

    private SearchView searchView;
    private String data;
    private SearchHistoryAdapter searchHistoryAdapter;
    private HeaderSearchBinding headerSearchBinding;
    private SearchCategoryAdapter searchCategoryAdapter;
    private SearchView.SearchAutoComplete searchAutoComplete;

    /**
     * 搜索建议适配器
     */
    private ArrayAdapter<String> suggestAdapter;
    private Runnable suggestionRunnable;

    @Override
    protected void initViews() {
        super.initViews();
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list);

        SuperTabUtil.bind(binding.indicator, binding.pager);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        //搜索历史适配器
        searchHistoryAdapter = new SearchHistoryAdapter(R.layout.item_search_history);
        searchHistoryAdapter.addHeaderView(createHeaderView());
        binding.list.setAdapter(searchHistoryAdapter);

        //搜索结果适配器
        searchCategoryAdapter = new SearchCategoryAdapter(getHostActivity());
        binding.pager.setAdapter(searchCategoryAdapter);

        //创建tab
        binding.indicator.removeAllTabs();
        for (int title : indicatorTitles) {
            binding.indicator.addTab(binding.indicator.newTab().setText(title));
        }
        searchCategoryAdapter.setDatum(Arrays.asList(indicatorTitles));
        binding.pager.setOffscreenPageLimit(indicatorTitles.length);

        loadPopularData();

        loadSearchHistoryData();
    }


    private void loadPopularData() {
        List<String> datum = new ArrayList<>();

        datum.add("爱学啊");
        datum.add("我的云音乐");
        datum.add("Android项目实战");
        datum.add("人生苦短");
        datum.add("我们只做好课");
        datum.add("android开发");
        datum.add("项目课程");

        //设置热门搜索数据
        setPopularData(datum);
    }

    private void setPopularData(List<String> datum) {
        for (String data : datum) {
            //循环每一个数据

            //创建布局
            ItemTagBinding itemTagBinding = ItemTagBinding.inflate(getLayoutInflater(), binding.list, false);

            //设置数据
            itemTagBinding.title.setText(data);

            //设置点击事件
            itemTagBinding.getRoot().setOnClickListener(v -> setSearchData(data));

            //添加控件
            headerSearchBinding.floatLayout.addView(itemTagBinding.getRoot());
        }
    }

    private void setSearchData(String data) {
        //将内容设置到搜索控件
        //并马上执行搜索
        searchView.setQuery(data, true);

        //进入搜索状态
        searchView.setIconified(false);

        //隐藏软键盘
        KeyboardUtil.hideKeyboard(getHostActivity());
    }

    private View createHeaderView() {
        headerSearchBinding = HeaderSearchBinding.inflate(getLayoutInflater(), binding.list, false);
        return headerSearchBinding.getRoot();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        searchHistoryAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                SearchHistory data = (SearchHistory) adapter.getItem(position);
                setSearchData(data.getContent());
            }
        });

        searchHistoryAdapter.addChildClickViewIds(R.id.delete);
        searchHistoryAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                SearchHistory data = (SearchHistory) adapter.getItem(position);
                getOrm().deleteSearchHistory(data);

                adapter.removeAt(position);

                refreshSearchHistoryTitleStatus();
            }
        });

        //pager滚动监听器，目的是实现滚动的时候，发送当前页搜索事件
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //执行搜索
                performSearch();
            }
        });
    }

    private void loadSearchHistoryData() {
        searchHistoryAdapter.setNewInstance(getOrm().querySearchHistory());

        refreshSearchHistoryTitleStatus();
    }

    private void refreshSearchHistoryTitleStatus() {
        SuperViewUtil.show(headerSearchBinding.searchHistoryTitle, searchHistoryAdapter.getItemCount() > 1);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        //查找搜索按钮
        MenuItem searchItem = menu.findItem(R.id.action_search);

        //查找搜索控件
        searchView = (SearchView) searchItem.getActionView();

        //可以在这里配置SearchView

        //设置搜索监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * 提交了搜索
             * 回车搜索调用两次
             * 点击键盘上搜索
             * @param query
             * @return
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                data = query;
                performSearch();
                return true;
            }

            /**
             * 搜索输入框文本改变了
             * @param newText
             * @return
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                prepareLoadSuggestion(newText);
                return true;
            }
        });

        //是否进入界面就打开搜索栏
        //false为默认打开
        //默认为true
        searchView.setIconified(false);

        //设置关闭监听器
        searchView.setOnCloseListener(() -> {
            //显示搜索历史控件
            showSearchHistoryView();
            return false;
        });

        //查找搜索建议控件
        searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        //默认要输入两个字符才显示提示，可以这样更改
        searchAutoComplete.setThreshold(1);

        //获取搜索管理器
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);

        //设置搜索信息
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //设置搜索建议点击回调
        searchAutoComplete.setOnItemClickListener((parent, view, position, id) -> setSearchData(suggestAdapter.getItem(position)));

        return true;
    }

    /**
     * 显示搜索历史控件
     */
    private void showSearchHistoryView() {
        binding.searchResultContainer.setVisibility(View.GONE);
        binding.list.setVisibility(View.VISIBLE);
    }

    /**
     * 显示搜索结果控件
     */
    private void showSearchResultView() {
        binding.searchResultContainer.setVisibility(View.VISIBLE);
        binding.list.setVisibility(View.GONE);
    }

    private void prepareLoadSuggestion(String data) {
        if (StringUtils.isBlank(data)) {
            return;
        }

        //取消原来的任务
        if (suggestionRunnable != null) {
            binding.pager.removeCallbacks(suggestionRunnable);
            suggestionRunnable = null;
        }

        //创建新的任务
        suggestionRunnable = () -> loadSuggestion(data);

        //500毫秒后执行
        binding.pager.postDelayed(suggestionRunnable, 500);
    }

    private void loadSuggestion(String data) {
        DefaultRepository.getInstance()
                .searchSuggest(data)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<Suggest>>() {
                    @Override
                    public void onSucceeded(DetailResponse<Suggest> data) {
                        setSuggest(data.getData());
                    }
                });
    }

    private void setSuggest(Suggest data) {
        //处理搜索建议数据

        //像变换这个中操作
        //如果是Kotlin语言中就一句话的事

        List<String> datum = new ArrayList<>();

        //处理歌单搜索建议
        if (data.getSheets() != null) {
            for (SuggestItem title : data.getSheets()) {
                datum.add(title.getTitle());
            }
        }

        //处理用户搜索建议
        if (data.getUsers() != null) {
            for (SuggestItem title : data.getUsers()
            ) {
                datum.add(title.getTitle());
            }
        }

        //创建适配器
        suggestAdapter = new ArrayAdapter<>(getHostActivity(),
                R.layout.item_suggest,
                R.id.title,
                datum);

        //设置到控件
        searchAutoComplete.setAdapter(suggestAdapter);
    }

    /**
     * 执行搜索
     */
    private void performSearch() {
        if (StringUtils.isEmpty(data)) {
            //没有数据直接返回
            return;
        }

        //保存搜索历史
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setContent(data);
        searchHistory.setCreatedAt(System.currentTimeMillis());

        getOrm().createOrUpdate(searchHistory);

        //显示搜索结果控件
        showSearchResultView();

        //发布搜索Key
        EventBus.getDefault().post(new SearchEvent(data, binding.pager.getCurrentItem()));

        loadSearchHistoryData();
    }

    /**
     * 物理按键返回调用
     */
    @Override
    public void onBackPressed() {
        if (searchView.isIconified()) {
            //不是在搜索状态

            //正常返回
            super.onBackPressed();
        } else {
            //是搜索状态

            //关闭搜索状态
            searchView.setIconified(true);
        }

    }
}