package com.ixuea.courses.mymusic.component.user.model;

import android.os.Parcel;
import android.text.TextUtils;

import com.ixuea.courses.mymusic.model.Common;
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;
import com.ixuea.courses.mymusic.util.Constant;

import org.apache.commons.lang3.StringUtils;

/**
 * 用户模型
 */
public class User extends Common implements BaseMultiItemEntity {
    /**
     * 未知
     */
    public static final int GENDER_UNKNOWN = 0;

    /**
     * 男
     */
    public static final int MALE = 10;

    /**
     * 女
     */
    public static final int FEMALE = 20;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String icon;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户的密码,登录，注册向服务端传递
     */
    private String password;

    /**
     * 微信第三方登录后id
     */
    private String wechatId;

    /**
     * QQ第三方登录后Id
     */
    private String qqId;

    /**
     * 验证码
     * 只有找回密码的时候才会用到
     */
    private String code;

    /**
     * 描述
     */
    private String detail;

    /**
     * 省
     */
    private String province;

    /**
     * 省编码
     */
    private String provinceCode;

    /**
     * 市
     */
    private String city;

    /**
     * 市编码
     */
    private String cityCode;


    /**
     * 区
     */
    private String area;

    /**
     * 区编码
     */
    private String areaCode;

    /**
     * 我的关注的人（好友）
     */
    private long followingsCount;

    /**
     * 关注我的人（粉丝）
     */
    private long followersCount;

    /**
     * 是否关注
     * 1:关注
     * 在用户详情才会返回
     */
    private String following;

    /**
     * 性别
     * 0：保密，10：男，20：女
     * 可以定义为枚举
     * 但枚举性能没有int好
     * 但int没有一些编译验证
     * Android中有替代方式
     * 这里用不到就不讲解了
     */
    private int gender;

    /**
     * 生日
     * 格式为：yyyy-MM-dd
     */
    private String birthday;

    /**
     * 设备名称
     * 例如：小米11
     */
    private String device;

    /**
     * 推送id
     */
    private String push;

    //region 本地过滤
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    /**
     * 拼音
     */
    private String pinyin;
    /**
     * 拼音首字母
     */
    private String pinyinFirst;
    //endregion
    /**
     * 拼音首字母的首字母
     */
    private String first;

    public User(String id) {
        super(id);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getQqId() {
        return qqId;
    }

    public void setQqId(String qqId) {
        this.qqId = qqId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public long getFollowingsCount() {
        return followingsCount;
    }

    public void setFollowingsCount(long followingsCount) {
        this.followingsCount = followingsCount;
    }

    public long getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(long followersCount) {
        this.followersCount = followersCount;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getPush() {
        return push;
    }

    public void setPush(String push) {
        this.push = push;
    }

    /**
     * 创建用户名登录参数
     *
     * @param phone
     * @param email
     * @param password
     * @return
     */
    public static User createLogin(String phone, String email, String password) {
        User result = new User();

        //这里虽然同时传递了手机号和邮箱
        //但服务端登录的时候有先后顺序
        result.setPhone(phone);
        result.setEmail(email);
        result.setPassword(password);

        return result;
    }

    /**
     * 是否关注了
     *
     * @return
     */
    public boolean isFollowing() {
        return following != null;
    }

    /**
     * 格式化后的性别
     *
     * @return
     */
    public String getGenderFormat() {
        switch (gender) {
            case 10:
                return "男";
            case 20:
                return "女";
            default:
                //0
                return "保密";
        }
    }

    public String birthdayFormat() {
        if (StringUtils.isBlank(birthday)) {
            return "";
        }

        return birthday;
    }

    /**
     * 格式化后的描述
     *
     * @return
     */
    public String getDescriptionFormat() {
        if (TextUtils.isEmpty(detail)) {
            return "这个人很懒，没有填写个人介绍!";
        }

        return detail;
    }

    public User() {
    }

    protected User(Parcel in) {
        super(in);
        this.nickname = in.readString();
        this.icon = in.readString();
        this.phone = in.readString();
        this.email = in.readString();
        this.password = in.readString();
        this.wechatId = in.readString();
        this.qqId = in.readString();
        this.code = in.readString();
        this.detail = in.readString();
        this.province = in.readString();
        this.provinceCode = in.readString();
        this.city = in.readString();
        this.cityCode = in.readString();
        this.area = in.readString();
        this.areaCode = in.readString();
        this.followingsCount = in.readLong();
        this.followersCount = in.readLong();
        this.following = in.readString();
        this.gender = in.readInt();
        this.birthday = in.readString();
        this.device = in.readString();
        this.push = in.readString();
        this.pinyin = in.readString();
        this.pinyinFirst = in.readString();
        this.first = in.readString();
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPinyinFirst() {
        return pinyinFirst;
    }

    public void setPinyinFirst(String pinyinFirst) {
        this.pinyinFirst = pinyinFirst;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.nickname);
        dest.writeString(this.icon);
        dest.writeString(this.phone);
        dest.writeString(this.email);
        dest.writeString(this.password);
        dest.writeString(this.wechatId);
        dest.writeString(this.qqId);
        dest.writeString(this.code);
        dest.writeString(this.detail);
        dest.writeString(this.province);
        dest.writeString(this.provinceCode);
        dest.writeString(this.city);
        dest.writeString(this.cityCode);
        dest.writeString(this.area);
        dest.writeString(this.areaCode);
        dest.writeLong(this.followingsCount);
        dest.writeLong(this.followersCount);
        dest.writeString(this.following);
        dest.writeInt(this.gender);
        dest.writeString(this.birthday);
        dest.writeString(this.device);
        dest.writeString(this.push);
        dest.writeString(this.pinyin);
        dest.writeString(this.pinyinFirst);
        dest.writeString(this.first);
    }

    public void readFromParcel(Parcel source) {
        super.readFromParcel(source);
        this.nickname = source.readString();
        this.icon = source.readString();
        this.phone = source.readString();
        this.email = source.readString();
        this.password = source.readString();
        this.wechatId = source.readString();
        this.qqId = source.readString();
        this.code = source.readString();
        this.detail = source.readString();
        this.province = source.readString();
        this.provinceCode = source.readString();
        this.city = source.readString();
        this.cityCode = source.readString();
        this.area = source.readString();
        this.areaCode = source.readString();
        this.followingsCount = source.readLong();
        this.followersCount = source.readLong();
        this.following = source.readString();
        this.gender = source.readInt();
        this.birthday = source.readString();
        this.device = source.readString();
        this.push = source.readString();
        this.pinyin = source.readString();
        this.pinyinFirst = source.readString();
        this.first = source.readString();
    }

    @Override
    public int getItemType() {
        return Constant.STYLE_USER;
    }
}
