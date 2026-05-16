package com.ixuea.courses.mymusic.component.search.fragment;

import android.os.Bundle;

/**
 * 其他搜索结果fragment
 */
public class OtherFragment extends BaseSearchFragment {
    public static OtherFragment newInstance() {

        Bundle args = new Bundle();

        OtherFragment fragment = new OtherFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
