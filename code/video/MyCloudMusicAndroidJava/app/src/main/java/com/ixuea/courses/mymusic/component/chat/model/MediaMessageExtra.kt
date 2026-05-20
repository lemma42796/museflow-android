package com.ixuea.courses.mymusic.component.chat.model

import com.ixuea.courses.mymusic.model.Base

class MediaMessageExtra() : Base() {
    var width: Int = 0
    var height: Int = 0

    constructor(width: Int, height: Int) : this() {
        this.width = width
        this.height = height
    }
}
