package com.ixuea.courses.mymusic.component.user.fragment;

import static autodispose2.AutoDispose.autoDisposable;

import android.os.Bundle;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.databinding.FragmentUserDetailAboutBinding;
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.Constant;

import org.apache.commons.lang3.StringUtils;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 用户详情-关于界面
 */
public class UserDetailAboutFragment extends BaseViewModelFragment<FragmentUserDetailAboutBinding> {

    private String id;

    public static UserDetailAboutFragment newInstance(String id) {

        Bundle args = new Bundle();
        args.putString(Constant.ID, id);

        UserDetailAboutFragment fragment = new UserDetailAboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        id = extraId();

        //可以再次请求网络接口
        //也可以把用户对象传递过来
        DefaultRepository.getInstance()
                .userDetail(id)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<User>>() {
                    @Override
                    public void onSucceeded(DetailResponse<User> data) {
                        show(data.getData());
                    }
                });
    }

    private void show(User data) {
        //昵称
        binding.nickname.setText(getResources()
                .getString(R.string.nickname_value, data.getNickname()));

        //性别
        binding.gender.setText(getResources()
                .getString(R.string.gender_value, data.getGenderFormat()));

        //生日
        binding.birthday.setText(getResources()
                .getString(R.string.birthday_value, data.birthdayFormat()));

        //地区
        String area = "";
        if (StringUtils.isNotEmpty(data.getProvince())) {
            //有省市区

            StringBuilder sb = new StringBuilder();

            //拼接地区
            sb.append(data.getProvince());
            sb.append("-");
            sb.append(data.getCity());
            sb.append("-");
            sb.append(data.getArea());

            area = sb.toString();
        }

        binding.area.setText(getResources().getString(R.string.area_value, area));

        //描述
        binding.description.setText(getResources()
                .getString(R.string.description_value, data.getDescriptionFormat()));
    }
}
