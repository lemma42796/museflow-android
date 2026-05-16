package com.ixuea.courses.mymusic.component.song.model;

import android.os.Parcel;

import com.ixuea.courses.mymusic.component.lyric.model.Lyric;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.model.Common;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

/**
 * 单曲模型
 */
@Table("song")
public class Song extends Common {
    /**
     * 音乐排序key
     * 音乐id，音乐名称，歌手名
     */
    public static final String[] SORT_KEYS = new String[]{"id", "title", "singer_nickname"};

    /**
     * 其他来源音乐
     * 包括在线的，下载的
     */
    public static final int SOURCE_OTHER = 0;

    /**
     * 本地音乐
     */
    public static final int SOURCE_LOCAL = 10;

    /**
     * 标题
     */
    private String title;

    /**
     * 封面
     */
    private String icon;

    /**
     * 音乐地址
     */
    private String uri;

    /**
     * 点击数
     */
    @Ignore
    private int clicksCount;

    /**
     * 评论数
     */
    @Ignore
    private int commentsCount;

    /**
     * 歌词类型
     */
    private Integer style;

    /**
     * 歌词内容
     */
    private String lyric;

    /**
     * 是否旋转黑胶唱片
     */
    @Ignore
    private boolean isRotate = true;

    /**
     * 创建该音乐的人
     */
    @Ignore
    private User user;

    /**
     * 歌手
     */
    @Ignore
    private User singer;

    /**
     * 总进度
     * 单位：毫秒
     */
    private long duration;

    /**
     * 播放进度
     */
    private long progress;

    /**
     * 是否在播放列表
     * true
     */
    @Column("list")
    private boolean playList;

    /**
     * 音乐来源
     */
    private int source;

    /**
     * 歌手Id
     * <p>
     * 在sqlite，mysql这样的数据库中
     * 字段名建议用下划线
     * 而不是驼峰命名
     * <p>
     * 用来将歌手对象拆分到多个字段，方便在一张表存储，和查询
     */
    @Column("singer_id")
    private String singerId;

    /**
     * 歌手名称
     */
    @Column("singer_nickname")
    private String singerNickname;

    /**
     * 歌手头像
     * 可选值
     */
    @Column("singer_icon")
    private String singerIcon;

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public void localConvert() {
        User singer = new User();
        singer.setId(singerId);
        singer.setNickname(singerNickname);
        singer.setIcon(singerIcon);
        setSinger(singer);
    }

    public void convertLocal() {
        singerId = singer.getId();
        singerNickname = singer.getNickname();
        singerIcon = singer.getIcon();
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getClicksCount() {
        return clicksCount;
    }

    public void setClicksCount(int clicksCount) {
        this.clicksCount = clicksCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Integer getStyle() {
        return style;
    }

    public void setStyle(Integer style) {
        this.style = style;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getSinger() {
        return singer;
    }

    public void setSinger(User singer) {
        this.singer = singer;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public boolean isPlayList() {
        return playList;
    }

    public void setPlayList(boolean playList) {
        this.playList = playList;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getSingerId() {
        return singerId;
    }

    public void setSingerId(String singerId) {
        this.singerId = singerId;
    }

    public String getSingerNickname() {
        return singerNickname;
    }

    public void setSingerNickname(String singerNickname) {
        this.singerNickname = singerNickname;
    }

    public String getSingerIcon() {
        return singerIcon;
    }

    public void setSingerIcon(String singerIcon) {
        this.singerIcon = singerIcon;
    }

    public boolean isRotate() {
        return isRotate;
    }

    public void setRotate(boolean rotate) {
        isRotate = rotate;
    }

    /**
     * 本地扫描的音乐路径
     * 也是相对位置
     * <p>
     * 在线的音乐下载后路径在下载对象那边
     */
    private String path;

    protected Song(Parcel in) {
        super(in);
        this.title = in.readString();
        this.icon = in.readString();
        this.uri = in.readString();
        this.clicksCount = in.readInt();
        this.commentsCount = in.readInt();
        this.style = (Integer) in.readValue(Integer.class.getClassLoader());
        this.lyric = in.readString();
        this.isRotate = in.readByte() != 0;
        this.user = in.readParcelable(User.class.getClassLoader());
        this.singer = in.readParcelable(User.class.getClassLoader());
        this.duration = in.readLong();
        this.progress = in.readLong();
        this.playList = in.readByte() != 0;
        this.source = in.readInt();
        this.singerId = in.readString();
        this.singerNickname = in.readString();
        this.singerIcon = in.readString();
        this.path = in.readString();
        this.parsedLyric = in.readParcelable(Lyric.class.getClassLoader());
    }

    /**
     * 已经解析后的歌词
     */
    @Ignore
    private Lyric parsedLyric;

    public Song() {
    }

    public Lyric getParsedLyric() {
        return parsedLyric;
    }

    public void setParsedLyric(Lyric parsedLyric) {
        this.parsedLyric = parsedLyric;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    /**
     * 是否是本地音乐
     *
     * @return
     */
    public boolean isLocal() {
        return source == SOURCE_LOCAL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.title);
        dest.writeString(this.icon);
        dest.writeString(this.uri);
        dest.writeInt(this.clicksCount);
        dest.writeInt(this.commentsCount);
        dest.writeValue(this.style);
        dest.writeString(this.lyric);
        dest.writeByte(this.isRotate ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.singer, flags);
        dest.writeLong(this.duration);
        dest.writeLong(this.progress);
        dest.writeByte(this.playList ? (byte) 1 : (byte) 0);
        dest.writeInt(this.source);
        dest.writeString(this.singerId);
        dest.writeString(this.singerNickname);
        dest.writeString(this.singerIcon);
        dest.writeString(this.path);
        dest.writeParcelable(this.parsedLyric, flags);
    }

    public void readFromParcel(Parcel source) {
        super.readFromParcel(source);
        this.title = source.readString();
        this.icon = source.readString();
        this.uri = source.readString();
        this.clicksCount = source.readInt();
        this.commentsCount = source.readInt();
        this.style = (Integer) source.readValue(Integer.class.getClassLoader());
        this.lyric = source.readString();
        this.isRotate = source.readByte() != 0;
        this.user = source.readParcelable(User.class.getClassLoader());
        this.singer = source.readParcelable(User.class.getClassLoader());
        this.duration = source.readLong();
        this.progress = source.readLong();
        this.playList = source.readByte() != 0;
        this.source = source.readInt();
        this.singerId = source.readString();
        this.singerNickname = source.readString();
        this.singerIcon = source.readString();
        this.path = source.readString();
        this.parsedLyric = source.readParcelable(Lyric.class.getClassLoader());
    }
}
