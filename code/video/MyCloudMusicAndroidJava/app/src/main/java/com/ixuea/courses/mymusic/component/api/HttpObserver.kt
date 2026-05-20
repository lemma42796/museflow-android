package com.ixuea.courses.mymusic.component.api

import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.observer.ObserverAdapter
import com.ixuea.courses.mymusic.fragment.BaseLogicFragment
import com.ixuea.courses.mymusic.model.response.BaseResponse
import com.ixuea.courses.mymusic.util.ExceptionHandlerUtil
import com.ixuea.courses.mymusic.view.PlaceholderView
import com.ixuea.superui.util.SuperViewUtil
import io.reactivex.rxjava3.disposables.Disposable
import retrofit2.Response

/**
 * 网络请求 Observer
 */
abstract class HttpObserver<T : Any> : ObserverAdapter<T> {
    private var fragment: BaseLogicFragment? = null
    private var activity: BaseLogicActivity? = null
    private var isShowLoading: Boolean = false

    constructor()

    constructor(activity: BaseLogicActivity) {
        this.activity = activity
    }

    constructor(activity: BaseLogicActivity, isShowLoading: Boolean) {
        this.activity = activity
        this.isShowLoading = isShowLoading
    }

    constructor(fragment: BaseLogicFragment) {
        this.fragment = fragment
    }

    constructor(fragment: BaseLogicFragment, isShowLoading: Boolean) {
        this.fragment = fragment
        this.activity = fragment.activity as? BaseLogicActivity
        this.isShowLoading = isShowLoading
    }

    /**
     * 请求成功
     */
    abstract fun onSucceeded(data: T)

    /**
     * 请求失败。
     *
     * @return true 表示外部已处理；false 表示继续交给框架处理。
     */
    open fun onFailed(data: T?, e: Throwable?): Boolean {
        return false
    }

    /**
     * 请求结束，成功失败都会调用，常用于隐藏加载提示。
     */
    open fun onEnd() {
        if (isShowLoading) {
            activity?.hideLoading()
        }
    }

    override fun onSubscribe(d: Disposable) {
        super.onSubscribe(d)

        if (isShowLoading) {
            activity?.showLoading()
        }

        getPlaceholderView()?.let {
            SuperViewUtil.gone(it)
        }
    }

    override fun onNext(t: T) {
        super.onNext(t)
        onEnd()

        if (isSucceeded(t)) {
            onSucceeded(t)
        } else {
            handlerRequest(t, null)
        }
    }

    override fun onError(e: Throwable) {
        super.onError(e)
        onEnd()
        handlerRequest(null, e)
    }

    private fun handlerRequest(data: T?, error: Throwable?) {
        if (!onFailed(data, error)) {
            ExceptionHandlerUtil.handlerRequest(data, error, getPlaceholderView())
        }
    }

    private fun getPlaceholderView(): PlaceholderView? {
        return activity?.placeholderView ?: fragment?.placeholderView
    }

    private fun isSucceeded(data: T): Boolean {
        return when (data) {
            is Response<*> -> data.code() in 200..299
            is BaseResponse -> data.isSucceeded()
            else -> false
        }
    }
}
