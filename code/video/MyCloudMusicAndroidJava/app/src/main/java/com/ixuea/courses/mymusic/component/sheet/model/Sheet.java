package com.ixuea.courses.mymusic.component.sheet.model;

import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.model.Common;
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;
import com.ixuea.courses.mymusic.util.Constant;

import java.util.ArrayList;

/**
 * 歌单模型
 */
public class Sheet extends Common implements BaseMultiItemEntity {
    /**
     * 歌单标题
     */
    private String title;

    /**
     * 歌单封面
     */
    private String icon;

    /**
     * 描述
     */
    private String detail;

    /**
     * 点击数
     */
    private int clicksCount;

    /**
     * 收藏数
     */
    private int collectsCount;

    /**
     * 评论数
     */
    private int commentsCount;

    /**
     * 音乐数量
     */
    private int songsCount;

    /**
     * 歌曲
     */
    private ArrayList<Song> songs;

    /**
     * 歌单创建者
     */
    private User user;

    /**
     * 是否收藏了
     * 有值就表示收藏了
     */
    private String collectId;

    /**
     * 是否收藏
     *
     * @return true:收藏；false:没有收藏
     */
    public boolean isCollect() {
        return collectId != null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getClicksCount() {
        return clicksCount;
    }

    public void setClicksCount(int clicksCount) {
        this.clicksCount = clicksCount;
    }

    public int getCollectsCount() {
        return collectsCount;
    }

    public void setCollectsCount(int collectsCount) {
        this.collectsCount = collectsCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getSongsCount() {
        return songsCount;
    }

    public void setSongsCount(int songsCount) {
        this.songsCount = songsCount;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCollectId() {
        return collectId;
    }

    public void setCollectId(String collectId) {
        this.collectId = collectId;
    }

    @Override
    public int getItemType() {
        return Constant.STYLE_SHEET;
    }
}
