package com.ixuea.courses.mymusic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.ixuea.courses.mymusic.manager.MusicListManager
import com.ixuea.courses.mymusic.playback.PlaybackService
import com.ixuea.superui.reflect.ReflectUtil

open class BaseViewModelBottomSheetDialogFragment<VB : ViewBinding> :
    BaseBottomSheetDialogFragment() {

    private var bindingDelegate: VB? = null

    protected val binding: VB
        get() = requireNotNull(bindingDelegate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingDelegate = ReflectUtil.newViewBinding(layoutInflater, javaClass)
    }

    override fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingDelegate = null
    }

    protected val musicListManager: MusicListManager
        get() = PlaybackService.getListManager(requireActivity().applicationContext)
}
