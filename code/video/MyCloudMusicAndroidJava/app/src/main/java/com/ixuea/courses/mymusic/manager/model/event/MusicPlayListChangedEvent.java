package com.ixuea.courses.mymusic.manager.model.event;

/**
 * 播放列表改变了事件
 */
public class MusicPlayListChangedEvent {
    /**
     * 删除的音乐索引
     * 删除全部为-1；如果要实现多选删除，可以用列表保存
     */
    private int position = -1;

    public MusicPlayListChangedEvent(int position) {
        this.position = position;
    }

    public boolean isDeleteAll() {
        return position == -1;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
