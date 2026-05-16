package com.ixuea.courses.mymusic.model;

import android.os.Parcel;

/**
 * 所有模型父类
 * <p>
 * 这里时间不参与比较，所以不重写equals，hashCode
 * 如果项目中需要参数比较，要重写
 */
public class Common extends BaseId {
    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 更新时间
     */
    private String updatedAt;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }


    public Common() {
    }

    public Common(String id) {
        super(id);
    }

    protected Common(Parcel in) {
        super(in);
        this.createdAt = in.readString();
        this.updatedAt = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.createdAt);
        dest.writeString(this.updatedAt);
    }

    public void readFromParcel(Parcel source) {
        super.readFromParcel(source);
        this.createdAt = source.readString();
        this.updatedAt = source.readString();
    }

}
