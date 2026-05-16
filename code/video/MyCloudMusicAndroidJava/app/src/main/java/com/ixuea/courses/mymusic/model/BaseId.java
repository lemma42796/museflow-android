package com.ixuea.courses.mymusic.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import java.util.Objects;

/**
 * 所有模型Id父类
 */
public class BaseId extends Base implements Parcelable {
    /**
     * Id
     */
    @PrimaryKey(AssignType.BY_MYSELF)
    private String id;

    public BaseId() {
    }

    public BaseId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseId baseId = (BaseId) o;
        return Objects.equals(id, baseId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
    }

    protected BaseId(Parcel in) {
        this.id = in.readString();
    }

    public static final Creator<BaseId> CREATOR = new Creator<BaseId>() {
        @Override
        public BaseId createFromParcel(Parcel in) {
            return new BaseId(in);
        }

        @Override
        public BaseId[] newArray(int size) {
            return new BaseId[size];
        }
    };
}
