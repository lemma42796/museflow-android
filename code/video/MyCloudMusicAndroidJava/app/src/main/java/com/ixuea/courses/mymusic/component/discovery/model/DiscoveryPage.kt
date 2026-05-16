package com.ixuea.courses.mymusic.component.discovery.model

import com.ixuea.courses.mymusic.model.response.BaseResponse
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity

/**
 * Aggregated discovery page sections.
 */
class DiscoveryPage(
    val sections: List<BaseMultiItemEntity>,
) : BaseResponse()
