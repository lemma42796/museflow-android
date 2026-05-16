package com.ixuea.courses.mymusic.component.address.activity;

import static autodispose2.AutoDispose.autoDisposable;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.address.model.Address;
import com.ixuea.courses.mymusic.component.address.model.request.DataRequest;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.config.glide.GlideEngine;
import com.ixuea.courses.mymusic.databinding.ActivityAddressDetailBinding;
import com.ixuea.courses.mymusic.model.Base;
import com.ixuea.courses.mymusic.model.BaseId;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.selector.region.Region;
import com.ixuea.selector.region.RegionSelector;
import com.ixuea.superui.toast.SuperToast;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.CropFileEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.request.PermissionBuilder;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropImageEngine;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.core.Observable;
import timber.log.Timber;
import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;

/**
 * 收货地址详情
 */
public class AddressDetailActivity extends BaseTitleActivity<ActivityAddressDetailBinding> {

    private String id;
    private Address data;
    private EventManager voiceRecognitionEventManager;
    private boolean isVoiceInput;
    /**
     * 百度语音识别事件监听器
     * <p>
     * https://ai.baidu.com/ai-doc/SPEECH/4khq3iy52
     */
    EventListener voiceRecognitionEventListener = new EventListener() {
        /**
         * 事件回调
         * @param name 回调事件名称
         * @param params 回调参数
         * @param data 数据
         * @param offset 开始位置
         * @param length 长度
         */
        @Override
        public void onEvent(String name, String params, byte[] data, int offset, int length) {
            String result = "name: " + name;

            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
                // 引擎就绪，可以说话，一般在收到此事件后通过UI通知用户可以说话了
                setStopVoiceRecognition();
            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
                // 一句话的临时结果，最终结果及语义结果

                if (params == null || params.isEmpty()) {
                    return;
                }

                // 识别相关的结果都在这里
                try {
                    JSONObject paramObject = new JSONObject(params);

                    //获取第一个结果
                    JSONArray resultsRecognition = paramObject.getJSONArray("results_recognition");

                    String voiceRecognitionResult = resultsRecognition.getString(0);

                    //可以根据result_type是临时结果，还是最终结果

                    binding.input.setText(voiceRecognitionResult);
                    result += voiceRecognitionResult;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
                //一句话识别结束（可能含有错误信息） 。最终识别的文字结果在ASR_PARTIAL事件中

                if (params.contains("\"error\":0")) {

                } else if (params.contains("\"error\":7")) {
                    SuperToast.show(R.string.voice_error_no_result);
                } else {
                    //其他错误
                    SuperToast.show(getString(R.string.voice_error, params));
                }
            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_EXIT)) {
                //识别结束，资源释放
                setStartVoiceRecognition();
            }

            Timber.d("baidu voice recognition onEvent %s", result);
        }
    };
    private HashMap<String, Object> voiceRecognitionParams;

    @Override
    protected void initDatum() {
        super.initDatum();
        id = extraId();

        //初始化语言识别事件管理器，可以用到的时候再初始化
        voiceRecognitionEventManager = EventManagerFactory.create(this, "asr");

        //注册事件监听器
        voiceRecognitionEventManager.registerListener(voiceRecognitionEventListener);

        loadData();
    }

    /**
     * 设置开始语言输入状态
     */
    private void setStartVoiceRecognition() {
        isVoiceInput = false;
        binding.voiceInput.setText(R.string.voice_input);
    }

    /**
     * 设置停止语言输入状态
     */
    private void setStopVoiceRecognition() {
        isVoiceInput = true;
        binding.voiceInput.setText(R.string.stop_voice_input);
    }

    @Override
    protected void loadData() {
        super.loadData();

        if (!TextUtils.isEmpty(id)) {
            DefaultRepository.getInstance()
                    .addressDetail(id)
                    .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe(new HttpObserver<DetailResponse<Address>>(getHostActivity(), true) {
                        @Override
                        public void onSucceeded(DetailResponse<Address> data) {
                            showData(data.getData());
                        }
                    });
        }

    }

    private void showData(Address data) {
        this.data = data;

        binding.name.setText(data.getName());
        binding.phone.setText(data.getTelephone());
        binding.detailAddress.setText(data.getDetail());
        binding.defaultAddress.setChecked(data.isDefault());

        showArea();
    }

    private void showArea() {
        binding.area.setText(data.getReceiverArea());
    }

    /**
     * 界面结果回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //请求成功了

            switch (requestCode) {
                case RegionSelector.REQUEST_REGION:
                    //城市选择

                    //省
                    Region province = RegionSelector.getProvince(data);

                    //市
                    Region city = RegionSelector.getCity(data);

                    //区
                    Region area = RegionSelector.getArea(data);

                    checkCreateAddress();

                    //设置数据
                    //省
                    this.data.setProvince(province.getName());
                    this.data.setProvinceCode(String.valueOf(province.getId()));

                    //市
                    this.data.setCity(city.getName());
                    this.data.setCityCode(String.valueOf(city.getId()));

                    //区
                    this.data.setArea(area.getName());
                    this.data.setAreaCode(String.valueOf(area.getId()));

                    //显示地区
                    showArea();
                    break;
            }
        }
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //选择图片点击
        binding.selectImage.setOnClickListener(v -> {
            //可以等选择了图片，在初始化
            AppContext.getInstance().initOCR();

            selectImage();
        });

        //语音输入点击
        binding.voiceInput.setOnClickListener(v -> {
            checkPermission();
        });

        binding.recognition.setOnClickListener(v -> {
            String content = binding.input.getText().toString().trim();
            if (StringUtils.isBlank(content)) {
                SuperToast.show(R.string.enter_content);
                return;
            }

            recognitionAddressClick(content);
        });

        //地区点击
        binding.area.setOnClickListener(v -> {
            //这里直接用省市区选择器选择，如果还需要用定位，像发布动态那样实现就行了
            RegionSelector.getInstance(this).start(this);
        });
    }

    private void selectImage() {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setMaxSelectNum(1)// 最大图片选择数量 int
                .setMinSelectNum(1)// 最小选择数量 int
                .setImageSpanCount(3)// 每行显示个数 int
                .setSelectionMode(SelectModeConfig.SINGLE)// 多选 or 单选 MULTIPLE or SINGLE
                .isPreviewImage(true)// 是否可预览图片 true or false
                .isDisplayCamera(true)// 是否显示拍照按钮 true or false
                .setCameraImageFormat(PictureMimeType.JPEG)// 拍照保存图片格式后缀,默认jpeg
                .setCropEngine(new CropFileEngine() {
                    @Override
                    public void onStartCrop(Fragment fragment, Uri srcUri, Uri destinationUri, ArrayList<String> dataSource, int requestCode) {
                        // 注意* 如果你实现自己的裁剪库，需要在Activity的.setResult();
                        // Intent中需要给MediaStore.EXTRA_OUTPUT，塞入裁剪后的路径；如果有额外数据也可以通过CustomIntentKey.EXTRA_CUSTOM_EXTRA_DATA字段存入；

                        UCrop uCrop = UCrop.of(srcUri, destinationUri, dataSource);
                        uCrop.setImageEngine(new UCropImageEngine() {
                            @Override
                            public void loadImage(Context context, String url, ImageView imageView) {
                                Glide.with(context).load(url).into(imageView);
                            }

                            @Override
                            public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
                                Glide.with(context).asBitmap().override(maxWidth, maxHeight).load(url).into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        if (call != null) {
                                            call.onCall(resource);
                                        }
                                    }

                                    @Override
                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                        if (call != null) {
                                            call.onCall(null);
                                        }
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                    }
                                });
                            }
                        });
                        uCrop.withOptions(buildOptions());
                        uCrop.start(fragment.getActivity(), fragment, requestCode);
                    }
                })
                //压缩
                .setCompressEngine(new CompressFileEngine() {
                    @Override
                    public void onStartCompress(Context context, ArrayList<Uri> source, OnKeyValueResultCallbackListener call) {
                        Luban.with(context).load(source).ignoreBy(100)
                                .setCompressListener(new OnNewCompressListener() {
                                    @Override
                                    public void onStart() {

                                    }

                                    @Override
                                    public void onSuccess(String source, File compressFile) {
                                        if (call != null) {
                                            call.onCallback(source, compressFile.getAbsolutePath());
                                        }
                                    }

                                    @Override
                                    public void onError(String source, Throwable e) {
                                        if (call != null) {
                                            call.onCallback(source, null);
                                        }
                                    }
                                }).launch();
                    }
                })
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        recognitionImage(result.get(0).getCutPath());
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    private void recognitionImage(String data) {
        GeneralBasicParams param = new GeneralBasicParams();
        param.setDetectDirection(true);
        param.setImageFile(new File(data));

        // 调用通用文字识别服务
        OCR.getInstance(getApplicationContext()).recognizeGeneralBasic(param, new OnResultListener<GeneralResult>() {

            /**
             * 成功
             * @param result
             */
            @Override
            public void onResult(GeneralResult result) {
                StringBuilder builder = new StringBuilder();
                for (WordSimple it : result.getWordList()) {
                    builder.append(it.getWords());

                    //每一项之间，添加空格，方便OCR失败
                    builder.append(" ");
                }

                binding.input.setText(builder.toString());
            }

            /**
             * 失败
             * @param error
             */
            @Override
            public void onError(OCRError error) {
                SuperToast.show(getString(R.string.ocr_error, error.getMessage(), error.getErrorCode()));
            }
        });
    }

    /**
     * 配制UCrop，可根据需求自我扩展
     *
     * @return
     */
    private UCrop.Options buildOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        options.setShowCropFrame(true);
        options.setShowCropGrid(true);
        options.isCropDragSmoothToCenter(false);
        return options;
    }

    private void recognitionAddressClick(String data) {
        DataRequest param = new DataRequest(data);
        DefaultRepository.getInstance()
                .recognitionAddress(param)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<Address>>() {
                    @Override
                    public void onSucceeded(DetailResponse<Address> data) {
                        showData(data.getData());
                    }
                });
    }

    /**
     * 返回菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_address_detail, menu);
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
        switch (item.getItemId()) {
            case R.id.save:
                saveClick();
                return true;
            case R.id.delete:
                deleteClick();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteClick() {
        if (TextUtils.isEmpty(id)) {
            //如果要实现新建的时候，隐藏删除按钮
            //可以像用户详情那样找到按钮设置
            return;
        }

        DefaultRepository.getInstance()
                .deleteAddress(id)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<Base>>() {
                    @Override
                    public void onSucceeded(DetailResponse<Base> d) {
                        finish();
                        SuperToast.show(R.string.success_delete);
                    }
                });
    }

    private void saveClick() {
        String name = binding.name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            SuperToast.show(R.string.enter_name);
            return;
        }

        String phone = binding.phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            SuperToast.show(R.string.enter_phone);
            return;
        }

        String detail = binding.detailAddress.getText().toString().trim();
        if (TextUtils.isEmpty(detail)) {
            SuperToast.show(R.string.enter_detail_address);
            return;
        }

        checkCreateAddress();
        data.setName(name);
        data.setTelephone(phone);
        data.setDetail(detail);
        data.setDefaultAddress(binding.defaultAddress.isChecked() ? Constant.VALUE10 : Constant.VALUE0);
        Observable<DetailResponse<BaseId>> api;
        if (TextUtils.isEmpty(id)) {
            //新建
            api = DefaultRepository.getInstance()
                    .createAddress(data);
        } else {
            api = DefaultRepository.getInstance()
                    .updateAddress(id, data);
        }
        api
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<BaseId>>() {
                    @Override
                    public void onSucceeded(DetailResponse<BaseId> data) {
                        finish();
                    }
                });

    }

    private void checkCreateAddress() {
        if (data == null) {
            data = new Address();
        }
    }

    /**
     * 返回语音识别参数
     * <p>
     * https://ai.baidu.com/ai-doc/SPEECH/5khq3i39w#%E9%89%B4%E6%9D%83%E4%BF%A1%E6%81%AF
     *
     * @return
     */
    private Map<String, Object> getVoiceRecognitionParams() {
        if (voiceRecognitionParams == null) {
            voiceRecognitionParams = new HashMap<>();

            //普通话（默认），可以不用设置
            voiceRecognitionParams.put(SpeechConstant.PID, 1537);

            //语音活动检测， 根据静音时长自动断句。注意不开启长语音的情况下，SDK只能录制60s音频。长语音请设置BDS_ASR_ENABLE_LONG_SPEECH参数
            //VAD_DNN:新一代VAD，各方面信息优秀，推荐使用。
            voiceRecognitionParams.put(SpeechConstant.VAD, "VAD_DNN");

            //不开启长语音。开启VAD尾点检测，即静音判断的毫秒数。
            voiceRecognitionParams.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 800);

            voiceRecognitionParams.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        }

        return voiceRecognitionParams;
    }


    /**
     * 开始语音识别
     */
    public void startVoiceRecognition() {
        Map<String, Object> params = getVoiceRecognitionParams();
        String json = new JSONObject(params).toString();
        voiceRecognitionEventManager.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }

    /**
     * 停止语音识别，提前结束录音等待识别结果
     */
    public void stopVoiceRecognition() {
        voiceRecognitionEventManager.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
    }

    @Override
    protected void onDestroy() {
        releaseVoiceRecognition();
        super.onDestroy();
    }

    /**
     * 释放语音识别相关资源
     */
    private void releaseVoiceRecognition() {
        if (voiceRecognitionEventManager == null) {
            return;
        }
        stopVoiceRecognition();

        // SDK 集成步骤（可选），卸载listener
        voiceRecognitionEventManager.unregisterListener(voiceRecognitionEventListener);
        voiceRecognitionEventManager = null;
    }

    //region 动态处理录音权限
    //也可以在启动界面一次获取，这里讲解用到的时候再获取权限
    //真实项目中推荐用到的时候在获取，但目前大部分真实项目都是在启动界面一次性获取大部分权限
    private void checkPermission() {
        //让动态框架检查是否授权了

        //如果不使用框架就使用系统提供的API检查
        //它内部也是使用系统API检查
        //只是使用框架就更简单了
        PermissionBuilder r = PermissionX.init(this).permissions(
                Manifest.permission.RECORD_AUDIO
        );
        r.request((allGranted, grantedList, deniedList) -> {
            if (allGranted) {
                binding.getRoot().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        prepareNext();
                    }
                }, 1000);
            } else {
                //可以在这里弹出提示告诉用户为什么需要权限
                finish();
            }
        });
    }

    private void prepareNext() {
        if (isVoiceInput) {
            stopVoiceRecognition();
        } else {
            startVoiceRecognition();
        }
    }
    //endregion
}