package com.ixuea.courses.mymusic.component.discovery.model.ui

import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity
import com.ixuea.courses.mymusic.util.Constant

/**
 * 发现界面，歌单外层容器数据
 */
class SheetData(
    var data: MutableList<Sheet>,
    sort: Int,
) : BaseSort(sort), BaseMultiItemEntity {
    override val itemType: Int
        get() = Constant.STYLE_SHEET
}
