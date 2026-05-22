package com.ixuea.courses.mymusic.activity

import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import com.ixuea.courses.mymusic.R

open class BaseTitleActivity<VB : ViewBinding> : BaseViewModelActivity<VB>() {
    protected lateinit var toolbar: Toolbar

    override fun initViews() {
        super.initViews()
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        if (isShowBackMenu()) {
            showBackMenu()
        }
    }

    protected open fun isShowBackMenu(): Boolean {
        return true
    }

    protected fun showBackMenu() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
