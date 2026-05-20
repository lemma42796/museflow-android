package com.ixuea.courses.mymusic.component.lyric.model

import android.os.Parcel
import android.os.Parcelable
import com.ixuea.courses.mymusic.model.Base

/**
 * 一行歌词
 */
class Line : Base, Parcelable {
    var data: String? = null
    var startTime: Long = 0
    var words: Array<String>? = null
    var wordDurations: IntArray? = null
    var endTime: Long = 0

    constructor()

    private constructor(source: Parcel) {
        data = source.readString()
        startTime = source.readLong()
        words = source.createStringArray()
        wordDurations = source.createIntArray()
        endTime = source.readLong()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(data)
        dest.writeLong(startTime)
        dest.writeStringArray(words)
        dest.writeIntArray(wordDurations)
        dest.writeLong(endTime)
    }

    fun readFromParcel(source: Parcel) {
        data = source.readString()
        startTime = source.readLong()
        words = source.createStringArray()
        wordDurations = source.createIntArray()
        endTime = source.readLong()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Line> = object : Parcelable.Creator<Line> {
            override fun createFromParcel(source: Parcel): Line = Line(source)

            override fun newArray(size: Int): Array<Line?> = arrayOfNulls(size)
        }
    }
}
