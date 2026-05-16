package com.ixuea.courses.mymusic.component.profile.activity;

import static autodispose2.AutoDispose.autoDisposable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.BuildConfig;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.oss.AliyunStorage;
import com.ixuea.courses.mymusic.component.profile.fragment.GenderDialogFragment;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.component.user.model.event.UserChangedEvent;
import com.ixuea.courses.mymusic.config.Config;
import com.ixuea.courses.mymusic.config.glide.GlideEngine;
import com.ixuea.courses.mymusic.databinding.ActivityProfileBinding;
import com.ixuea.courses.mymusic.model.Base;
import com.ixuea.courses.mymusic.model.Resource;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.StringUtil;
import com.ixuea.courses.mymusic.util.SuperDateUtil;
import com.ixuea.selector.region.Region;
import com.ixuea.selector.region.RegionSelector;
import com.ixuea.superui.toast.SuperToast;
import com.ixuea.superui.util.UUIDUtil;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.CropFileEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropImageEngine;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;

/**
 * 我的资料界面
 */
public class ProfileActivity extends BaseTitleActivity<ActivityProfileBinding> {

    private User data;
    private String nickname;
    private String description;
    private String iconFileName;

    @Override
    protected void initDatum() {
        super.initDatum();
        loadData();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //头像容器点击
        binding.iconContainer.setOnClickListener(v -> selectAvatar());

        //性别点击
        binding.genderContainer.setOnClickListener(v -> GenderDialogFragment
                .show(getSupportFragmentManager(), data.getGender() / 10, (dialog, which) -> {
                    //关闭对话框
                    dialog.dismiss();

                    //转换性别格式
                    switch (which) {
                        case 1:
                            //男
                            data.setGender(User.MALE);
                            break;
                        case 2:
                            //女
                            data.setGender(User.FEMALE);
                            break;
                        default:
                            //保密
                            data.setGender(User.GENDER_UNKNOWN);
                            break;
                    }

                    //显示性别
                    showGender();
                }));

        //生日点击
        binding.birthdayContainer.setOnClickListener(v -> {
            //当前日期前可以选择，包括今天
            DateValidatorPointBackward before = DateValidatorPointBackward.now();

            //日期约束
            CalendarConstraints calendarConstraints = new CalendarConstraints.Builder()
                    .setValidator(CompositeDateValidator.allOf(Arrays.asList(before)))
                    .build();

            //创建选择器
            MaterialDatePicker<Long> fragment = MaterialDatePicker.Builder.datePicker()
                    .setCalendarConstraints(calendarConstraints)
                    .build();

            //确定点击
            fragment.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    data.setBirthday(SuperDateUtil.yyyyMMdd(selection));
                    showBirthday();
                }
            });

            fragment.show(getSupportFragmentManager(), "MaterialDatePicker");
        });

        //地区点击
        binding.areaContainer.setOnClickListener(v -> RegionSelector.getInstance(this).start(this));
    }

    private void selectAvatar() {
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
                        uploadIcon(result.get(0).getCutPath());
                    }

                    @Override
                    public void onCancel() {

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
        options.setFreeStyleCropEnabled(false);
        options.setShowCropFrame(true);
        options.setShowCropGrid(true);
        options.withAspectRatio(500, 500);
        options.isCropDragSmoothToCenter(false);
        options.setMaxScaleMultiplier(500);
        return options;
    }

    private void uploadIcon(String data) {
        //上传到服务端
        File file = new File(data);

        //文件表单项
        RequestBody fileBody=RequestBody.Companion.create(file, MediaType.parse("image/*"));
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), fileBody);

        //渠道项
        RequestBody flavorBody = RequestBody.Companion.create(BuildConfig.FLAVOR,MediaType.Companion.parse("multipart/form-data"));

        DefaultRepository.getInstance()
                .uploadFile(multipartBody, flavorBody)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<Resource>>() {
                    @Override
                    public void onSucceeded(DetailResponse<Resource> data) {
                        processUploadIconResult(data.getData().getUri());
                    }
                });

        //上传到阿里云OSS
        //课程里面不在使用这种方式
        //但如果你的项目需要使用，大体可以按照这种方式写
//        new UploadIconAsyncTask(this, data).execute(data);
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        DefaultRepository.getInstance()
                .userDetail(sp.getUserId())
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<User>>() {
                    @Override
                    public void onSucceeded(DetailResponse<User> data) {
                        next(data.getData());
                    }
                });
    }

    private void next(User data) {
        this.data = data;

        //头像
        ImageUtil.showAvatar(getHostActivity(), binding.icon, data.getIcon());

        //昵称
        binding.nickname.setText(data.getNickname());

        //性别
        showGender();

        //生日
        showBirthday();

        //地区
        showArea();

        //描述
        if (StringUtils.isNotBlank(data.getDetail())) {
            binding.description.setText(data.getDetail());
        }

        //手机号
        binding.phone.setText(data.getPhone());

        //邮箱
        binding.email.setText(data.getEmail());

    }

    /**
     * 显示地区
     */
    private void showArea() {
        if (StringUtils.isNotBlank(data.getProvince())) {
            String area = getResources().getString(R.string.area_value2,
                    data.getProvince(),
                    data.getCity(),
                    data.getArea());
            binding.area.setText(area);
        }
    }

    /**
     * 显示生日
     */
    private void showBirthday() {
        if (StringUtils.isNotBlank(data.getBirthday())) {
            binding.birthday.setText(data.getBirthday());
        }
    }

    /**
     * 显示性别
     */
    private void showGender() {
        binding.gender.setText(data.getGenderFormat());
    }

    /**
     * 返回菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
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
        //这里就不判断用户是否更改了资料
        //只要点击保存就更新
        //因为要判断比较麻烦

        //获取昵称
        nickname = binding.nickname.getText().toString().trim();

        //判断是否输入昵称
        if (TextUtils.isEmpty(nickname)) {
            SuperToast.show(R.string.enter_nickname);
            return;
        }

        if (!StringUtil.isNickname(nickname)) {
            SuperToast.show(R.string.error_nickname_format);
        }

        //获取描述
        description = binding.description.getText().toString().trim();

        //更新资料
        updateUserInfo();
    }

    private void updateUserInfo() {
        if (StringUtils.isNotBlank(iconFileName)) {
            //设置头像
            data.setIcon(iconFileName);
        }

        if (StringUtils.isNotBlank(nickname)) {
            //设置昵称
            data.setNickname(nickname);
        }

        //设置描述
        data.setDetail(description);

        DefaultRepository.getInstance()
                .updateUser(sp.getUserId(), data)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<Base>>() {
                    @Override
                    public void onSucceeded(DetailResponse<Base> data) {
                        //更新成功
                        //不提示任何信息

                        //关闭当前界面
                        //当然也可以不关闭
                        //真实项目中根据业务需求调整就行了

                        //发送通知
                        EventBus.getDefault().post(new UserChangedEvent());

                        //关闭界面
                        finish();
                    }
                });
    }

    private void processUploadIconResult(String data) {
        if (StringUtils.isNotBlank(data)) {
            //上传成功
            iconFileName = data;

            //更新用户资料
            updateUserInfo();
        } else {
            iconFileName = null;

            //头像上传失败
            //真实项目中
            //可以实现重试
            //同时重试的时候
            //只上传失败的图片
            SuperToast.show(R.string.error_upload_image);
        }
    }

    /**
     * 上传头像到阿里云OSS异步任务
     *
     * @deprecated 课程不在使用阿里云OSS，使用{@link DefaultRepository#uploadFiles(List, RequestBody)}方法上传到API服务端
     * 但你的项目如果要使用阿里云OSS，那可以参考如下写法
     */
    @Deprecated
    private static class UploadIconAsyncTask extends AsyncTask<String, Void, String> {
        /**
         * 弱引用保存界面，防止出现内存泄漏
         */
        private final WeakReference<ProfileActivity> activityReference;
        private final String path;

        public UploadIconAsyncTask(ProfileActivity activity, String path) {
            activityReference = new WeakReference<>(activity);
            this.path = path;
        }

        /**
         * 在子线程中执行
         *
         * @param strings
         * @return
         */
        @Override
        protected String doInBackground(String... strings) {
            try {
                //获取oss客户端
                OSSClient storage = AliyunStorage.getInstance(AppContext.getInstance()).getOssClient();

                //上传
                //OSS如果没有特殊需求建议不要分目陆
                //如果一定要分不要让目陆名前面连续
                //例如时间戳倒过来
                //不然连续请求达到一定量级会有性能影响
                //https://help.aliyun.com/document_detail/64945.html
                String iconFileName = String.format("%s%s.jpg", UUIDUtil.getUUID(), BuildConfig.FLAVOR);

                //创建上传文件请求
                //上传其他文件也是这样的
                //他不关心文件具体内容
                PutObjectRequest request = new PutObjectRequest(
                        Config.ALIYUN_OSS_BUCKET_NAME,
                        iconFileName,
                        path
                );

                //上传
                PutObjectResult putObjectResult = storage.putObject(request);

                //上传成功
                return iconFileName;
            } catch (Exception e) {
                //上传失败了
                e.printStackTrace();
            }

            return null;
        }

        /**
         * 异步任务执行完成后调用
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ProfileActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.processUploadIconResult(result);

        }
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
        if (RESULT_OK == resultCode) {
            //请求成功了
            switch (requestCode) {
                case RegionSelector.REQUEST_REGION:
                    //城市选择

                    //城市选择
                    //这里的Id和iOS那边城市选择框架的Id不一样
                    //这里我们没有用到所以没多大影响
                    //真实项目中要保持一致

                    //省
                    Region province = RegionSelector.getProvince(data);

                    //市
                    Region city = RegionSelector.getCity(data);

                    //区
                    Region area = RegionSelector.getArea(data);

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
    protected String pageId() {
        return "Profile";
    }
}