package com.ixuea.courses.mymusic.component.scan.activity;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.zxing.Result;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.code.activity.CodeActivity;
import com.ixuea.courses.mymusic.component.user.activity.UserDetailActivity;
import com.ixuea.courses.mymusic.component.web.activity.WebActivity;
import com.ixuea.courses.mymusic.config.Config;
import com.ixuea.courses.mymusic.config.glide.GlideEngine;
import com.ixuea.courses.mymusic.databinding.ActivityScanBinding;
import com.ixuea.courses.mymusic.util.StringUtil;
import com.ixuea.superui.toast.SuperToast;
import com.ixuea.superui.util.SuperUrlUtil;
import com.king.zxing.CameraScan;
import com.king.zxing.DefaultCameraScan;
import com.king.zxing.util.CodeUtils;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;

/**
 * 扫一扫界面
 */
public class ScanActivity extends BaseTitleActivity<ActivityScanBinding> implements CameraScan.OnScanResultCallback {
    private DefaultCameraScan cameraScan;

    @Override
    protected void initViews() {
        super.initViews();
        //设置沉浸式状态栏
        QMUIStatusBarHelper.translucent(this);

        //状态栏文字白色
        QMUIStatusBarHelper.setStatusBarDarkMode(this);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        cameraScan = new DefaultCameraScan(this, binding.previewView);
        cameraScan.setOnScanResultCallback(this)
                .setVibrate(true)
                .startCamera();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        binding.code.setOnClickListener(v -> loginAfter(() ->
                startActivityExtraId(CodeActivity.class, sp.getUserId())
        ));

        binding.flashlight.setOnClickListener(v -> {
            cameraScan.enableTorch(!cameraScan.isTorchEnabled());

            binding.flashlight.setImageResource(
                    cameraScan.isTorchEnabled() ?
                            R.drawable.flashlight_on
                            :
                            R.drawable.flashlight_off
            );
        });

        binding.codeImage.setOnClickListener(v -> PictureSelector.create(getHostActivity())
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setMaxSelectNum(1)// 最大图片选择数量 int
                .setMinSelectNum(1)// 最小选择数量 int
                .setImageSpanCount(3)// 每行显示个数 int
                .setSelectionMode(SelectModeConfig.SINGLE)// 多选 or 单选 MULTIPLE or SINGLE
                .isPreviewImage(true)// 是否可预览图片 true or false
                .isDisplayCamera(true)// 是否显示拍照按钮 true or false
                .setCameraImageFormat(PictureMimeType.JPEG)// 拍照保存图片格式后缀,默认jpeg
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
                    public void onResult(ArrayList<LocalMedia> results) {
                        String result = CodeUtils.parseQRCode(results.get(0).getCompressPath());
                        processScanResult(result);
                    }

                    @Override
                    public void onCancel() {

                    }
                }));
    }

    /**
     * 处理扫描结果
     * 除了自己的二维码网址，其他的网址打开
     */
    private void processScanResult(String data) {
        if (TextUtils.isEmpty(data)) {
            showHint(R.string.error_empty_qrcode);
            return;
        }

        if (data.startsWith(Config.QRCODE_URL)) {
            //解析出网址中的查询参数
            Map<String, Object> query = SuperUrlUtil.getQueryMap(data);

            //获取值
            //获取用户id值
            String userId = (String) query.get("u");
            if (StringUtils.isNotBlank(userId)) {
                //有值
                processUserCode(userId);
            } else {
                //显示不支持该类型
                showHint(R.string.error_not_support_qrcode_format);
            }
        } else if (StringUtil.isUrl(data)) {
            //其他网址

            //直接打开
            WebActivity.start(getHostActivity(), "", data);
        } else {
            //直接内容显示到界面
            WebActivity.startWithData(getHostActivity(), getString(R.string.san_result), data);
        }

    }


    /**
     * 处理用户二维码
     *
     * @param data
     */
    private void processUserCode(String data) {
        //跳转到用户详情
        UserDetailActivity.startWithId(getHostActivity(), data);

        //关闭当前界面
        finish();
    }

    /**
     * 显示提示
     */
    private void showHint(int data) {
        //先暂停
        cameraScan.stopCamera();

        SuperToast.show(data);

        //延迟后启用扫描
        //目的是防止持续扫描不正确的二维码
        //可以根据需求调整
        binding.finderView.postDelayed(() -> cameraScan.startCamera(), 800);
    }

    /**
     * 扫描结果回调
     * true:连续扫描
     * false:单次扫描
     */
    @Override
    public boolean onScanResultCallback(Result data) {
        processScanResult(data.getText());

        return false;
    }

    @Override
    public void onScanResultFailure() {
    }
}