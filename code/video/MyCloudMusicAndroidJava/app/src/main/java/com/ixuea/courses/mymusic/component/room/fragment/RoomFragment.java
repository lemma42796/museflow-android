package com.ixuea.courses.mymusic.component.room.fragment;

import android.os.Bundle;

import com.ixuea.courses.mymusic.databinding.FragmentRoomBinding;
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment;

/**
 * 首页-直播界面
 */
public class RoomFragment extends BaseViewModelFragment<FragmentRoomBinding> {
    public static RoomFragment newInstance() {

        Bundle args = new Bundle();

        RoomFragment fragment = new RoomFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
