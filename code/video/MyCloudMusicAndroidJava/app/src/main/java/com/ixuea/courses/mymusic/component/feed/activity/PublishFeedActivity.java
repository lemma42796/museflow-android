package com.ixuea.courses.mymusic.component.feed.activity;

import static autodispose2.AutoDispose.autoDisposable;

import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.adapter.TextWatcherAdapter;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.feed.adapter.ImageAdapter;
import com.ixuea.courses.mymusic.component.feed.model.Feed;
import com.ixuea.courses.mymusic.component.feed.model.event.FeedChangedEvent;
import com.ixuea.courses.mymusic.component.feed.repository.FeedPublishRepository;
import com.ixuea.courses.mymusic.component.feed.repository.ImageCompressionRepository;
import com.ixuea.courses.mymusic.component.feed.ui.FeedPublishViewModel;
import com.ixuea.courses.mymusic.config.glide.GlideEngine;
import com.ixuea.courses.mymusic.databinding.ActivityPublishFeedBinding;
import com.ixuea.courses.mymusic.model.Base;
import com.ixuea.courses.mymusic.model.Resource;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.model.response.ListResponse;
import com.ixuea.courses.mymusic.util.ImageCompressor;
import com.ixuea.superui.decoration.GridDividerItemDecoration;
import com.ixuea.superui.toast.SuperToast;
import com.ixuea.superui.util.DensityUtil;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 发布动态界面
 */
public class PublishFeedActivity extends BaseTitleActivity<ActivityPublishFeedBinding> {
    private String content;

    /**
     * 动态
     */
    private Feed feed = new Feed();
    private ImageAdapter adapter;
    private FeedPublishViewModel viewModel;
    private FeedPublishRepository publishRepository;

    @Override
    protected void initViews() {
        super.initViews();
        //设置布局管理器
        GridLayoutManager layoutManager = new GridLayoutManager(getHostActivity(), 4);
        binding.list.setLayoutManager(layoutManager);

        GridDividerItemDecoration itemDecoration = new GridDividerItemDecoration(getHostActivity(), (int) DensityUtil.dip2px(getHostActivity(), 5F));
        binding.list.addItemDecoration(itemDecoration);
        binding.position.setVisibility(View.GONE);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        publishRepository = FeedPublishRepository.getInstance();
        viewModel = new ViewModelProvider(this).get(FeedPublishViewModel.class);

        adapter = new ImageAdapter(R.layout.item_image);
        binding.list.setAdapter(adapter);

        viewModel.getMediaItems().observe(this, this::setData);
        viewModel.setSelectedImages(new ArrayList<>());
    }

    private void setData(List<Object> datum) {
        adapter.setNewInstance(new ArrayList<>(datum));
    }

    /**
     * 返回菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.publish, menu);
        return true;
    }

    /**
     * 按钮点击了
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.publish) {
            sendClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        binding.content.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                String result = getString(R.string.feed_count, s.toString().length());
                binding.count.setText(result);
            }
        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (adapter.getItem(position) instanceof Integer) {
                    selectImage();
                }
            }
        });

        adapter.addChildClickViewIds(R.id.close);
        adapter.setOnItemChildClickListener((adapter, view, position) -> viewModel.removeSelectedImage(position));

    }

    /**
     * 选择图片
     */
    private void selectImage() {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setMaxSelectNum(9)// 最大图片选择数量 int
                .setMinSelectNum(1)// 最小选择数量 int
                .setImageSpanCount(3)// 每行显示个数 int
                .setSelectionMode(SelectModeConfig.MULTIPLE)// 多选 or 单选 MULTIPLE or SINGLE
                .isPreviewImage(true)// 是否可预览图片 true or false
                .isDisplayCamera(true)// 是否显示拍照按钮 true or false
                .setCameraImageFormat(PictureMimeType.JPEG)// 拍照保存图片格式后缀,默认jpeg
                //自定义压缩
                .setCompressEngine(new CompressFileEngine() {
                    @Override
                    public void onStartCompress(Context context, ArrayList<Uri> arrayList, OnKeyValueResultCallbackListener onKeyValueResultCallbackListener) {
                        ImageCompressionRepository.getInstance().compressImages(context, arrayList, new ImageCompressor.CompressionCallback() {
                            @Override
                            public void onCompressionComplete(String originalFilePath, String compressedFilePath) {
                            Log.d("TAG", "onStartCompress: "+originalFilePath+","+compressedFilePath);

                                // 将压缩后的文件路径通过回调返回
                                onKeyValueResultCallbackListener.onCallback(originalFilePath , compressedFilePath);
                            }

                            @Override
                            public void onCompressionError(Exception e) {

                            }
                        });
                    }
                })
                //Luban压缩框架好像有bug，部分图片没有压缩
//                .setCompressEngine(new CompressFileEngine() {
//                    @Override
//                    public void onStartCompress(Context context, ArrayList<Uri> source, OnKeyValueResultCallbackListener call) {
//                        Luban.with(context).load(source).ignoreBy(100)
//                                .setCompressListener(new OnNewCompressListener() {
//                                    @Override
//                                    public void onStart() {
//
//                                    }
//
//                                    @Override
//                                    public void onSuccess(String source, File compressFile) {
//                                        if (call != null) {
//                                            Log.d("TAG", "onSuccess: "+compressFile.getAbsolutePath());
//                                            call.onCallback(source, compressFile.getAbsolutePath());
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onError(String source, Throwable e) {
//                                        if (call != null) {
//                                            call.onCallback(source, null);
//                                        }
//                                    }
//                                }).launch();
//                    }
//                })
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        viewModel.setSelectedImages(result);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    private void sendClick() {
        //获取输入的内容
        content = binding.content.getText().toString().trim();

        //判断是否输入了
        if (StringUtils.isBlank(content)) {
            SuperToast.error(R.string.hint_feed);
            return;
        }

        //判断长度
        //至于为什么是140
        //市面上大部分软件都是这样
        //大家感兴趣可以搜索下
        if (content.length() > 140) {
            SuperToast.error(R.string.error_content_length);
            return;
        }

        //获取选中的图片
        List<LocalMedia> selectedImages = getSelectedImages();
        if (selectedImages.size() > 0) {
            //有图片

            //先上传图片
            uploadImages(selectedImages);
        } else {
            //没有图片

            //直接发布动态
            saveFeed(null);
        }
    }

    /**
     * 获取选中的图片
     *
     * @return
     */
    private List<LocalMedia> getSelectedImages() {
        return viewModel.getSelectedImages();
    }

    private void uploadImages(List<LocalMedia> datum) {
        showLoading(getString(R.string.loading_upload, 1));
        publishRepository.uploadImages(datum)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<ListResponse<Resource>>() {
                    @Override
                    public boolean onFailed(ListResponse<Resource> data, Throwable e) {
                        hideLoading();
                        return super.onFailed(data, e);
                    }

                    @Override
                    public void onSucceeded(ListResponse<Resource> data) {
                        hideLoading();
                        List<Resource> results = data.getData().getData();
                        if (results != null && results.size() == datum.size()) {
                            saveFeed(results);
                        } else {
                            SuperToast.show(R.string.error_upload_image);
                        }
                    }
                });
    }

    private void saveFeed(List<Resource> results) {
        //真实项目中应该由服务端判断发送设备，避免客户端破解后，可以设置任意值
        feed.setContent(String.format("%s\n📱来自【Android Java云音乐客户端】",content));
        feed.setMedias(results);

        publishRepository.createFeed(feed)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<Base>>() {
                    @Override
                    public void onSucceeded(DetailResponse<Base> data) {
                        //发布通知
                        EventBus.getDefault().post(new FeedChangedEvent());

                        //关闭界面
                        finish();
                    }
                });
    }

}
