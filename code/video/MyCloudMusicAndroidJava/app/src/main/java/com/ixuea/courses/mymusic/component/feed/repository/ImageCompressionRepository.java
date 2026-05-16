package com.ixuea.courses.mymusic.component.feed.repository;

import android.content.Context;
import android.net.Uri;

import com.ixuea.courses.mymusic.util.ImageCompressor;

import java.util.List;

/**
 * Main-safe image compression entrypoint for publishing and image-message flows.
 */
public class ImageCompressionRepository {
    private static ImageCompressionRepository instance;

    public synchronized static ImageCompressionRepository getInstance() {
        if (instance == null) {
            instance = new ImageCompressionRepository();
        }
        return instance;
    }

    public void compressImages(Context context, List<Uri> imageUris, ImageCompressor.CompressionCallback callback) {
        ImageCompressor.compressImagesAsync(context, imageUris, callback);
    }
}
