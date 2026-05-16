package com.ixuea.courses.mymusic.component.code.activity;

import static autodispose2.AutoDispose.autoDisposable;

import android.graphics.Bitmap;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.config.Config;
import com.ixuea.courses.mymusic.databinding.ActivityCodeBinding;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.StorageUtil;
import com.ixuea.superui.toast.SuperToast;
import com.ixuea.superui.util.SuperViewUtil;
import com.king.zxing.util.CodeUtils;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 二维码界面
 */
public class CodeActivity extends BaseTitleActivity<ActivityCodeBinding> {

    private String id;

    @Override
    protected void initDatum() {
        super.initDatum();
        id = extraId();
        loadData();
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        DefaultRepository.getInstance()
                .userDetail(id)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<User>>() {
                    @Override
                    public void onSucceeded(DetailResponse<User> data) {
                        showData(data.getData());
                    }
                });
    }

    private void showData(User data) {
        //头像
        ImageUtil.showAvatar(getHostActivity(), binding.icon, data.getIcon());

        //昵称
        binding.nickname.setText(data.getNickname());

        //我们这里的二维码的数据
        //就是一个网址
        //真实的数据在网址的查询参数里面
        //http://www.ixuea.com?u=
        String codeString = String.format(Config.USER_QRCODE_URL, data.getId());

        //生成二维码
        showCode(codeString);
    }


    /**
     * 生成二维码
     *
     * @param data
     */
    private void showCode(String data) {
        //生成二维码最好放子线程生成防止阻塞UI
        //这里只是演示
//        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//
//        //生成二维码
//        //logo会覆盖到二维码
//        //暂时没找到设置方法
//        //所以就不设置了
//        Bitmap bitmap =  CodeUtils.createQRCode(data, binding.code.getWidth(),logo);

        Bitmap bitmap = CodeUtils.createQRCode(data, binding.code.getWidth());

        //显示二维码
        binding.code.setImageBitmap(bitmap);
    }

    /**
     * 返回要显示的菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    /**
     * 菜单点击了
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveClick();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveClick() {
        //从View生成一张图片
        //Bitmap就是常说的位图
        Bitmap bitmap = SuperViewUtil.captureBitmap(binding.codeContainer);

        //保存到相册
        StorageUtil.savePicture(getHostActivity(), bitmap);
        SuperToast.success(R.string.success_save);
    }
}