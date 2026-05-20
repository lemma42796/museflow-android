package com.ixuea.courses.mymusic.component.lyric.model

import android.os.Parcel
import android.os.Parcelable
import com.ixuea.courses.mymusic.model.Base

/**
 * 解析后的歌词模型
 */
class Lyric : Base, Parcelable {
    var isAccurate: Boolean = false
    var datum: ArrayList<Line>? = null

    constructor()

    private constructor(source: Parcel) {
        isAccurate = source.readByte().toInt() != 0
        datum = source.createTypedArrayList(Line.CREATOR)
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeByte(if (isAccurate) 1 else 0)
        dest.writeTypedList(datum)
    }

    fun readFromParcel(source: Parcel) {
        isAccurate = source.readByte().toInt() != 0
        datum = source.createTypedArrayList(Line.CREATOR)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Lyric> = object : Parcelable.Creator<Lyric> {
            override fun createFromParcel(source: Parcel): Lyric = Lyric(source)

            override fun newArray(size: Int): Array<Lyric?> = arrayOfNulls(size)
        }
    }
}
