package com.ixuea.courses.mymusic.manager;

/**
 * 全局（桌面）歌词管理器
 */
public interface GlobalLyricManager {
    /**
     * 显示桌面歌词
     */
    void show();

    /**
     * 隐藏桌面歌词
     */
    void hide();

    /**
     * 桌面歌词是否显示了
     *
     * @return
     */
    boolean isShowing();

    /**
     * 尝试隐藏
     */
    void tryHide();

    /**
     * 尝试显示
     */
    void tryShow();
}
