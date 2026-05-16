package com.ixuea.courses.mymusic.component.feed.task;

import android.os.AsyncTask;

import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.BuildConfig;
import com.ixuea.courses.mymusic.component.oss.AliyunStorage;
import com.ixuea.courses.mymusic.config.Config;
import com.ixuea.courses.mymusic.model.Resource;
import com.ixuea.superui.util.UUIDUtil;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * 上传文件任务
 */
public class UploadFeedImageAsyncTask extends AsyncTask<List<LocalMedia>, Integer, Result<List<Resource>>> {
    @Override
    protected Result<List<Resource>> doInBackground(List<LocalMedia>... params) {
        //创建结果数组
        List<Resource> results = new ArrayList<Resource>();

        //获取oss客户端
        OSSClient storage = AliyunStorage.getInstance(AppContext.getInstance()).getOssClient();

        List<LocalMedia> resources = params[0];

        try {
            LocalMedia it;
            for (int i = 0; i < resources.size(); i++) {
                it = resources.get(i);

                //上传
                //OSS如果没有特殊需求建议不要分目录
                //如果一定要分不要让目录名前面连续
                //例如时间戳倒过来
                //不然连续请求达到一定量级会有性能影响
                //https://help.aliyun.com/document_detail/64945.html

                //这里加上环境目的是，方便清理测试数据
                String destFileName = String.format("%s%s.jpg", UUIDUtil.getUUID(), BuildConfig.FLAVOR);

                //创建上传文件请求
                //上传其他文件也是这样的
                //他不关心文件具体内容
                PutObjectRequest request = new PutObjectRequest(
                        Config.ALIYUN_OSS_BUCKET_NAME,
                        destFileName,
                        it.getCompressPath()
                );

                //上传
                PutObjectResult putObjectResult = storage.putObject(request);

                //如果上传成功
                //将文件名添加到集合
                //这里没有很好的处理错误
                results.add(new Resource(destFileName));

                publishProgress(i + 1);
            }

            return Result.success(results);
        } catch (Exception e) {
            //出错了

            //返回异常，这里不能抛出异常
            return Result.error(e);
        }
    }
}
