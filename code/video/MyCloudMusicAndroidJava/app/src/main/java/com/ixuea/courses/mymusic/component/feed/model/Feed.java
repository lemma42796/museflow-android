package com.ixuea.courses.mymusic.component.feed.model;

import com.ixuea.courses.mymusic.component.comment.model.Comment;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.model.Common;
import com.ixuea.courses.mymusic.model.Resource;

import java.util.List;

/**
 * 动态
 */
public class Feed extends Common {
    /**
     * 动态内容
     */
    private String content;

    /**
     * 省
     * 主要用来在前段显示
     */
    private String province;

    /**
     * 省编码
     * 主要用来在后端计算，因为名称可能重复，但编码是唯一
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
     * 位置名称，也就是地图sdk返回的poi名称，例如：山西大学，天府广场
     */
    private String position;

    /**
     * 详细地址，从路开始，例如：山西大学的详细地址是，坞城南路92号
     */
    private String address;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 创建用户
     */
    private User user;

    /**
     * 媒体资源
     */
    private List<Resource> medias;

    /**
     * 点赞的用户
     */
    private List<User> likes;

    /**
     * 评论
     */
    private List<Comment> comments;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Resource> getMedias() {
        return medias;
    }

    public void setMedias(List<Resource> medias) {
        this.medias = medias;
    }

    public List<User> getLikes() {
        return likes;
    }

    public void setLikes(List<User> likes) {
        this.likes = likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
