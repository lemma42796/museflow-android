package com.ixuea.courses.mymusic.component.discovery.repository

import com.ixuea.courses.mymusic.component.ad.model.Ad
import com.ixuea.courses.mymusic.component.discovery.model.DiscoveryPage
import com.ixuea.courses.mymusic.component.discovery.model.ui.BannerData
import com.ixuea.courses.mymusic.component.discovery.model.ui.BaseSort
import com.ixuea.courses.mymusic.component.discovery.model.ui.ButtonData
import com.ixuea.courses.mymusic.component.discovery.model.ui.FooterData
import com.ixuea.courses.mymusic.component.discovery.model.ui.SheetData
import com.ixuea.courses.mymusic.component.discovery.model.ui.SongData
import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.model.response.ListResponse
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity
import com.ixuea.courses.mymusic.repository.DefaultRepository
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.PreferenceUtil
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Builds typed immutable sections for the discovery page.
 */
class DiscoveryRepository private constructor(
    private val repository: DefaultRepository,
) {
    suspend fun homeSections(sp: PreferenceUtil): DiscoveryPage = coroutineScope {
        val ads = async { repository.bannerAd() }
        val sheets = async { repository.sheets(Constant.SIZE12) }
        val songs = async { repository.songs() }

        DiscoveryPage(
            buildSections(
                sp,
                ads.await(),
                sheets.await(),
                songs.await(),
            )
        )
    }

    private fun buildSections(
        sp: PreferenceUtil,
        ads: ListResponse<Ad>,
        sheets: ListResponse<Sheet>,
        songs: ListResponse<Song>,
    ): List<BaseMultiItemEntity> {
        val sections = mutableListOf<BaseMultiItemEntity>()
        sections += BannerData(ads.data?.data.orEmpty().toMutableList(), sp.getSort(Constant.STYLE_BANNER))
        sections += ButtonData(sp.getSort(Constant.STYLE_BUTTON))
        sections += SheetData(sheets.data?.data.orEmpty().toMutableList(), sp.getSort(Constant.STYLE_SHEET))
        sections += SongData(songs.data?.data.orEmpty().toMutableList(), sp.getSort(Constant.STYLE_SONG))
        sections += FooterData()
        sections.sortWith { first, second ->
            (first as BaseSort).compareTo(second as BaseSort)
        }
        return sections.toList()
    }

    companion object {
        @Volatile
        private var instance: DiscoveryRepository? = null

        @JvmStatic
        fun getInstance(): DiscoveryRepository {
            return instance ?: synchronized(this) {
                instance ?: DiscoveryRepository(DefaultRepository.getInstance()).also {
                    instance = it
                }
            }
        }
    }
}
