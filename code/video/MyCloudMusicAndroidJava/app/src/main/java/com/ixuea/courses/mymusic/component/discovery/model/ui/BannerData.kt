package com.ixuea.courses.mymusic.component.discovery.model.ui

import com.ixuea.courses.mymusic.component.ad.model.Ad
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity
import com.ixuea.courses.mymusic.util.Constant

/**
 * 发现界面，轮播图数据
 */
class BannerData(
    var data: MutableList<Ad>,
    sort: Int,
) : BaseSort(sort), BaseMultiItemEntity {
    override val itemType: Int
        get() = Constant.STYLE_BANNER
}
