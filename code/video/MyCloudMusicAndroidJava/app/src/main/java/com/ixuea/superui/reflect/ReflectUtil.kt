package com.ixuea.superui.reflect

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * 反射工具类。
 */
object ReflectUtil {
    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun <VB : ViewBinding> newViewBinding(layoutInflater: LayoutInflater, clazz: Class<*>): VB {
        try {
            val type = try {
                clazz.genericSuperclass as ParameterizedType
            } catch (e: ClassCastException) {
                clazz.superclass.genericSuperclass as ParameterizedType
            }

            val bindingClass = type.actualTypeArguments[0] as Class<VB>
            val inflateMethod = bindingClass.getMethod("inflate", LayoutInflater::class.java)
            return inflateMethod.invoke(null, layoutInflater) as VB
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }
}
