package com.ixuea.courses.mymusic.component.observer

import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

/**
 * 通用实现 Observer 里面的方法，避免每次都要实现所有回调。
 */
open class ObserverAdapter<T : Any> : Observer<T> {
    override fun onSubscribe(d: Disposable) {
    }

    override fun onNext(t: T) {
    }

    override fun onError(e: Throwable) {
    }

    override fun onComplete() {
    }
}
