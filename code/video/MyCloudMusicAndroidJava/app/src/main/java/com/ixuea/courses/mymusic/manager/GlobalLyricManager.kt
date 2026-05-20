package com.ixuea.courses.mymusic.manager

/**
 * 全局（桌面）歌词管理器
 */
interface GlobalLyricManager {
    fun show()

    fun hide()

    fun isShowing(): Boolean

    fun tryHide()

    fun tryShow()
}
