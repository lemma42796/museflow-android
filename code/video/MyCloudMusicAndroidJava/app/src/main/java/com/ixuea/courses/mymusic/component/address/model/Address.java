package com.ixuea.courses.mymusic.component.address.model;

import com.google.gson.annotations.SerializedName;
import com.ixuea.courses.mymusic.model.Common;
import com.ixuea.courses.mymusic.util.Constant;

/**
 * 收货地址
 * <p>
 * 例如：{
 * "name": "李薇",
 * "phone": "13141111222",
 * "province": "四川省",
 * "province_code": "510000",
 * "city": "成都市",
 * "city_code": "510100",
 * "area": "双流区",
 * "area_code": "510116",
 * "detail": "牧华路远大中央公园9栋1单元801",
 * "default_address": 0
 * }
 */
public class Address extends Common {
    private String name;
    private String telephone;
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
     * 剩余的地址部分
     */
    private String detail;

    /**
     * 是否是默认地址
     * <p>
     * 0：不是默认
     * 10：默认
     */
    @SerializedName("default")
    private int defaultAddress;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(int defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    /**
     * 获取收货地区
     * <p>
     * 省市区拼接
     *
     * @return
     */
    public String getReceiverArea() {
        return String.format("%s%s%s", getProvince(), getCity(), getArea());
    }

    /**
     * 是否是默认地址
     *
     * @return
     */
    public boolean isDefault() {
        return defaultAddress == Constant.VALUE10;
    }

    public String getContact() {
        return String.format("%s %s", getName(), getTelephone());
    }
}
