package com.ixuea.courses.mymusic.util;

import android.content.Context;

import com.ixuea.courses.mymusic.component.search.model.SearchHistory;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.config.Config;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.util.List;

/**
 * LiteOrm数据库工具类
 */
public class LiteORMUtil {
    private static LiteORMUtil instance;
    private final Context context;
    private final LiteOrm orm;

    private LiteORMUtil(Context context) {
        this.context = context.getApplicationContext();

        //获取偏好设置工具类
        PreferenceUtil sp = PreferenceUtil.getInstance(this.context);

        //创建数据库实例
        String databaseName = String.format("%s.db", sp.getUserId());

        orm = LiteOrm.newSingleInstance(this.context, databaseName);

        //设置调试模式
        orm.setDebugged(Config.DEBUG);
    }

    public synchronized static LiteORMUtil getInstance(Context context) {
        if (instance == null) {
            instance = new LiteORMUtil(context);
        }
        return instance;
    }

    public static void destroy() {
        instance = null;
    }

    //region 搜索历史

    /**
     * 创建或更新搜索历史
     *
     * @param data
     */
    public void createOrUpdate(SearchHistory data) {
        orm.save(data);
    }

    /**
     * 查询所有搜索历史
     *
     * @return
     */
    public List<SearchHistory> querySearchHistory() {
        QueryBuilder<SearchHistory> queryBuilder = new QueryBuilder<SearchHistory>(SearchHistory.class)
                .appendOrderDescBy("createdAt");
        return orm.query(queryBuilder);
    }

    /**
     * 删除搜索历史
     *
     * @param data
     */
    public void deleteSearchHistory(SearchHistory data) {
        orm.delete(data);
    }
    //endregion

    //region 播放列表

    /**
     * 从数据库中查询播放列表
     *
     * @return
     */
    public List<Song> queryPlayList() {
        QueryBuilder<Song> queryBuilder = new QueryBuilder<>(Song.class);
        queryBuilder.whereEquals("list", true);
        queryBuilder.appendOrderDescBy("createdAt");
        return localConverts(orm.query(queryBuilder));
    }

    /**
     * 删除音乐
     *
     * @param data
     */
    public void deleteSong(Song data) {
        orm.delete(data);
    }

    /**
     * 删除所有
     */
    public void deleteAllSong() {
        orm.deleteAll(Song.class);
    }

    /**
     * 删除播放列表
     */
    public void deletePlayListSong() {
        WhereBuilder whereBuilder = WhereBuilder.create(Song.class, "list = ?", new Object[]{true});
        orm.delete(whereBuilder);
    }

    /**
     * 保存所有音乐
     *
     * @param datum
     */
    public void saveAll(List<Song> datum) {
        //将嵌套模型转为单独的字段
        convertLocals(datum);

        orm.save(datum);
    }

    /**
     * 保存音乐
     *
     * @param data
     */
    public void saveSong(Song data) {
        orm.save(data);
    }

    private void convertLocals(List<Song> params) {
        for (Song data : params) {
            data.convertLocal();
        }
    }

    private List<Song> localConverts(List<Song> params) {
        //转换数据
        for (Song data : params) {
            data.localConvert();
        }
        return params;
    }

    /**
     * 根据id查询
     *
     * @param data
     * @return
     */
    public Song querySong(String data) {
        Song song = orm.queryById(data, Song.class);
        if (song != null) {
            song.localConvert();
        }
        return song;
    }

    //endregion

    //region 本地音乐
    public List<Song> queryLocalMusic(int sortIndex) {
        QueryBuilder<Song> queryBuilder = new QueryBuilder<>(Song.class);
        queryBuilder.whereEquals("source", Song.SOURCE_LOCAL);
        queryBuilder.appendOrderAscBy(Song.SORT_KEYS[sortIndex]);
        return localConverts(orm.query(queryBuilder));
    }

    /**
     * 根据id删除本地音乐
     *
     * @param data
     */
    public void deleteSongById(String data) {
        WhereBuilder whereBuilder = WhereBuilder.create(Song.class, "id = ?", new Object[]{data});
        orm.delete(whereBuilder);
    }
    //endregion
}
