package com.ixuea.courses.mymusic.component.cart.activity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.cart.adapter.ShopCartAdapter;
import com.ixuea.courses.mymusic.component.cart.model.Cart;
import com.ixuea.courses.mymusic.component.cart.ui.ShopCartViewModel;
import com.ixuea.courses.mymusic.component.order.activity.ConfirmOrderActivity;
import com.ixuea.courses.mymusic.databinding.ActivityShopCartBinding;
import com.ixuea.superui.dialog.SuperDialog;
import com.ixuea.superui.util.SuperViewUtil;

import java.math.BigDecimal;
import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * 购物车
 */
@AndroidEntryPoint
public class ShopCartActivity extends BaseTitleActivity<ActivityShopCartBinding> {

    private ShopCartViewModel viewModel;
    private ShopCartAdapter adapter;

    /**
     * 选中的购物车id
     */
    private ArrayList<String> selectCarts = new ArrayList<>();
    private MenuItem editMenuItem;

    @Override
    protected void initDatum() {
        super.initDatum();
        //创建ViewModel
        viewModel = new ViewModelProvider(this).get(ShopCartViewModel.class);

        adapter = new ShopCartAdapter(R.layout.item_shop_cart);
        binding.list.setAdapter(adapter);

        loadData();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //数据加载成功监听
        viewModel.loadDataSuccess.observe(this, data -> {
            adapter.setNewInstance(data);
        });

        //编辑成功
        viewModel.editSuccess.observe(this, data -> {
            itemSelectChanged();
        });

        //结算按钮点击
        binding.primary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmOrderActivity.startWithCarts(getHostActivity(), selectCarts);
            }
        });

        //选择所有点击
        binding.selectAll.setOnClickListener(v -> {
            if (isEmpty()) {
                return;
            }

            adapter.toggleSelectAll();
            itemSelectChanged();
            binding.selectIcon.setImageResource(adapter.isSelectAll() ? R.drawable.radio_button_checked : R.drawable.radio_button);
        });

        //删除成功
        viewModel.deleteSuccess.observe(this, data -> {
            //从购物列表删除，如果始终希望本地的列表和服务端完全保持一致
            //那这样删除完成后，可以调用列表接口重新获取
            adapter.deleteSelect();
            selectCarts.clear();

            if (isEmpty()) {
                //如果没有数据了，关闭购物车界面

                //当然也可以显示为空提示，并退出编辑模式
                finish();
            }
        });

        //删除按钮点击
        binding.delete.setOnClickListener(v -> SuperDialog.newInstance(getSupportFragmentManager())
                .setTitleRes(R.string.confirm_delete)
                .setOnClickListener(data -> viewModel.deleteCarts(selectCarts)).show());

        //item子控件点击
        adapter.addChildClickViewIds(R.id.select_icon, R.id.decrement, R.id.increment);
        adapter.setOnItemChildClickListener((a, view, position) -> {
            Cart data = adapter.getItem(position);

            switch (view.getId()) {
                case R.id.select_icon:
                    //选择图标点击
                    data.toggleSelect();
                    itemSelectChanged();
                    break;
                case R.id.decrement:
                    //数量-点击
                    if (data.getCount() > 1) {
                        data.setCount(data.getCount() - 1);
                        viewModel.editCart(data);
                    }
                    break;
                case R.id.increment:
                    //数量+点击
                    if (data.getCount() < 100) {
                        data.setCount(data.getCount() + 1);
                        viewModel.editCart(data);
                    }
                    break;
            }

            adapter.notifyItemChanged(position);

        });
    }

    private boolean isEmpty() {
        return adapter.getItemCount() == 0;
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        viewModel.loadData();
    }

    /**
     * item选择状态改变了
     * 计算商品价格，可以请求服务端计算
     * 当然也可以在本地计算，因为这个界面只是试算，后面确订单服务端计算
     */
    private void itemSelectChanged() {
        selectCarts.clear();
        Cart it;

        if (isEditModel()) {
            //编辑模式，不计算价格

            int count = 0;
            for (int i = 0; i < adapter.getItemCount(); i++) {
                it = adapter.getItem(i);
                if (it.isSelect()) {
                    count++;
                    selectCarts.add(it.getId());
                }
            }

            binding.delete.setEnabled(count > 0);
        } else {
            //一定要使用BigDecimal

            //总价
            BigDecimal total = new BigDecimal(0);
            BigDecimal productPrice = null;

            int count = 0;
            for (int i = 0; i < adapter.getItemCount(); i++) {
                it = adapter.getItem(i);
                if (it.isSelect()) {
                    count++;
                    selectCarts.add(it.getId());

                    //商品单价
                    productPrice = new BigDecimal(String.valueOf(it.getProduct().getPrice()));

                    //单价乘以数量
                    total = total.add(productPrice.multiply(new BigDecimal(it.getCount())));
                }
            }

            double totalPriceDouble = total.doubleValue();
            binding.price.setText(getString(R.string.price, totalPriceDouble));

            boolean isSelectProduct = count > 0;
            binding.primary.setEnabled(isSelectProduct);
            if (isSelectProduct) {
                binding.primary.setText(getString(R.string.settle_account_count, count));
            } else {
                binding.primary.setText(R.string.settle_account);
            }
        }

    }

    /**
     * 是否是编辑模型
     *
     * @return
     */
    private boolean isEditModel() {
        return binding.editControlContainer.getVisibility() == View.VISIBLE;
    }

    /**
     * 返回菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_text, menu);
        //查找编辑按钮
        editMenuItem = menu.findItem(R.id.edit);
        return true;
    }

    /**
     * 按钮点击了
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit) {
            onEditClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onEditClick() {
        if (isEmpty()) {
            return;
        }

        if (isEditModel()) {
            exitEditModel();
        } else {
            //现在是结算状态

            //进入编辑状态
            editMenuItem.setTitle(R.string.complete);
            SuperViewUtil.gone(binding.settleControlContainer);
            SuperViewUtil.show(binding.editControlContainer);
        }
    }

    private void exitEditModel() {
        editMenuItem.setTitle(R.string.edit);
        SuperViewUtil.show(binding.settleControlContainer);
        SuperViewUtil.gone(binding.editControlContainer);

        //计算一次价格
        itemSelectChanged();
    }
}