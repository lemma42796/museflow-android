package com.ixuea.courses.mymusic.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    protected open fun initViews() = Unit

    protected open fun initDatum() = Unit

    protected open fun initListeners() = Unit

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        initViews()
        initDatum()
        initListeners()
    }
}
