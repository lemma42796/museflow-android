package com.ixuea.courses.mymusic.model

import android.os.Parcel

/**
 * 所有带创建/更新时间的模型父类。
 *
 * 这里时间不参与比较，所以不重写 equals/hashCode。
 */
open class Common : BaseId {
    var createdAt: String? = null
    var updatedAt: String? = null

    constructor()

    constructor(id: String?) : super(id)

    protected constructor(source: Parcel) : super(source) {
        createdAt = source.readString()
        updatedAt = source.readString()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeString(createdAt)
        dest.writeString(updatedAt)
    }

    override fun readFromParcel(source: Parcel) {
        super.readFromParcel(source)
        createdAt = source.readString()
        updatedAt = source.readString()
    }
}
