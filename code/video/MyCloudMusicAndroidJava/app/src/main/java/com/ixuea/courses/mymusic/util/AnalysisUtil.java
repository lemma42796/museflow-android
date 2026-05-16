package com.ixuea.courses.mymusic.util;

import android.content.Context;

import com.ixuea.courses.mymusic.component.order.model.Order;
import com.ixuea.courses.mymusic.component.user.model.User;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import cn.jiguang.analytics.android.api.CountEvent;
import cn.jiguang.analytics.android.api.Currency;
import cn.jiguang.analytics.android.api.JAnalyticsInterface;
import cn.jiguang.analytics.android.api.LoginEvent;
import cn.jiguang.analytics.android.api.PurchaseEvent;

/**
 * 统计相关工具类
 */
public class AnalysisUtil {
    /**
     * 登录事件
     *
     * @param context
     * @param success
     */
    public static void onLogin(
            Context context, Boolean success,
            User data
    ) {
        //创建登录事件
        //参数1：登录类型
        //参数2：是否登录成功
        LoginEvent event = new LoginEvent(getMethod(data), success);

        //获取扩展信息
        Map<String, String> extra = new HashMap<>();

        //头像
        if (StringUtils.isNotBlank(data.getIcon())) {
            extra.put("icon", data.getIcon());
        }

        //昵称
        if (StringUtils.isNotBlank(data.getNickname())) {
            extra.put("nickname", data.getNickname());
        }

        //手机号
        if (StringUtils.isNotBlank(data.getPhone())) {
            extra.put("phone", data.getPhone());
        }

        //邮箱
        if (StringUtils.isNotBlank(data.getEmail())) {
            extra.put("email", data.getEmail());
        }

        //qq
        if (StringUtils.isNotBlank(data.getQqId())) {
            extra.put("QQ", data.getQqId());
        }

        //微信
        if (StringUtils.isNotBlank(data.getWechatId())) {
            extra.put("Wechat", data.getWechatId());
        }

        //添加扩展信息
        event.addExtMap(extra);

        //记录事件
        JAnalyticsInterface.onEvent(context, event);
    }

    /**
     * 获取登录方式
     */
    private static String getMethod(User data) {
        if (StringUtils.isNotBlank(data.getCode())) {
            if (StringUtils.isNotBlank(data.getPhone()))
                return "PhoneCode";
            else
                return "EmailCode";
        } else if (StringUtils.isNotBlank(data.getPhone())) {
            return "Phone";
        } else if (StringUtils.isNotBlank(data.getQqId())) {
            return "QQ";
        } else if (StringUtils.isNotBlank(data.getWechatId())) {
            return "Wechat";
        } else {
            return "Email";
        }
    }

    /**
     * 购买事件
     *
     * @param context
     * @param success
     * @param data
     */
    public static void onPurchase(Context context, Boolean success, Order data) {
        //官方文档
        //https://docs.jiguang.cn//janalytics/client/android_api/#_6

//        Goodsid = var1;
//        purchaseGoodsname = var2;
//        purchasePrice = var3;
//        purchaseSuccess = var5;
//        purchaseCurrency = var6;
//        purchaseGoodstype = var7;
//        purchaseGoodsCount = var8;
//        isSetPurchasePrice = true;
//        isSetPurchaseSuccess
        PurchaseEvent event = new PurchaseEvent(
                data.getId(),
                data.getProducts().get(0).getProduct().getTitle(),
                data.getPrice(),
                success,
                Currency.CNY,
                "Product",//商品类型
                data.getProducts().size()
        );

        //添加扩展信息
        event.addKeyValue("order", data.getId());

        JAnalyticsInterface.onEvent(context, event);
    }

    /**
     * 点击了跳过广告
     *
     * @param userId
     */
    public static void onSkipAd(Context context, String userId) {

        //自定义事件名称
        //和iOS那边保持一致
        CountEvent event = new CountEvent("SkipAd");

        //传递了用户Id
        //就可以统计到是谁跳过了
        event.addKeyValue("user", userId);

        //记录事件
        JAnalyticsInterface.onEvent(context, event);
    }
}
