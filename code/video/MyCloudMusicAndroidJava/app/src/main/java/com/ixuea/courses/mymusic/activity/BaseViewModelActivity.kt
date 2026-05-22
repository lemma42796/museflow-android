package com.ixuea.courses.mymusic.activity

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.ixuea.superui.reflect.ReflectUtil

open class BaseViewModelActivity<VB : ViewBinding> : BaseLogicActivity() {
    protected lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ReflectUtil.newViewBinding(layoutInflater, javaClass)
        setContentView(binding.root)
    }
}
