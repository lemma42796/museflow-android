package com.ixuea.courses.mymusic.model

import android.os.Parcel
import android.os.Parcelable
import com.litesuits.orm.db.annotation.PrimaryKey
import com.litesuits.orm.db.enums.AssignType

/**
 * 所有模型 Id 父类
 */
open class BaseId : Base, Parcelable {
    @field:PrimaryKey(AssignType.BY_MYSELF)
    var id: String? = null

    constructor()

    constructor(id: String?) {
        this.id = id
    }

    protected constructor(source: Parcel) {
        id = source.readString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as BaseId
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
    }

    open fun readFromParcel(source: Parcel) {
        id = source.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<BaseId> = object : Parcelable.Creator<BaseId> {
            override fun createFromParcel(source: Parcel): BaseId = BaseId(source)

            override fun newArray(size: Int): Array<BaseId?> = arrayOfNulls(size)
        }
    }
}
