package com.ixuea.courses.mymusic.fragment

import android.content.Intent
import android.os.Parcelable
import com.ixuea.courses.mymusic.util.Constant

abstract class BaseCommonFragment : BaseFragment() {
    protected fun startActivity(clazz: Class<*>) {
        startActivity(Intent(hostActivity, clazz))
    }

    protected fun startActivityAfterFinishThis(clazz: Class<*>) {
        startActivity(Intent(hostActivity, clazz))
        hostActivity.finish()
    }

    protected fun startActivityExtraId(clazz: Class<*>, id: String?) {
        val intent = Intent(hostActivity, clazz)
        if (!id.isNullOrEmpty()) {
            intent.putExtra(Constant.ID, id)
        }
        startActivity(intent)
    }

    @Suppress("DEPRECATION", "UNCHECKED_CAST")
    protected fun <T> extraData(): T {
        return requireNotNull(arguments?.getParcelable<Parcelable>(Constant.DATA) as? T)
    }

    protected fun extraString(key: String): String? {
        return arguments?.getString(key)
    }

    protected fun extraId(): String? {
        return extraString(Constant.ID)
    }

    protected fun extraInt(key: String): Int {
        return arguments?.getInt(key, -1) ?: -1
    }
}
