package com.ixuea.courses.mymusic.component.discovery.model.ui

import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity
import com.ixuea.courses.mymusic.util.Constant

/**
 * 发现界面，尾部数据
 */
class FooterData : BaseSort(Int.MAX_VALUE), BaseMultiItemEntity {
    override val itemType: Int
        get() = Constant.STYLE_FOOTER
}
