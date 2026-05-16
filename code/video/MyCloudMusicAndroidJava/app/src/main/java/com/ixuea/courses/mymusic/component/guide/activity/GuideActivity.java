package com.ixuea.courses.mymusic.component.guide.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.ixuea.courses.mymusic.MainActivity;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseViewModelActivity;
import com.ixuea.courses.mymusic.component.api.DefaultService;
import com.ixuea.courses.mymusic.component.guide.adapter.GuideAdapter;
import com.ixuea.courses.mymusic.config.Config;
import com.ixuea.courses.mymusic.databinding.ActivityGuideBinding;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.SuperDarkUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 左右滚动的引导界面
 * 如果想实现更复杂的效果，例如：滚动时文本缩放等效果，可以使用类似这样的框架：
 * https://github.com/bingoogolapple/BGABanner-Android
 */
public class GuideActivity extends BaseViewModelActivity<ActivityGuideBinding> implements View.OnClickListener {

    private static final String TAG = "GuideActivity";
    private GuideAdapter adapter;
    private DefaultService service;

    @Override
    protected void initViews() {
        super.initViews();
        //设置沉浸式状态栏
        QMUIStatusBarHelper.translucent(this);

        if (SuperDarkUtil.isDark(this)) {
            //状态栏文字白色
            QMUIStatusBarHelper.setStatusBarDarkMode(this);
        } else {
            //状态栏文字黑色
            QMUIStatusBarHelper.setStatusBarLightMode(this);
        }
    }

    @Override
    protected void initDatum() {
        super.initDatum();
//        OkHttpClient okHttpClient = NetworkModule.provideOkHttpClient();
//        Retrofit retrofit = NetworkModule.provideRetrofit(okHttpClient);
//        service = retrofit.create(DefaultService.class);

        //创建适配器
        adapter = new GuideAdapter(getHostActivity(), getSupportFragmentManager());

        //设置适配器到控件
        binding.list.setAdapter(adapter);

        //让指示器根据列表控件配合工作
        binding.indicator.setViewPager(binding.list);

        //适配器注册数据源观察者
        adapter.registerDataSetObserver(binding.indicator.getDataSetObserver());

        //准备数据
        List<Integer> datum = new ArrayList<>();
        datum.add(R.drawable.guide1);
        datum.add(R.drawable.guide2);
        datum.add(R.drawable.guide3);
        datum.add(R.drawable.guide4);
        datum.add(R.drawable.guide5);

        //设置数据到适配器
        adapter.setDatum(datum);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        binding.loginOrRegister.setOnClickListener(this);
        binding.experienceNow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_or_register:
                Intent intent = new Intent(getHostActivity(), MainActivity.class);
                intent.setAction(Constant.ACTION_LOGIN);
                startActivity(intent);

                setShowGuide();

                finish();
                break;
            case R.id.experience_now:
                startActivityAfterFinishThis(MainActivity.class);

                setShowGuide();
//                testGet();
//                testRetrofitGet();
                break;
        }
    }

    /**
     * retrofit get请求
     */
    private void testRetrofitGet() {
//        service.sheets(null, 2)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ListResponse<Sheet>>() {
//                    @Override
//                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ListResponse<Sheet> data) {
//                        Sheet sheet = data.getData().getData().get(0);
//                        Log.d(TAG, "onNext: " + sheet.getTitle());
//                    }
//
//                    @Override
//                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
//                        Log.d(TAG, "onError: " + e.getLocalizedMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });

//        service.comments()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ListResponse<Comment>>() {
//                    @Override
//                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ListResponse<Comment> data) {
//                        Comment sheet = data.getData().getData().get(0);
//                        Log.d(TAG, "onNext: " + sheet.getContent());
//                    }
//
//                    @Override
//                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
//                        Log.d(TAG, "onError: " + e.getLocalizedMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });

//        service.sheetDetail("ixuea", "1")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<DetailResponse<Sheet>>() {
//                    @Override
//                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull DetailResponse<Sheet> data) {
//                        Log.d(TAG, "onNext: " + data.getData().getTitle());
//                    }
//
//                    @Override
//                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
//                        Log.e(TAG, "onError: " + e.getLocalizedMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });

//        service.sheetDetail("ixuea", "1")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new ObserverAdapter<DetailResponse<Sheet>>() {
//                    @Override
//                    public void onNext(DetailResponse<Sheet> sheetDetailResponse) {
//                        super.onNext(sheetDetailResponse);
//                    }
//                });

//        Toast.makeText(getHostActivity(), R.string.about_ta, Toast.LENGTH_SHORT).show();

//        SuperToast.success(R.string.about);

//        SuperRoundLoadingDialogFragment dialogFragment = SuperRoundLoadingDialogFragment.newInstance("拼命加载中.");
//        dialogFragment.show(getSupportFragmentManager(),"SuperRoundLoadingDialogFragment");

//        showLoading("拼命加载中.");
//
//        binding.indicator.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                hideLoading();
//            }
//        },3000);

//        service.sheetDetail("ixuea", "998888")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new HttpObserver<DetailResponse<Sheet>>(getHostActivity(), true) {
//                    @Override
//                    public void onSucceeded(DetailResponse<Sheet> data) {
//                        Log.d(TAG, "onSucceeded: " + data.getData().getTitle());
//                    }
//
//                    @Override
//                    public boolean onFailed(DetailResponse<Sheet> data, Throwable e) {
//                        Log.e(TAG, "onFailed: " + e.getLocalizedMessage());
//                        return false;
//                    }
//                });
    }

    /**
     * okhttp get请求
     */
    private void testGet() {
        OkHttpClient client = new OkHttpClient();

        String url = Config.ENDPOINT + "v1/sheets";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "get error: " + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "get success: " + response.body().string());
            }
        });
    }

    private void setShowGuide() {
        sp.setShowGuide(false);
    }
}
