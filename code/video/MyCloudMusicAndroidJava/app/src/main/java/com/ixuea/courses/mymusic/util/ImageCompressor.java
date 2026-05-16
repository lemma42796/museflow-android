package com.ixuea.courses.mymusic.util;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageCompressor {
    // 使用共享 Handler 实例
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    // 定义回调接口
    public interface CompressionCallback {
        void onCompressionComplete(String originalFilePath, String compressedFilePath);
        void onCompressionError(Exception e);
    }

    // 使用 ExecutorService 执行异步任务
    private static final ExecutorService executorService = Executors.newFixedThreadPool(4); // 可根据需要调整线程池大小

    public static void compressImagesAsync(Context context, List<Uri> imageUris, CompressionCallback callback) {
        for (Uri imageUri : imageUris) {
            compressImageAsync(context, imageUri, callback);
        }
    }

    public static void compressImageAsync(Context context, Uri imageUri, CompressionCallback callback) {
        executorService.submit(() -> {
            try {
                String originalFilePath = imageUri.toString();
                String compressedFilePath = compressImage(context, imageUri);
                // 在主线程中调用回调
                mainHandler .post(() -> callback.onCompressionComplete(originalFilePath, compressedFilePath));
            } catch (Exception e) {
                // 在主线程中调用回调
                mainHandler.post(() -> callback.onCompressionError(e));
            }
        });
    }

    private static String compressImage(Context context, Uri imageUri) throws IOException {
        // 定义最大边长为 1080 像素
        int maxSize = 1080;

        // 获取 ContentResolver
        ContentResolver contentResolver = context.getContentResolver();

        // 获取图像的输入流
        Bitmap compressedBitmap;
        try (InputStream inputStream = contentResolver.openInputStream(imageUri)) {
            // 从输入流中加载原始位图
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);

            // 计算压缩比例
            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;
            int newWidth = originalWidth;
            int newHeight = originalHeight;

            // 调整宽高，保持比例，并确保最大边不超过 1080 像素
            if (originalWidth > originalHeight) {
                if (originalWidth > maxSize) {
                    newWidth = maxSize;
                    newHeight = (originalHeight * maxSize) / originalWidth;
                }
            } else {
                if (originalHeight > maxSize) {
                    newHeight = maxSize;
                    newWidth = (originalWidth * maxSize) / originalHeight;
                }
            }

            // 使用新宽高加载压缩后的位图
            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight);

            // 需要重新获取输入流，因为前面已经关闭了
            try (InputStream inputStream2 = contentResolver.openInputStream(imageUri)) {
                compressedBitmap = BitmapFactory.decodeStream(inputStream2, null, options);
            }

            // 创建缩放后的位图
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(compressedBitmap, newWidth, newHeight, true);

            // 生成唯一的文件名，使用时间戳避免文件覆盖
            String uniqueFileName;
            String fileExtension = getFileExtension(context, imageUri);
            if ("png".equalsIgnoreCase(fileExtension)) {
                uniqueFileName = "compressed_" + System.currentTimeMillis() + ".png";
            } else {
                uniqueFileName = "compressed_" + System.currentTimeMillis() + ".jpg";
            }

            // 创建子目录
            File cacheDir = context.getExternalCacheDir();
            File subDir = new File(cacheDir, "compressed_images");
            if (!subDir.exists()) {
                subDir.mkdirs();
            }

            // 保存压缩文件
            File compressedFile = new File(subDir, uniqueFileName);
            try (FileOutputStream out = new FileOutputStream(compressedFile)) {
                if ("png".equalsIgnoreCase(fileExtension)) {
                    scaledBitmap.compress(Bitmap.CompressFormat.PNG, 6, out);
                } else {
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
                }
            }

            // 返回压缩文件的路径
            return compressedFile.getAbsolutePath();
        }
    }

    // 从 Uri 获取文件路径的方法
//    private static String getFilePathFromUri(Context context, Uri uri) {
//        String[] projection = {MediaStore.Images.Media.DATA};
//        try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {
//            if (cursor != null && cursor.moveToFirst()) {
//                return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
//            }
//        }
//        return null;
//    }

    // 获取文件扩展名的方法
    private static String getFileExtension(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.MIME_TYPE};
        try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
                return mimeType.substring(mimeType.lastIndexOf("/") + 1);
            }
        }
        return "";
    }

    // 计算采样率的方法
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
