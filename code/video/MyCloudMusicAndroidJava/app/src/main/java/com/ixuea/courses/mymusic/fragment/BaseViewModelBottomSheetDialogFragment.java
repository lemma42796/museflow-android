package com.ixuea.courses.mymusic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.ixuea.courses.mymusic.manager.MusicListManager;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.superui.reflect.ReflectUtil;


/**
 * 所有BottomSheetDialogFragment父类
 */
public class BaseViewModelBottomSheetDialogFragment<VB extends ViewBinding> extends BaseBottomSheetDialogFragment {
    protected VB binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //调用inflate方法，创建viewBinding
        binding = ReflectUtil.newViewBinding(getLayoutInflater(), this.getClass());
    }

    @Override
    protected View getLayoutView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * 获取播放列表管理器
     *
     * @return
     */
    protected MusicListManager getMusicListManager() {
        return MusicPlayerService.getListManager(getActivity().getApplicationContext());
    }
}
