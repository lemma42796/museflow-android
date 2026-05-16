package com.ixuea.courses.mymusic.component.discovery.model.ui;

import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;
import com.ixuea.courses.mymusic.util.Constant;

import java.util.List;

/**
 * 发现界面，单曲外层容器数据
 */
public class SongData extends BaseSort implements BaseMultiItemEntity {
    private List<Song> data;

    public SongData(List<Song> data, int sort) {
        super(sort);
        this.data = data;
    }

    public List<Song> getData() {
        return data;
    }

    public void setData(List<Song> data) {
        this.data = data;
    }

    @Override
    public int getItemType() {
        return Constant.STYLE_SONG;
    }
}
