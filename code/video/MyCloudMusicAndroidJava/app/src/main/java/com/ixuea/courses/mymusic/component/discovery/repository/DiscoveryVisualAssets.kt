package com.ixuea.courses.mymusic.component.discovery.repository

import com.ixuea.courses.mymusic.BuildConfig
import com.ixuea.courses.mymusic.component.ad.model.Ad
import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import com.ixuea.courses.mymusic.component.song.model.Song

internal object DiscoveryVisualAssets {
    private val bannerImages = listOf(
        localDrawableUri("discovery_banner_android16"),
    )

    private val coverImages = listOf(
        localDrawableUri("discovery_cover_android16_1"),
        localDrawableUri("discovery_cover_android16_2"),
        localDrawableUri("discovery_cover_android16_3"),
        localDrawableUri("discovery_cover_android16_4"),
        localDrawableUri("discovery_cover_android16_5"),
        localDrawableUri("discovery_cover_android16_6"),
    )

    private val songCoverImages = listOf(
        localDrawableUri("discovery_song_cover_bright_1"),
        localDrawableUri("discovery_cover_android16_5"),
        localDrawableUri("discovery_cover_android16_6"),
        localDrawableUri("discovery_cover_android16_1"),
        localDrawableUri("discovery_cover_android16_2"),
        localDrawableUri("discovery_cover_android16_3"),
    )

    private val sheetTitles = listOf(
        "晨光通勤电台",
        "周末轻盈律动",
        "夜色城市漫游",
        "专注时刻白噪",
        "微醺独立流行",
        "治愈系晚安曲",
        "新鲜电子心跳",
        "雨天柔软民谣",
        "午后咖啡旋律",
        "运动能量补给",
        "深夜灵感采样",
        "清爽夏日合辑",
    )

    fun applyTo(
        ads: MutableList<Ad>,
        sheets: MutableList<Sheet>,
        songs: MutableList<Song>,
    ) {
        ads.forEachIndexed { index, ad ->
            ad.icon = bannerImages[index % bannerImages.size]
        }
        sheets.forEachIndexed { index, sheet ->
            sheet.icon = coverImages[index % coverImages.size]
            sheet.title = sheetTitles[index % sheetTitles.size]
        }
        songs.forEachIndexed { index, song ->
            song.icon = songCoverImages[index % songCoverImages.size]
        }
    }

    fun fallbackBanner(): Ad {
        return Ad().apply {
            title = "MuseFlow"
            icon = bannerImages.first()
        }
    }

    private fun localDrawableUri(name: String): String {
        return "android.resource://${BuildConfig.APPLICATION_ID}/drawable/$name"
    }
}
