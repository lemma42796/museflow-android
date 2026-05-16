package com.ixuea.courses.mymusic.component.setting.activity;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.databinding.ActivitySettingBinding;

/**
 * 设置界面
 * <p>
 * 这里使用系统提供的偏好设置功能实现，样式和最新Android系统（大概10及以上）设置界面差不多
 * 好处是他会根据不同的组件自动保存，获取偏好设置的值
 * 如果要实现为侧滑那种样式，直接在布局里面写相应的布局就行了
 */
public class SettingActivity extends BaseTitleActivity<ActivitySettingBinding> {

    @Override
    protected void initViews() {
        super.initViews();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new SettingFragment())
                .commit();
    }

    public static class SettingFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.setting_preferences, rootKey);
        }
    }
}