package com.ixuea.courses.mymusic.component.search.model;

import android.os.Parcel;

import com.ixuea.courses.mymusic.model.BaseId;

/**
 * 搜索建议项
 */
public class SuggestItem extends BaseId {
    public static final Creator<SuggestItem> CREATOR = new Creator<SuggestItem>() {
        @Override
        public SuggestItem createFromParcel(Parcel source) {
            return new SuggestItem(source);
        }

        @Override
        public SuggestItem[] newArray(int size) {
            return new SuggestItem[size];
        }
    };
    /**
     * 标题
     */
    private String title;

    public SuggestItem(String id, String title) {
        super(id);
        this.title = title;
    }

    protected SuggestItem(Parcel in) {
        super(in);
        this.title = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.title);
    }

    public void readFromParcel(Parcel source) {
        super.readFromParcel(source);
        this.title = source.readString();
    }
}
