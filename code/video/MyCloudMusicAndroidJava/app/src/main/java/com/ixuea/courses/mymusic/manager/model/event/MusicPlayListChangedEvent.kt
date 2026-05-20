package com.ixuea.courses.mymusic.manager.model.event

/**
 * 播放列表改变了事件
 */
class MusicPlayListChangedEvent(
    /**
     * 删除的音乐索引。删除全部为 -1；如果要实现多选删除，可以用列表保存。
     */
    var position: Int = -1,
) {
    val isDeleteAll: Boolean
        get() = position == -1
}
