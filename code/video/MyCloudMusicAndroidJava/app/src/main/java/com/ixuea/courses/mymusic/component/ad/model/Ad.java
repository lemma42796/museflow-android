package com.ixuea.courses.mymusic.component.ad.model;

import android.os.Parcel;

import com.ixuea.courses.mymusic.model.Common;

/**
 * 广告模型
 */
public class Ad extends Common {
    /**
     * 标题
     */
    private String title;

    /**
     * 图片
     */
    private String icon;

    /**
     * 点击广告后跳转的地址
     */
    private String uri;

    /**
     * 类型，0：图片；10：视频；20：应用
     */
    private byte style;

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

    public byte getStyle() {
        return style;
    }

    public void setStyle(byte style) {
        this.style = style;
    }


    public static final Creator<Ad> CREATOR = new Creator<Ad>() {
        @Override
        public Ad createFromParcel(Parcel source) {
            return new Ad(source);
        }

        @Override
        public Ad[] newArray(int size) {
            return new Ad[size];
        }
    };

    public Ad() {
    }

    protected Ad(Parcel in) {
        super(in);
        this.title = in.readString();
        this.icon = in.readString();
        this.uri = in.readString();
        this.style = in.readByte();
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
        dest.writeByte(this.style);
    }

    public void readFromParcel(Parcel source) {
        super.readFromParcel(source);
        this.title = source.readString();
        this.icon = source.readString();
        this.uri = source.readString();
        this.style = source.readByte();
    }
}
