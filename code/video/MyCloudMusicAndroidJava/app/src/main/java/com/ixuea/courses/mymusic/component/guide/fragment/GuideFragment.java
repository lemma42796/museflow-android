package com.ixuea.courses.mymusic.component.guide.fragment;

import android.os.Bundle;

import com.ixuea.courses.mymusic.databinding.FragmentGuideBinding;
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment;
import com.ixuea.courses.mymusic.util.Constant;

/**
 * 引导界面Fragment
 */
public class GuideFragment extends BaseViewModelFragment<FragmentGuideBinding> {
    public static GuideFragment newInstance(Integer data) {

        Bundle args = new Bundle();
        args.putInt(Constant.ID, data);

        GuideFragment fragment = new GuideFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        int data = getArguments().getInt(Constant.ID);
        binding.icon.setImageResource(data);
    }
}
