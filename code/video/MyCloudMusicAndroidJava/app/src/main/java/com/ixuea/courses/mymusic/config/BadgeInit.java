package com.ixuea.courses.mymusic.config;

import android.widget.ImageView;
import android.widget.TextView;

import cn.bingoogolapple.badgeview.annotation.BGABadge;

/**
 * 初始化给控件添加未读消息数红点框架
 * https://github.com/bingoogolapple/BGABadgeView-Android
 */
@BGABadge({
        ImageView.class, // 对应 cn.bingoogolapple.badgeview.BGABadgeImageView，不想用这个类的话就删了这一行
        TextView.class // 对应 cn.bingoogolapple.badgeview.BGABadgeTextView，不想用这个类的话就删了这一行
})
public class BadgeInit {
}
