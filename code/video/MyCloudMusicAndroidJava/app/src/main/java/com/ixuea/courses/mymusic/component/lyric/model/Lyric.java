package com.ixuea.courses.mymusic.component.lyric.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.ixuea.courses.mymusic.model.Base;

import java.util.ArrayList;

/**
 * 解析后的歌词模型
 */
public class Lyric extends Base implements Parcelable {
    /**
     * 是否是精确到字的歌词
     */
    private boolean isAccurate;
    /**
     * 所有的歌词
     */
    private ArrayList<Line> datum;

    public Lyric() {
    }

    public boolean isAccurate() {
        return isAccurate;
    }

    public void setAccurate(boolean accurate) {
        isAccurate = accurate;
    }

    public ArrayList<Line> getDatum() {
        return datum;
    }

    public void setDatum(ArrayList<Line> datum) {
        this.datum = datum;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isAccurate ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.datum);
    }

    public void readFromParcel(Parcel source) {
        this.isAccurate = source.readByte() != 0;
        this.datum = source.createTypedArrayList(Line.CREATOR);
    }

    protected Lyric(Parcel in) {
        this.isAccurate = in.readByte() != 0;
        this.datum = in.createTypedArrayList(Line.CREATOR);
    }

    public static final Creator<Lyric> CREATOR = new Creator<Lyric>() {
        @Override
        public Lyric createFromParcel(Parcel source) {
            return new Lyric(source);
        }

        @Override
        public Lyric[] newArray(int size) {
            return new Lyric[size];
        }
    };
}
