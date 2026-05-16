package com.ixuea.courses.mymusic.component.discovery.model.ui;

/**
 * 排序相关字段
 */
public class BaseSort implements Comparable<BaseSort> {
    /**
     * 排序字段
     */
    private int sort;

    public BaseSort() {
    }

    public BaseSort(int sort) {
        this.sort = sort;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public int compareTo(BaseSort o) {
        if (sort > o.sort) {
            return 1;
        } else if (sort < o.sort) {
            return -1;
        }
        return 0;
    }
}
