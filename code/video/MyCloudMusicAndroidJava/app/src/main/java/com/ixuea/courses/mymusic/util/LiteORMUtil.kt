package com.ixuea.courses.mymusic.util

import android.content.Context
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.config.Config
import com.litesuits.orm.LiteOrm
import com.litesuits.orm.db.assit.QueryBuilder
import com.litesuits.orm.db.assit.WhereBuilder

/**
 * LiteOrm 数据库工具类
 */
class LiteORMUtil private constructor(context: Context) {
    private val context: Context = context.applicationContext
    private val orm: LiteOrm

    init {
        val sp = PreferenceUtil.getInstance(this.context)
        val databaseName = String.format("%s.db", sp.userId)
        orm = LiteOrm.newSingleInstance(this.context, databaseName)
        orm.setDebugged(Config.DEBUG)
    }

    /**
     * 从数据库中查询播放列表。
     */
    fun queryPlayList(): MutableList<Song> {
        val queryBuilder = QueryBuilder<Song>(Song::class.java)
        queryBuilder.whereEquals("list", true)
        queryBuilder.appendOrderDescBy("createdAt")
        return localConverts(orm.query(queryBuilder))
    }

    /**
     * 删除音乐。
     */
    fun deleteSong(data: Song) {
        orm.delete(data)
    }

    /**
     * 删除所有。
     */
    fun deleteAllSong() {
        orm.deleteAll(Song::class.java)
    }

    /**
     * 删除播放列表。
     */
    fun deletePlayListSong() {
        val whereBuilder = WhereBuilder.create(Song::class.java, "list = ?", arrayOf(true))
        orm.delete(whereBuilder)
    }

    /**
     * 保存所有音乐。
     */
    fun saveAll(datum: List<Song>) {
        convertLocals(datum)
        orm.save(datum)
    }

    /**
     * 保存音乐。
     */
    fun saveSong(data: Song) {
        orm.save(data)
    }

    private fun convertLocals(params: List<Song>) {
        params.forEach { it.convertLocal() }
    }

    private fun localConverts(params: MutableList<Song>): MutableList<Song> {
        params.forEach { it.localConvert() }
        return params
    }

    /**
     * 根据 id 查询。
     */
    fun querySong(data: String?): Song? {
        val song = orm.queryById(data, Song::class.java)
        song?.localConvert()
        return song
    }

    /**
     * 查询本地音乐。
     */
    fun queryLocalMusic(sortIndex: Int): MutableList<Song> {
        val queryBuilder = QueryBuilder<Song>(Song::class.java)
        queryBuilder.whereEquals("source", Song.SOURCE_LOCAL)
        queryBuilder.appendOrderAscBy(Song.SORT_KEYS[sortIndex])
        return localConverts(orm.query(queryBuilder))
    }

    /**
     * 根据 id 删除本地音乐。
     */
    fun deleteSongById(data: String?) {
        val whereBuilder = WhereBuilder.create(Song::class.java, "id = ?", arrayOf(data))
        orm.delete(whereBuilder)
    }

    companion object {
        @Volatile
        private var instance: LiteORMUtil? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): LiteORMUtil {
            if (instance == null) {
                instance = LiteORMUtil(context)
            }
            return instance!!
        }

        @JvmStatic
        fun destroy() {
            instance = null
        }
    }
}
