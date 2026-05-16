package com.ixuea.courses.mymusic.component.input.activity.model.ui;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 输入验证码界面数据模型
 */
public class InputCodePageData implements Parcelable {
    public static final Creator<InputCodePageData> CREATOR = new Creator<InputCodePageData>() {
        @Override
        public InputCodePageData createFromParcel(Parcel source) {
            return new InputCodePageData(source);
        }

        @Override
        public InputCodePageData[] newArray(int size) {
            return new InputCodePageData[size];
        }
    };
    private int style;
    private String phone;
    private String email;

    public InputCodePageData() {
    }

    public InputCodePageData(int style) {
        this.style = style;
    }

    protected InputCodePageData(Parcel in) {
        this.style = in.readInt();
        this.phone = in.readString();
        this.email = in.readString();
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.style);
        dest.writeString(this.phone);
        dest.writeString(this.email);
    }

    public void readFromParcel(Parcel source) {
        this.style = source.readInt();
        this.phone = source.readString();
        this.email = source.readString();
    }
}
