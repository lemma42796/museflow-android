package com.ixuea.courses.mymusic.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import com.ixuea.courses.mymusic.util.Constant

open class BaseCommonActivity : BaseActivity() {
    protected fun startActivity(clazz: Class<out Activity>) {
        startActivity(Intent(this, clazz))
    }

    protected fun startActivityAfterFinishThis(clazz: Class<out Activity>) {
        startActivity(clazz)
        finish()
    }

    protected fun startActivityExtraId(clazz: Class<*>, id: String?) {
        val intent = Intent(this, clazz)
        if (!id.isNullOrEmpty()) {
            intent.putExtra(Constant.ID, id)
        }
        startActivity(intent)
    }

    protected fun startActivityExtraData(clazz: Class<*>, data: Parcelable) {
        val intent = Intent(this, clazz)
        intent.putExtra(Constant.DATA, data)
        startActivity(intent)
    }

    protected fun extraString(key: String): String? {
        return intent.getStringExtra(key)
    }

    protected fun extraInt(key: String): Int {
        return intent.getIntExtra(key, -1)
    }

    @Suppress("DEPRECATION", "UNCHECKED_CAST")
    protected fun <T> extraData(): T {
        return requireNotNull(intent.getParcelableExtra<Parcelable>(Constant.DATA) as? T)
    }

    protected fun extraId(): String? {
        return extraString(Constant.ID)
    }

    protected fun setStatusBarColor(data: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = data
            window.navigationBarColor = data
        }
    }
}
