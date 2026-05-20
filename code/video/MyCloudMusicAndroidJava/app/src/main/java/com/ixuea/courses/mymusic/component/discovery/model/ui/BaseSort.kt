package com.ixuea.courses.mymusic.component.discovery.model.ui

/**
 * 排序相关字段
 */
open class BaseSort @JvmOverloads constructor(
    var sort: Int = 0,
) : Comparable<BaseSort> {
    override fun compareTo(other: BaseSort): Int {
        return sort.compareTo(other.sort)
    }
}
