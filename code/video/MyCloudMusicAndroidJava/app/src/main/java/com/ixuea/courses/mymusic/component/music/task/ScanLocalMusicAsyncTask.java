package com.ixuea.courses.mymusic.component.music.task;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.LiteORMUtil;
import com.ixuea.courses.mymusic.util.StorageUtil;
import com.ixuea.superui.util.BitmapUtil;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * 扫描本地音乐异步任务
 */
public class ScanLocalMusicAsyncTask extends AsyncTask<Void, String, List<Song>> {
    /**
     * 系统媒体库音乐封面路径
     */
    private static final Uri URI_ARTWORK = Uri.parse("content://media/external/audio/albumart");

    private final Context context;

    public ScanLocalMusicAsyncTask(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * 子线程中执行
     *
     * @param voids
     * @return
     */
    @Override
    @SuppressLint("Range")
    protected List<Song> doInBackground(Void... voids) {
        //创建结果列表
        List<Song> datum = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();

        /**
         * 使用内容提供者查询
         * 我们这里是查询音乐大小大于1M，时间大于60秒
         * @param uri 资源标识符
         * @param projection 选择那些字段
         * @param selection 条件
         * @param selectionArgs 条件参数
         * @param sortOrder 排序
         */
        //新版Android（大概是10以及以上）手动导入到sdcard的音乐，媒体库扫描后的数据在/data/data/com.google.android.providers.media.module/databases/external.db数据库文件
        //这样查询的是上面数据库中的files表
        Cursor cursor = contentResolver.query(
                //查询的地址
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,

                //要返回数据的字段
                new String[]{
                        //媒体Id
                        BaseColumns._ID,

                        //媒体标题
                        MediaStore.Audio.AudioColumns.TITLE,

                        //艺术家（歌手）
                        MediaStore.Audio.AudioColumns.ARTIST,

                        //专辑
                        MediaStore.Audio.AudioColumns.ALBUM,

                        //专辑id
                        MediaStore.Audio.AudioColumns.ALBUM_ID,

                        //文件路径
                        MediaStore.Audio.AudioColumns.DATA,

                        //显示的名称
                        //例如:"爱的代价.mp3"
                        MediaStore.Audio.AudioColumns.DISPLAY_NAME,

                        //文件大小
                        //单位：字节
                        MediaStore.Audio.AudioColumns.SIZE,

                        //时长
                        //单位：毫秒
                        MediaStore.Audio.AudioColumns.DURATION
                },

                //查询条件
                Constant.MEDIA_AUDIO_SELECTION,

                //查询参数
                new String[]{
                        String.valueOf(Constant.MUSIC_FILTER_SIZE),
                        String.valueOf(Constant.MUSIC_FILTER_DURATION),
                },

                //排序方式
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        );

        //遍历数据
        while (cursor != null && cursor.moveToNext()) {
            //有数据

            //遍历每一行数据

            //像封面这些信息
            //从文件中获取的很有可能不对
            //所以真实项目中
            //一般是根据标题，歌手，专辑等信息
            //去服务端匹配封面，歌词等信息
            //这里我们就不在实现该功能了

            //媒体id
            long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));

            //媒体标题
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));

            //艺术家（歌手）
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));

            //专辑
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));

            //专辑id
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));

            //文件路径
            //DATA列在Android Q以下版本代表了文件路径
            //但在 Android Q上该路径无法被访问

            //获取这个路径的目的是
            //扫描的时候显示到界面上
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));

            //需要通过这种方式方法
            //该方法也兼容Android Q以下版本
            String uri = StorageUtil.getAudioContentUri(id);

            //显示名称
            String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));

            //文件大小
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

            //时长
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

            Timber.d("scan local music %d,%s,%s,%s", id, title, artist, album);

            //保存音乐
            Song data = new Song();

            //设置id
            data.setId(String.valueOf(id));

            //处理封面
            data.setIcon(getAndSaveAlbum(contentResolver, albumId));

            //标题
            data.setTitle(title);

            //歌手
            data.setSingerNickname(artist);

            //由于我们项目没有实现专辑
            //所以这些字段也就不保存了

            //时长
            //也可以不保存
            //因为我们是在播放的时候获取
            data.setDuration(duration);

            //路径
            data.setPath(uri);

            //来源
            data.setSource(Song.SOURCE_LOCAL);

            //添加到列表
            datum.add(data);

            //保存到数据库
            LiteORMUtil.getInstance(this.context).saveSong(data);

            //发布通知
            publishProgress(path);

            SystemClock.sleep(500);
        }

        return datum;
    }

    /**
     * 获取并保存封面
     * <p>
     * 并不是所有音乐都有封面，如果有，可以通过如下方法看到
     * Windows：使用自带的Media Player播放
     * macOS：Finder就可以看到
     *
     * @param contentResolver
     * @param data
     * @return
     */
    private String getAndSaveAlbum(ContentResolver contentResolver, long data) {
        if (data <= 0) {
            return null;
        }

        //拼接路径
        Uri uri = ContentUris.withAppendedId(URI_ARTWORK, data);

        try (InputStream in = contentResolver.openInputStream(uri)) {
            //从输入流解码图片
            Bitmap bitmap = BitmapFactory.decodeStream(in);

            if (bitmap != null) {
                //保存到文件
                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), String.valueOf(data));
                BitmapUtil.saveToFile(bitmap, file);
                return file.getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
