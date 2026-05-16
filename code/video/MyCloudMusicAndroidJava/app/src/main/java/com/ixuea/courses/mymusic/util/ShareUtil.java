package com.ixuea.courses.mymusic.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.song.model.Song;

import java.io.File;

public class ShareUtil {
    public static void shareLyricText(Context context, Song data, String lyric) {
        String title = data == null ? "" : data.getTitle();
        String shareContent = String.format("分享歌词：%s %s", lyric, title);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareContent);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)));
    }

    public static void shareLyricTextToQQ(Context context, Song data, String lyric) {
        shareLyricText(context, data, lyric);
    }

    public static void shareImage(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)));
    }
}
