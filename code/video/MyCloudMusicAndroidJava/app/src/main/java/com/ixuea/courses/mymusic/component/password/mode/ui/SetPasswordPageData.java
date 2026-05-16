package com.ixuea.courses.mymusic.component.password.mode.ui;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 设置密码界面数据模型
 */
public class SetPasswordPageData implements Parcelable {
    public static final Creator<SetPasswordPageData> CREATOR = new Creator<SetPasswordPageData>() {
        @Override
        public SetPasswordPageData createFromParcel(Parcel source) {
            return new SetPasswordPageData(source);
        }

        @Override
        public SetPasswordPageData[] newArray(int size) {
            return new SetPasswordPageData[size];
        }
    };
    private String phone;
    private String email;
    /**
     * 验证码
     */
    private String code;

    public SetPasswordPageData(String phone, String email, String code) {
        this.phone = phone;
        this.email = email;
        this.code = code;
    }

    public SetPasswordPageData() {
    }

    protected SetPasswordPageData(Parcel in) {
        this.phone = in.readString();
        this.email = in.readString();
        this.code = in.readString();
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phone);
        dest.writeString(this.email);
        dest.writeString(this.code);
    }

    public void readFromParcel(Parcel source) {
        this.phone = source.readString();
        this.email = source.readString();
        this.code = source.readString();
    }
}
