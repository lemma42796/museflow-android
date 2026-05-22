package com.ixuea.courses.mymusic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.ixuea.courses.mymusic.activity.BaseCommonActivity

abstract class BaseFragment : Fragment() {
    protected open fun initViews() = Unit

    protected open fun initDatum() = Unit

    protected open fun initListeners() = Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return getLayoutView(inflater, container, savedInstanceState)
    }

    protected abstract fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initDatum()
        initListeners()
    }

    fun <T : View> findViewById(@IdRes id: Int): T {
        return requireView().findViewById(id)
    }

    val hostActivity: BaseCommonActivity
        get() = requireActivity() as BaseCommonActivity
}
