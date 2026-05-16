package com.ixuea.courses.mymusic.component.about.activity;

import android.view.View;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.config.Config;
import com.ixuea.courses.mymusic.databinding.ActivityAboutBinding;
import com.ixuea.courses.mymusic.util.IntentUtil;
import com.ixuea.superui.util.SuperPackageUtil;
import com.ixuea.superui.util.SuperViewUtil;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * 关于界面
 */
public class AboutActivity extends BaseTitleActivity<ActivityAboutBinding> {
    @Override
    protected void initViews() {
        super.initViews();
        if (Config.DEBUG) {
            SuperViewUtil.show(binding.createException);
            SuperViewUtil.show(binding.throwException);
        }
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        //获取版本名称
        String versionName = SuperPackageUtil.getVersionName(getHostActivity());

        //版本号
        long versionCode = SuperPackageUtil.getVersionCode(getHostActivity());

        String version = getResources().getString(R.string.version_value, versionName, versionCode);
        binding.version.setMoreText(version);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        binding.version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //参数1：isManual 用户手动点击检查，非用户点击操作请传false
                //参数2：isSilence 是否显示弹窗等交互，[true:没有弹窗和toast] [false:有弹窗或toast]
                Beta.checkUpgrade(true, false);
            }
        });

        //关于爱学啊点击
        binding.aboutUs.setOnClickListener(v -> {
            //这里演示用系统浏览器打开
            IntentUtil.startBrowser(getHostActivity(), Config.QRCODE_URL);
        });

        //触发一个异常，让SDK自动捕捉
        binding.createException.setOnClickListener(v -> {
            CrashReport.testJavaCrash();
        });

        //抛出一个异常
        binding.throwException.setOnClickListener(v -> {
            try {
                //模拟崩溃，自己捕捉，然后手动上传到umeng
                throw new IllegalArgumentException("参数错误，模拟崩溃，手动上传");
            } catch (Exception e) {
                //https://bugly.qq.com/docs/user-guide/advance-features-android/
                CrashReport.postCatchedException(e);
            }
        });
    }
}