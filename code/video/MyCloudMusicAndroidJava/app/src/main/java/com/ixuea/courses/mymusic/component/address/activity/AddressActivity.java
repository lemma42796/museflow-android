package com.ixuea.courses.mymusic.component.address.activity;

import static autodispose2.AutoDispose.autoDisposable;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.address.adapter.AddressAdapter;
import com.ixuea.courses.mymusic.component.address.model.Address;
import com.ixuea.courses.mymusic.component.address.model.event.AddressChangedEvent;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.databinding.ActivityAddressBinding;
import com.ixuea.courses.mymusic.model.response.ListResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.Constant;

import org.greenrobot.eventbus.EventBus;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 收货地址界面
 */
public class AddressActivity extends BaseTitleActivity<ActivityAddressBinding> {

    private AddressAdapter adapter;
    private int style;

    /**
     * 选择地址
     *
     * @param context
     */
    public static void startWithSelect(Context context) {
        Intent intent = new Intent(context, AddressActivity.class);
        intent.putExtra(Constant.STYLE, Constant.VALUE10);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(false);
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);

        DefaultRepository.getInstance()
                .addresses()
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<ListResponse<Address>>(getHostActivity(), true) {
                    @Override
                    public void onSucceeded(ListResponse<Address> data) {
                        adapter.setNewInstance(data.getData().getData());
                    }
                });
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        style = extraInt(Constant.STYLE);

        adapter = new AddressAdapter(R.layout.item_address);
        binding.list.setAdapter(adapter);
    }

    /**
     * 返回菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    /**
     * 菜单点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add) {
            //添加按钮
            onAddClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onAddClick() {
        startActivity(AddressDetailActivity.class);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        adapter.setOnItemClickListener((adapter, view, position) -> {
            Address data = (Address) adapter.getItem(position);
            if (Constant.VALUE10 == style) {
                EventBus.getDefault().post(new AddressChangedEvent(data));
                finish();
            } else {
                startActivityExtraId(AddressDetailActivity.class, data.getId());
            }

        });
    }
}