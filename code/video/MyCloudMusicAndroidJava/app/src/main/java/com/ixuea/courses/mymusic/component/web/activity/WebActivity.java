package com.ixuea.courses.mymusic.component.web.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.databinding.ActivityWebBinding;
import com.ixuea.courses.mymusic.util.Constant;

/**
 * 通用WebView界面
 */
public class WebActivity extends BaseTitleActivity<ActivityWebBinding> {
    private String title;

    /**
     * 定义静态的启动方法
     * 好处是用户只要看到声明
     * 就知道该界面需要哪些参数
     *
     * @param activity
     * @param title
     * @param url
     */
    public static void start(Activity activity, String title, String url) {
        //创建Intent
        Intent intent = new Intent(activity, WebActivity.class);
        //添加标题
        intent.putExtra(Constant.TITLE_KEY, title);

        //添加url
        intent.putExtra(Constant.URL, url);

        //启动界面
        activity.startActivity(intent);
    }

    /**
     * 定义静态的启动方法
     * 好处是用户只要看到声明
     * 就知道该界面需要哪些参数
     *
     * @param activity
     * @param url
     */
    public static void start(Activity activity, String url) {
        //创建Intent
        Intent intent = new Intent(activity, WebActivity.class);

        //添加url
        intent.putExtra(Constant.URL, url);

        //启动界面
        activity.startActivity(intent);
    }

    /**
     * 显示字符串html
     *
     * @param activity
     * @param title
     * @param data
     */
    public static void startWithData(Activity activity, String title, String data) {
        //创建Intent
        Intent intent = new Intent(activity, WebActivity.class);
        intent.putExtra(Constant.TITLE_KEY, title);
        intent.putExtra(Constant.DATA, data);
        activity.startActivity(intent);
    }

    @Override
    protected void initViews() {
        super.initViews();
        //获取webview设置
        WebSettings webSettings = binding.web.getSettings();

        //运行访问文件
        webSettings.setAllowFileAccess(true);

        //支持多窗口
        webSettings.setSupportMultipleWindows(true);

        //开启js支持
        webSettings.setJavaScriptEnabled(true);

        //显示图片
        webSettings.setBlockNetworkImage(false);

        //运行显示手机中的网页
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        //运行文件访问
        webSettings.setAllowFileAccessFromFileURLs(true);

        //运行dom存储
        webSettings.setDomStorageEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //运行混合类型
            //也就说支持网页中有http/https
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        binding.web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.startsWith("http://") && url.startsWith("https://")) {
                    //只加载网址协议
                    view.loadUrl(url);
                    return true;
                }
                return false;
            }
        });

        binding.web.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (TextUtils.isEmpty(WebActivity.this.title)) {
                    //如果没有不传递标题，就用网址标题
                    setTitle(title);
                }
            }

            /**
             * 进度改变了
             *
             * @param view
             * @param newProgress
             */
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                binding.progress.setWebProgress(newProgress);
            }

            /**
             * html里面点击全屏调用
             *
             * @param view
             * @param callback
             */
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                switchFullScreen();

                binding.fullContainer.setVisibility(View.VISIBLE);
                binding.fullContainer.addView(view);
            }

            /**
             * html退出全屏调用
             */
            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                switchFullScreen();

                binding.fullContainer.setVisibility(View.GONE);
                binding.fullContainer.removeAllViews();

            }
        });

        //进度条高亮颜色
        binding.progress.setColor(getColor(R.color.primary));
    }

    @Override
    protected void initDatum() {
        super.initDatum();

        //获取传递的数据
        title = extraString(Constant.TITLE_KEY);
        String url = getIntent().getStringExtra(Constant.URL);
        String data = getIntent().getStringExtra(Constant.DATA);

        if (!TextUtils.isEmpty(title)) {
            //设置标题
            setTitle(title);
        }

        if (!TextUtils.isEmpty(url)) {
            //加载网址
            binding.web.loadUrl(url);
        } else if (!TextUtils.isEmpty(data)) {
            //加载字符串html
            binding.web.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
        }
    }

    private void switchFullScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            //竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        binding.web.destroy();
        super.onDestroy();
    }
}