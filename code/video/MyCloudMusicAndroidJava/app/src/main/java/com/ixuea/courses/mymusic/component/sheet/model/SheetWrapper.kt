package com.ixuea.courses.mymusic.component.sheet.model

import com.ixuea.courses.mymusic.model.response.Meta

/**
 * 用来解析歌单列表数据
 */
class SheetWrapper {
    var status: Int = 0
    var message: String? = null
    var data: Meta<Sheet>? = null
}
