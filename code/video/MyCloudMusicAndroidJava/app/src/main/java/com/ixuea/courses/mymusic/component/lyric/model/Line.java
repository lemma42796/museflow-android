package com.ixuea.courses.mymusic.component.lyric.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.ixuea.courses.mymusic.model.Base;

/**
 * 一行歌词
 */
public class Line extends Base implements Parcelable {
    /**
     * 整行歌词
     */
    private String data;
    /**
     * 开始时间
     * 单位毫秒
     */
    private long startTime;
    /**
     * 每一个字
     */
    private String[] words;
    /**
     * 每一个字对应的时间
     */
    private int[] wordDurations;
    /**
     * 结束时间
     */
    private long endTime;

    public Line() {
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String[] getWords() {
        return words;
    }

    public void setWords(String[] words) {
        this.words = words;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    protected Line(Parcel in) {
        this.data = in.readString();
        this.startTime = in.readLong();
        this.words = in.createStringArray();
        this.wordDurations = in.createIntArray();
        this.endTime = in.readLong();
    }

    public int[] getWordDurations() {
        return wordDurations;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setWordDurations(int[] wordDurations) {
        this.wordDurations = wordDurations;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.data);
        dest.writeLong(this.startTime);
        dest.writeStringArray(this.words);
        dest.writeIntArray(this.wordDurations);
        dest.writeLong(this.endTime);
    }

    public void readFromParcel(Parcel source) {
        this.data = source.readString();
        this.startTime = source.readLong();
        this.words = source.createStringArray();
        this.wordDurations = source.createIntArray();
        this.endTime = source.readLong();
    }

    public static final Creator<Line> CREATOR = new Creator<Line>() {
        @Override
        public Line createFromParcel(Parcel source) {
            return new Line(source);
        }

        @Override
        public Line[] newArray(int size) {
            return new Line[size];
        }
    };
}
