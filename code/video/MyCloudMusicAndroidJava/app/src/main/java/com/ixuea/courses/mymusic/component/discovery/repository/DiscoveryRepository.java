package com.ixuea.courses.mymusic.component.discovery.repository;

import com.ixuea.courses.mymusic.component.ad.model.Ad;
import com.ixuea.courses.mymusic.component.discovery.model.DiscoveryPage;
import com.ixuea.courses.mymusic.component.discovery.model.ui.BannerData;
import com.ixuea.courses.mymusic.component.discovery.model.ui.BaseSort;
import com.ixuea.courses.mymusic.component.discovery.model.ui.ButtonData;
import com.ixuea.courses.mymusic.component.discovery.model.ui.FooterData;
import com.ixuea.courses.mymusic.component.discovery.model.ui.SheetData;
import com.ixuea.courses.mymusic.component.discovery.model.ui.SongData;
import com.ixuea.courses.mymusic.component.sheet.model.Sheet;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.model.response.ListResponse;
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.PreferenceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

/**
 * Builds typed immutable sections for the discovery page.
 */
public class DiscoveryRepository {
    private static DiscoveryRepository instance;
    private final DefaultRepository repository;

    private DiscoveryRepository(DefaultRepository repository) {
        this.repository = repository;
    }

    public synchronized static DiscoveryRepository getInstance() {
        if (instance == null) {
            instance = new DiscoveryRepository(DefaultRepository.getInstance());
        }
        return instance;
    }

    public Observable<DiscoveryPage> homeSections(PreferenceUtil sp) {
        return Observable.zip(
                repository.bannerAd(),
                repository.sheets(Constant.SIZE12),
                repository.songs(),
                (ads, sheets, songs) -> new DiscoveryPage(buildSections(sp, ads, sheets, songs))
        );
    }

    private List<BaseMultiItemEntity> buildSections(
            PreferenceUtil sp,
            ListResponse<Ad> ads,
            ListResponse<Sheet> sheets,
            ListResponse<Song> songs
    ) {
        ArrayList<BaseMultiItemEntity> sections = new ArrayList<>();
        sections.add(new BannerData(ads.getData().getData(), sp.getSort(Constant.STYLE_BANNER)));
        sections.add(new ButtonData(sp.getSort(Constant.STYLE_BUTTON)));
        sections.add(new SheetData(sheets.getData().getData(), sp.getSort(Constant.STYLE_SHEET)));
        sections.add(new SongData(songs.getData().getData(), sp.getSort(Constant.STYLE_SONG)));
        sections.add(new FooterData());
        Collections.sort(sections, (o1, o2) -> ((BaseSort) o1).compareTo((BaseSort) o2));
        return Collections.unmodifiableList(sections);
    }
}
