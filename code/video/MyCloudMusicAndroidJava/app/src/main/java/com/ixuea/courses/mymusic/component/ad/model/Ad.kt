package com.ixuea.courses.mymusic.component.ad.model

import android.os.Parcel
import android.os.Parcelable
import com.ixuea.courses.mymusic.model.Common

/**
 * 广告模型
 */
class Ad : Common {
    var title: String? = null
    var icon: String? = null
    var uri: String? = null
    var style: Byte = 0

    constructor()

    private constructor(source: Parcel) : super(source) {
        title = source.readString()
        icon = source.readString()
        uri = source.readString()
        style = source.readByte()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeString(title)
        dest.writeString(icon)
        dest.writeString(uri)
        dest.writeByte(style)
    }

    override fun readFromParcel(source: Parcel) {
        super.readFromParcel(source)
        title = source.readString()
        icon = source.readString()
        uri = source.readString()
        style = source.readByte()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Ad> = object : Parcelable.Creator<Ad> {
            override fun createFromParcel(source: Parcel): Ad = Ad(source)

            override fun newArray(size: Int): Array<Ad?> = arrayOfNulls(size)
        }
    }
}
