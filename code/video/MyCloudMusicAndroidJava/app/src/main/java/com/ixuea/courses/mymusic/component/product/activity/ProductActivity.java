package com.ixuea.courses.mymusic.component.product.activity;

import static autodispose2.AutoDispose.autoDisposable;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.product.adapter.DropDownGridMenuAdapter;
import com.ixuea.courses.mymusic.component.product.adapter.ProductAdapter;
import com.ixuea.courses.mymusic.component.product.model.Product;
import com.ixuea.courses.mymusic.component.product.ui.DropDownListMenuAdapter;
import com.ixuea.courses.mymusic.component.product.ui.DropMenuItem;
import com.ixuea.courses.mymusic.component.product.ui.ProductViewModel;
import com.ixuea.courses.mymusic.databinding.ActivityProductBinding;
import com.ixuea.courses.mymusic.databinding.DropdownPhoneBrandBinding;
import com.ixuea.courses.mymusic.exception.ResponseException;
import com.ixuea.courses.mymusic.model.response.BaseResponse;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.ExceptionHandlerUtil;
import com.ixuea.superui.decoration.GridDividerItemDecoration;
import com.ixuea.superui.util.DensityUtil;
import com.ixuea.superui.util.SuperRecyclerViewUtil;
import com.ixuea.superui.util.SuperViewUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * 商城界面
 */
@AndroidEntryPoint
public class ProductActivity extends BaseTitleActivity<ActivityProductBinding> {
//    @Inject
//    ProductService service;
//
//    @Inject
//    ProductRepository repository;

    private ProductAdapter adapter;
    private GridLayoutManager layoutManager;
    private ProductViewModel viewModel;
    private MenuItem changeStyleMenuItem;
    private SearchView searchView;
    private List<View> dropdownMenuContentViews = new ArrayList<>();
    private boolean isScrollTop;

    @Override
    protected void initViews() {
        super.initViews();
        //刷新箭头颜色
        binding.refresh.setColorSchemeResources(R.color.primary);

        //刷新圆圈颜色
        binding.refresh.setProgressBackgroundColorSchemeResource(R.color.white);

        binding.list.setHasFixedSize(true);

        GridDividerItemDecoration itemDecoration = new GridDividerItemDecoration(getHostActivity(), (int) DensityUtil.dip2px(getHostActivity(), 10F));
        binding.list.addItemDecoration(itemDecoration);

        layoutManager = new GridLayoutManager(getHostActivity(), 1);
        binding.list.setLayoutManager(layoutManager);

        //添加下拉菜单
        addListPopupMenuView(Arrays.asList(
                new DropMenuItem(R.string.comprehensive, Constant.VALUE0),
                new DropMenuItem(R.string.price_desc, Constant.VALUE10),
                new DropMenuItem(R.string.price_asc, Constant.VALUE20),
                new DropMenuItem(R.string.id_asc, Constant.VALUE30)
        ));

        addGridPopupMenuView(Arrays.asList(getResources().getStringArray(R.array.phone_brand).clone()));

        binding.dropdown.setupDropDownMenu(
                Arrays.asList(
                        getString(R.string.comprehensive),
                        getString(R.string.brand)
                ),
                dropdownMenuContentViews
        );
    }

    private void addGridPopupMenuView(List<String> menuData) {
        DropdownPhoneBrandBinding phoneBandBinding = DropdownPhoneBrandBinding.inflate(getLayoutInflater(), null, false);
        phoneBandBinding.list.setHasFixedSize(true);

        //布局管理器
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getHostActivity(), 2);
        phoneBandBinding.list.setLayoutManager(layoutManager);

        DropDownGridMenuAdapter menuAdapter = new DropDownGridMenuAdapter(R.layout.item_dropdown);
        menuAdapter.setOnItemClickListener((adapter, view, position) -> {
            ((DropDownGridMenuAdapter) adapter).setSelect(position);
        });
        phoneBandBinding.list.setAdapter(menuAdapter);

        //重置按钮点击
        phoneBandBinding.reset.setOnClickListener(v -> {
            binding.dropdown.closeMenu();
            menuAdapter.resetSelect();

            //设置当前tab标题
            binding.dropdown.setTabText(R.string.brand, 1);

            setScrollTop();

            viewModel.setBrand(null);
        });

        //确定点击
        phoneBandBinding.confirm.setOnClickListener(v -> {
            binding.dropdown.closeMenu();

            List<String> selects = menuAdapter.getSelect();

            String title;
            if (selects.size() > 1) {
                title = String.format("%s...", selects.get(0));
            } else if (selects.size() > 0) {
                title = selects.get(0);
            } else {
                title = getString(R.string.brand);
            }

            //设置当前tab标题
            binding.dropdown.setTabText(title, 1);

            setScrollTop();

            viewModel.setBrand(selects);
        });

        menuAdapter.setNewInstance(menuData);

        dropdownMenuContentViews.add(phoneBandBinding.getRoot());
    }

    /**
     * 添加单选列表下拉菜单
     *
     * @param menuData
     */
    private void addListPopupMenuView(List<DropMenuItem> menuData) {
        RecyclerView recyclerView = new RecyclerView(getHostActivity());
        recyclerView.setBackgroundResource(R.drawable.shape_surface);

        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(recyclerView);

        DropDownListMenuAdapter menuAdapter = new DropDownListMenuAdapter(R.layout.item_dropdown);
        menuAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                //关闭下拉菜单
                binding.dropdown.closeMenu();

                DropMenuItem menuItem = (DropMenuItem) adapter.getItem(position);

                //设置当前tab标题
                binding.dropdown.setTabText(menuItem.getTitle(), 0);

                //设置当前位置选中
                ((DropDownListMenuAdapter) adapter).setSelect(position);

                setScrollTop();

                viewModel.setSort(menuItem.getValue());
            }
        });
        recyclerView.setAdapter(menuAdapter);
        dropdownMenuContentViews.add(recyclerView);

        menuAdapter.setNewInstance(menuData);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
//        service = NetworkModule.provideRetrofit(NetworkModule.provideOkHttpClient()).create(ProductService.class);
//        repository = new ProductRepository(service);

        //创建ViewModel
//        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
//            @NonNull
//            @Override
//            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//                return (T) new ProductViewModel(repository);
//            }
//        }).get(ProductViewModel.class);

        //使用依赖注入后
        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        adapter = new ProductAdapter(getHostActivity(), viewModel);
        binding.list.setAdapter(adapter);

        loadData();
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
//        viewModel.products()
//                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
//                .subscribe(new HttpObserver<ListResponse<Product>>() {
//                    @Override
//                    public void onSucceeded(ListResponse<Product> data) {
//                        adapter.setDatum(data.getData().getData());
//                    }
//                });

        viewModel.products()
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(result -> {
                    adapter.submitData(getLifecycle(), result);
                });

//        repository.products(10, null, null)
//                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
//                .subscribe(new HttpObserver<ListResponse<Product>>(this) {
//                    @Override
//                    public void onSucceeded(ListResponse<Product> data) {
//                        adapter.setDatum(data.getData().getData());
//                    }
//                });
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        viewModel.loadData.observe(this, data -> loadData());

        //监听要打开详情界面事件
        viewModel.detail.observe(this, position -> {
            Product data = adapter.snapshot().get(position);
            startActivityExtraId(ProductDetailActivity.class, data.getId());
        });

        binding.refresh.setOnRefreshListener(() -> adapter.refresh());

        //监听适配器加载状态，包括错误处理
        //主要是实现，筛选，排序，搜索更改后，列表滚动到顶部
        //https://developer.android.google.cn/topic/libraries/architecture/paging/load-state#java
        adapter.addLoadStateListener(state -> {
            //是否加载中
            boolean isLoading = state.getRefresh() instanceof LoadState.Loading;
            binding.refresh.setRefreshing(isLoading);

            //处理空列表
            boolean isEmpty = state.getRefresh() instanceof LoadState.NotLoading && adapter.getItemCount() == 0;
            showEmpty(isEmpty);

            //处理错误状态
            boolean isError = state.getRefresh() instanceof LoadState.Error;
            if (isError) {
                SuperViewUtil.show(binding.placeholderView, true);

                BaseResponse errorData = null;
                Throwable error = null;

                LoadState.Error loadStateError = (LoadState.Error) state.getRefresh();
                if (loadStateError.getError() instanceof ResponseException) {
                    //如果是网络响应错误，例如：参数不正确等情况，就解析出网络返回对象
                    ResponseException responseException = (ResponseException) loadStateError.getError();
                    errorData = responseException.getData();
                } else {
                    error = loadStateError.getError();
                }

                ExceptionHandlerUtil.handlerRequest(errorData, error, getPlaceholderView());
            }

            if (isScrollTop) {
                binding.list.postDelayed(() -> binding.list.scrollToPosition(0), 300);
                isScrollTop = false;
            }

            return null;
        });

        //提示控件点击
        binding.placeholderView.setOnClickListener(v -> {
            //重试
            adapter.retry();
        });
    }

    private void setScrollTop() {
        isScrollTop = true;
    }

    private void showEmpty(boolean data) {
        SuperViewUtil.show(binding.placeholderView, data);
        SuperViewUtil.gone(binding.list, data);
        binding.placeholderView.showTitle(R.string.no_result);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product, menu);
        changeStyleMenuItem = menu.findItem(R.id.change_style);

        //查找搜索按钮
        android.view.MenuItem searchItem = menu.findItem(R.id.search);

        //查找搜索控件
        searchView = (SearchView) searchItem.getActionView();

        //可以在这里配置SearchView

        //设置搜索监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * 提交了搜索
             * 回车搜索调用两次
             * 点击键盘上搜索
             *
             * @param query
             * @return
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                setScrollTop();
                viewModel.setQuery(query);
                return true;
            }

            /**
             * 搜索输入框文本改变了
             *
             * @param newText
             * @return
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        //设置关闭监听器
        searchView.setOnCloseListener(() -> {
            setScrollTop();

            //显示搜索历史控件
            viewModel.setQuery(null);
            return false;
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_style:
                changeListStyle();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeListStyle() {
        if (layoutManager.getSpanCount() == 2) {
            adapter.setListStyle(true);
            layoutManager.setSpanCount(1);
            changeStyleMenuItem.setIcon(R.drawable.style_grid);
        } else {
            adapter.setListStyle(false);
            layoutManager.setSpanCount(2);
            changeStyleMenuItem.setIcon(R.drawable.style_list);
        }
    }
}