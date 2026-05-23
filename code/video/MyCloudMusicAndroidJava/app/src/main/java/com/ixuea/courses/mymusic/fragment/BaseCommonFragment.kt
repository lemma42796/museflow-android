package com.ixuea.courses.mymusic.fragment

import android.content.Intent
import android.os.Parcelable
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.getParcelableCompat

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

    protected inline fun <reified T : Parcelable> extraData(): T {
        return requireNotNull(arguments?.getParcelableCompat<T>(Constant.DATA))
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
