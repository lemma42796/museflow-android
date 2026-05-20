package com.ixuea.courses.mymusic.component.song.model

import android.os.Parcel
import android.os.Parcelable
import com.ixuea.courses.mymusic.component.lyric.model.Lyric
import com.ixuea.courses.mymusic.component.user.model.User
import com.ixuea.courses.mymusic.model.Common
import com.litesuits.orm.db.annotation.Column
import com.litesuits.orm.db.annotation.Ignore
import com.litesuits.orm.db.annotation.Table

/**
 * 单曲模型
 */
@Table("song")
class Song : Common {
    var title: String? = null
    var icon: String? = null
    var uri: String? = null

    @field:Ignore
    var clicksCount: Int = 0

    @field:Ignore
    var commentsCount: Int = 0

    var style: Int? = null
    var lyric: String? = null

    @field:Ignore
    var isRotate: Boolean = true

    @field:Ignore
    var user: User? = null

    @field:Ignore
    var singer: User? = null

    var duration: Long = 0
    var progress: Long = 0

    @field:Column("list")
    var isPlayList: Boolean = false

    var source: Int = SOURCE_OTHER

    @field:Column("singer_id")
    var singerId: String? = null

    @field:Column("singer_nickname")
    var singerNickname: String? = null

    @field:Column("singer_icon")
    var singerIcon: String? = null

    var path: String? = null

    @field:Ignore
    var parsedLyric: Lyric? = null

    constructor()

    private constructor(source: Parcel) : super(source) {
        title = source.readString()
        icon = source.readString()
        uri = source.readString()
        clicksCount = source.readInt()
        commentsCount = source.readInt()
        style = source.readValue(Int::class.java.classLoader) as? Int
        lyric = source.readString()
        isRotate = source.readByte().toInt() != 0
        user = source.readParcelable(User::class.java.classLoader)
        singer = source.readParcelable(User::class.java.classLoader)
        duration = source.readLong()
        progress = source.readLong()
        isPlayList = source.readByte().toInt() != 0
        this.source = source.readInt()
        singerId = source.readString()
        singerNickname = source.readString()
        singerIcon = source.readString()
        path = source.readString()
        parsedLyric = source.readParcelable(Lyric::class.java.classLoader)
    }

    fun localConvert() {
        singer = User().apply {
            id = singerId
            nickname = singerNickname
            icon = singerIcon
        }
    }

    fun convertLocal() {
        val currentSinger = singer ?: return
        singerId = currentSinger.id
        singerNickname = currentSinger.nickname
        singerIcon = currentSinger.icon
    }

    val isLocal: Boolean
        get() = source == SOURCE_LOCAL

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeString(title)
        dest.writeString(icon)
        dest.writeString(uri)
        dest.writeInt(clicksCount)
        dest.writeInt(commentsCount)
        dest.writeValue(style)
        dest.writeString(lyric)
        dest.writeByte(if (isRotate) 1 else 0)
        dest.writeParcelable(user, flags)
        dest.writeParcelable(singer, flags)
        dest.writeLong(duration)
        dest.writeLong(progress)
        dest.writeByte(if (isPlayList) 1 else 0)
        dest.writeInt(source)
        dest.writeString(singerId)
        dest.writeString(singerNickname)
        dest.writeString(singerIcon)
        dest.writeString(path)
        dest.writeParcelable(parsedLyric, flags)
    }

    override fun readFromParcel(source: Parcel) {
        super.readFromParcel(source)
        title = source.readString()
        icon = source.readString()
        uri = source.readString()
        clicksCount = source.readInt()
        commentsCount = source.readInt()
        style = source.readValue(Int::class.java.classLoader) as? Int
        lyric = source.readString()
        isRotate = source.readByte().toInt() != 0
        user = source.readParcelable(User::class.java.classLoader)
        singer = source.readParcelable(User::class.java.classLoader)
        duration = source.readLong()
        progress = source.readLong()
        isPlayList = source.readByte().toInt() != 0
        this.source = source.readInt()
        singerId = source.readString()
        singerNickname = source.readString()
        singerIcon = source.readString()
        path = source.readString()
        parsedLyric = source.readParcelable(Lyric::class.java.classLoader)
    }

    companion object {
        @JvmField
        val SORT_KEYS: Array<String> = arrayOf("id", "title", "singer_nickname")

        const val SOURCE_OTHER = 0
        const val SOURCE_LOCAL = 10

        @JvmField
        val CREATOR: Parcelable.Creator<Song> = object : Parcelable.Creator<Song> {
            override fun createFromParcel(source: Parcel): Song = Song(source)

            override fun newArray(size: Int): Array<Song?> = arrayOfNulls(size)
        }
    }
}
