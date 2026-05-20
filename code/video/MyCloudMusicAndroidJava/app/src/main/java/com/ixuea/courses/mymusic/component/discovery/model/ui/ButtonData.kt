package com.ixuea.courses.mymusic.component.discovery.model.ui

import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity
import com.ixuea.courses.mymusic.util.Constant

/**
 * 发现界面，快捷按钮数据
 */
class ButtonData(sort: Int) : BaseSort(sort), BaseMultiItemEntity {
    val data: MutableList<IconTitleButtonData>
        get() = results

    override val itemType: Int
        get() = Constant.STYLE_BUTTON

    companion object {
        private val results = arrayListOf(
            IconTitleButtonData(R.drawable.day_recommend, R.string.day_recommend),
            IconTitleButtonData(R.drawable.person_fm, R.string.person_fm),
            IconTitleButtonData(R.drawable.sheet, R.string.sheet),
            IconTitleButtonData(R.drawable.rank, R.string.rank),
            IconTitleButtonData(R.drawable.button_live, R.string.live),
            IconTitleButtonData(R.drawable.digital_album, R.string.digital_album),
            IconTitleButtonData(R.drawable.digital_album, R.string.digital_album),
        )
    }
}
