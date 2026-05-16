package com.ixuea.courses.mymusic.component.feed.repository;

import com.ixuea.courses.mymusic.BuildConfig;
import com.ixuea.courses.mymusic.component.feed.model.Feed;
import com.ixuea.courses.mymusic.model.Base;
import com.ixuea.courses.mymusic.model.Resource;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.model.response.ListResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.luck.picture.lib.entity.LocalMedia;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Repository for feed publishing and media upload.
 */
public class FeedPublishRepository {
    private static FeedPublishRepository instance;
    private final DefaultRepository repository;

    private FeedPublishRepository(DefaultRepository repository) {
        this.repository = repository;
    }

    public synchronized static FeedPublishRepository getInstance() {
        if (instance == null) {
            instance = new FeedPublishRepository(DefaultRepository.getInstance());
        }
        return instance;
    }

    public Observable<ListResponse<Resource>> uploadImages(List<LocalMedia> data) {
        ArrayList<MultipartBody.Part> bodyFiles = new ArrayList<>();
        for (LocalMedia it : data) {
            String path = mediaPath(it);
            if (StringUtils.isBlank(path)) {
                continue;
            }

            File file = new File(path);
            RequestBody fileBody = RequestBody.Companion.create(file, MediaType.parse("image/*"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
            bodyFiles.add(body);
        }

        RequestBody flavorBody = RequestBody.Companion.create(
                BuildConfig.FLAVOR,
                MediaType.Companion.parse("multipart/form-data")
        );
        return repository.uploadFiles(bodyFiles, flavorBody);
    }

    public Observable<DetailResponse<Base>> createFeed(Feed data) {
        return repository.createFeed(data);
    }

    private String mediaPath(LocalMedia data) {
        if (StringUtils.isNotBlank(data.getCompressPath())) {
            return data.getCompressPath();
        }

        return data.getPath();
    }
}
