package com.ixuea.courses.mymusic.manager.model

class MusicPlayListChange(
    /**
     * 删除的音乐索引。删除全部为 -1；如果要实现多选删除，可以用列表保存。
     */
    val position: Int = -1,
) {
    val isDeleteAll: Boolean
        get() = position == -1
}
