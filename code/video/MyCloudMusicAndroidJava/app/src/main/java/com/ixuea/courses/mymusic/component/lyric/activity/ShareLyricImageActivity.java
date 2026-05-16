package com.ixuea.courses.mymusic.component.lyric.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.databinding.ActivityShareLyricImageBinding;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.ShareUtil;
import com.ixuea.courses.mymusic.util.StorageUtil;
import com.ixuea.superui.toast.SuperToast;
import com.ixuea.superui.util.SuperViewUtil;

import timber.log.Timber;

/**
 * 分享歌词图片界面
 */
public class ShareLyricImageActivity extends BaseTitleActivity<ActivityShareLyricImageBinding> {

    private Song data;
    private String lyric;

    /**
     * 启动方法
     *
     * @param activity
     * @param data
     * @param lyric
     */
    public static void start(Activity activity, Song data, String lyric) {
        //创建intent
        Intent intent = new Intent(activity, ShareLyricImageActivity.class);

        //传递音乐
        intent.putExtra(Constant.DATA, data);

        //传递歌词
        intent.putExtra(Constant.ID, lyric);

        //启动界面
        activity.startActivity(intent);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        data = extraData();

        lyric = extraId();

        ImageUtil.show(getHostActivity(), binding.icon, data.getIcon());
        binding.lyric.setText(lyric);
        binding.song.setText(getResources().getString(R.string.share_song_name,
                data.getSinger().getNickname(),
                data.getTitle()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加载布局文件
        getMenuInflater().inflate(R.menu.menu_share_lyric_image, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareClick();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareClick() {
        Bitmap bitmap = SuperViewUtil.captureBitmap(binding.lyricContainer);

        //将私有路径的图片保存到相册
        //这样其他应用才能访问
        Uri uri = StorageUtil.savePicture(getHostActivity(), bitmap);

        if (uri != null) {
            //获取uri文件路径
            String path = StorageUtil.getMediaStorePath(getHostActivity(), uri);
            Timber.d("shareClick %s", path);

            ShareUtil.shareImage(getHostActivity(), path);
        } else {
            SuperToast.show(R.string.error_share_failed);
        }
    }
}