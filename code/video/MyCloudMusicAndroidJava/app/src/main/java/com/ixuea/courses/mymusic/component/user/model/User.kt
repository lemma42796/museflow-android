package com.ixuea.courses.mymusic.component.user.model

import android.os.Parcel
import android.os.Parcelable
import com.ixuea.courses.mymusic.model.Common
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity
import com.ixuea.courses.mymusic.util.Constant

/**
 * 用户模型
 */
class User : Common, BaseMultiItemEntity {
    var nickname: String? = null
    var icon: String? = null
    var phone: String? = null
    var email: String? = null
    var password: String? = null
    var wechatId: String? = null
    var qqId: String? = null
    var code: String? = null
    var detail: String? = null
    var province: String? = null
    var provinceCode: String? = null
    var city: String? = null
    var cityCode: String? = null
    var area: String? = null
    var areaCode: String? = null
    var followingsCount: Long = 0
    var followersCount: Long = 0
    var following: String? = null
    var gender: Int = GENDER_UNKNOWN
    var birthday: String? = null
    var device: String? = null
    var push: String? = null
    var pinyin: String? = null
    var pinyinFirst: String? = null
    var first: String? = null

    constructor()

    constructor(id: String?) : super(id)

    private constructor(source: Parcel) : super(source) {
        nickname = source.readString()
        icon = source.readString()
        phone = source.readString()
        email = source.readString()
        password = source.readString()
        wechatId = source.readString()
        qqId = source.readString()
        code = source.readString()
        detail = source.readString()
        province = source.readString()
        provinceCode = source.readString()
        city = source.readString()
        cityCode = source.readString()
        area = source.readString()
        areaCode = source.readString()
        followingsCount = source.readLong()
        followersCount = source.readLong()
        following = source.readString()
        gender = source.readInt()
        birthday = source.readString()
        device = source.readString()
        push = source.readString()
        pinyin = source.readString()
        pinyinFirst = source.readString()
        first = source.readString()
    }

    val isFollowing: Boolean
        get() = following != null

    val genderFormat: String
        get() = when (gender) {
            MALE -> "男"
            FEMALE -> "女"
            else -> "保密"
        }

    val descriptionFormat: String
        get() = detail.takeUnless { it.isNullOrEmpty() } ?: "这个人很懒，没有填写个人介绍!"

    fun birthdayFormat(): String {
        return birthday.takeUnless { it.isNullOrBlank() }.orEmpty()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeString(nickname)
        dest.writeString(icon)
        dest.writeString(phone)
        dest.writeString(email)
        dest.writeString(password)
        dest.writeString(wechatId)
        dest.writeString(qqId)
        dest.writeString(code)
        dest.writeString(detail)
        dest.writeString(province)
        dest.writeString(provinceCode)
        dest.writeString(city)
        dest.writeString(cityCode)
        dest.writeString(area)
        dest.writeString(areaCode)
        dest.writeLong(followingsCount)
        dest.writeLong(followersCount)
        dest.writeString(following)
        dest.writeInt(gender)
        dest.writeString(birthday)
        dest.writeString(device)
        dest.writeString(push)
        dest.writeString(pinyin)
        dest.writeString(pinyinFirst)
        dest.writeString(first)
    }

    override fun readFromParcel(source: Parcel) {
        super.readFromParcel(source)
        nickname = source.readString()
        icon = source.readString()
        phone = source.readString()
        email = source.readString()
        password = source.readString()
        wechatId = source.readString()
        qqId = source.readString()
        code = source.readString()
        detail = source.readString()
        province = source.readString()
        provinceCode = source.readString()
        city = source.readString()
        cityCode = source.readString()
        area = source.readString()
        areaCode = source.readString()
        followingsCount = source.readLong()
        followersCount = source.readLong()
        following = source.readString()
        gender = source.readInt()
        birthday = source.readString()
        device = source.readString()
        push = source.readString()
        pinyin = source.readString()
        pinyinFirst = source.readString()
        first = source.readString()
    }

    override val itemType: Int
        get() = Constant.STYLE_USER

    companion object {
        const val GENDER_UNKNOWN = 0
        const val MALE = 10
        const val FEMALE = 20

        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)

            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }

        @JvmStatic
        fun createLogin(phone: String?, email: String?, password: String?): User {
            return User().apply {
                this.phone = phone
                this.email = email
                this.password = password
            }
        }
    }
}
