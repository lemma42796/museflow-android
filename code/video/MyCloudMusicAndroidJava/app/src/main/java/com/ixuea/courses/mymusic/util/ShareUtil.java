package com.ixuea.courses.mymusic.util;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.superui.toast.SuperToast;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class ShareUtil {
    /**
     * 分享歌词文本
     *
     * @param context
     * @param data    歌曲信息
     * @param lyric   歌词文本
     */
    public static void shareLyricText(Context context, Song data, String lyric) {
        //分享字符串：分享歌词：这是歌词。分享%s的单曲《%s》：http://dev-courses-misuc.ixuea.com/songs/%s (来自@我的云音乐)
        //具体的可以根据业务需求更改

        //真实项目中分享的地址
        //一般是音乐的网页
        //但由于我们这里没有实现网页
        //所以这个地址是随便写的

        //格式化url
        String url = String.format("http://dev-my-cloud-music-api-rails.ixuea.com/v1/songs/%s", data.getId());

        //分享的文本
        String shareContent = String.format("分享歌词：%s。分享%s的单曲《%s》：%s (来自@我的云音乐)",
                lyric,
                data.getSinger().getNickname(),
                data.getTitle(),
                url);

        //应用的名称
        String name = context.getString(R.string.app_name);

        //创建一键分享
        OnekeyShare oks = new OnekeyShare();

        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(name);

        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(url);

        // text是分享文本，所有平台都需要这个字段
        oks.setText(shareContent);

        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);

        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("这个课程真不错！");

        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(name);

        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);

        //启动分享
        oks.show(context);
    }

    /**
     * QQ分享纯文本，使用系统分析实现
     *
     * @param context
     * @param data
     * @param lyric
     */
    public static void shareLyricTextToQQ(Context context, Song data, String lyric) {
        //格式化url
        String url = String.format("http://dev-my-cloud-music-api-rails.ixuea.com/v1/songs/%s", data.getId());

        //分享的文本
        String shareContent = String.format("分享歌词：%s。分享%s的单曲《%s》：%s (来自@我的云音乐)",
                lyric,
                data.getSinger().getNickname(),
                data.getTitle(),
                url);

        try {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");

            //标题
            intent.putExtra(Intent.EXTRA_SUBJECT, "");

            //内容
            intent.putExtra(Intent.EXTRA_TEXT, shareContent);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.setComponent(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            SuperToast.show(R.string.install_app_tip);
        }
    }

    /**
     * 分享图片
     *
     * @param context
     * @param path
     */
    public static void shareImage(Context context, String path) {
        //创建一键分享
        OnekeyShare oks = new OnekeyShare();

        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        //imagePath是图片的本地路径
        //Linked-In以外的平台都支持此参数
        //确保sdcard下面存在此张图片
        oks.setImagePath(path);

        //分享
        oks.show(context);
    }
}
