package com.ixuea.courses.mymusic.component.discovery.model;

import com.ixuea.courses.mymusic.model.response.BaseResponse;
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;

import java.util.List;

/**
 * Aggregated discovery page sections.
 */
public class DiscoveryPage extends BaseResponse {
    private final List<BaseMultiItemEntity> sections;

    public DiscoveryPage(List<BaseMultiItemEntity> sections) {
        this.sections = sections;
    }

    public List<BaseMultiItemEntity> getSections() {
        return sections;
    }
}
