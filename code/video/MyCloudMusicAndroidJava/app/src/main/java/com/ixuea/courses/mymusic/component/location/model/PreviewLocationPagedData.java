package com.ixuea.courses.mymusic.component.location.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 预览位置（位置详情）界面界面数据
 */
public class PreviewLocationPagedData implements Parcelable {
    public static final Creator<PreviewLocationPagedData> CREATOR = new Creator<PreviewLocationPagedData>() {
        @Override
        public PreviewLocationPagedData createFromParcel(Parcel source) {
            return new PreviewLocationPagedData(source);
        }

        @Override
        public PreviewLocationPagedData[] newArray(int size) {
            return new PreviewLocationPagedData[size];
        }
    };
    /**
     * 名称
     */
    private String title;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 经度
     */
    private Double longitude;
    /**
     * 纬度
     */
    private Double latitude;

    public PreviewLocationPagedData(String title, String address, Double longitude, Double latitude) {
        this.title = title;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public PreviewLocationPagedData() {
    }

    protected PreviewLocationPagedData(Parcel in) {
        this.title = in.readString();
        this.address = in.readString();
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.address);
        dest.writeValue(this.longitude);
        dest.writeValue(this.latitude);
    }

    public void readFromParcel(Parcel source) {
        this.title = source.readString();
        this.address = source.readString();
        this.longitude = (Double) source.readValue(Double.class.getClassLoader());
        this.latitude = (Double) source.readValue(Double.class.getClassLoader());
    }
}
