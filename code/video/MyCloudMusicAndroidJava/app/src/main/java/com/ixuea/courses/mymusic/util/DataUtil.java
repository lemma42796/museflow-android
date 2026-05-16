package com.ixuea.courses.mymusic.util;

import com.ixuea.courses.mymusic.component.song.model.Song;

import java.util.List;

public class DataUtil {
    public static void changePlayListFlag(List<Song> datum, boolean value) {
        for (Song data : datum) {
            data.setPlayList(value);
        }
    }
}
