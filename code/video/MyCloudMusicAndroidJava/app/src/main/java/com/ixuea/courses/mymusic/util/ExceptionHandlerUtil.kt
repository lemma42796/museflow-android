package com.ixuea.courses.mymusic.util

import android.text.TextUtils
import com.ixuea.courses.mymusic.AppContext
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.exception.ResponseDecryptException
import com.ixuea.courses.mymusic.exception.ResponseSignException
import com.ixuea.courses.mymusic.model.response.BaseResponse
import com.ixuea.courses.mymusic.view.PlaceholderView
import org.apache.commons.lang3.StringUtils
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 错误处理辅助方法
 */
object ExceptionHandlerUtil {
    /**
     * 网络请求错误处理。
     */
    @JvmStatic
    fun <T> handlerRequest(data: T?, error: Throwable?, placeholderView: PlaceholderView?) {
        if (error != null) {
            handleException(error, placeholderView)
            return
        }

        when (data) {
            is Response<*> -> {
                val code = data.code()
                if (code !in 200..299) {
                    handleHttpError(code, placeholderView)
                }
            }

            is BaseResponse -> {
                if (TextUtils.isEmpty(data.message)) {
                    TipUtil.showError(R.string.error_unknown, placeholderView)
                } else {
                    TipUtil.showError(data.message, placeholderView)
                }
            }
        }
    }

    /**
     * 处理异常。
     */
    @JvmStatic
    fun handleException(error: Throwable, placeholderView: PlaceholderView?) {
        when (error) {
            is UnknownHostException -> TipUtil.showError(
                R.string.error_network_unknown_host,
                placeholderView,
                R.string.network_error_click_reload
            )

            is ConnectException -> TipUtil.showError(
                R.string.network_error,
                placeholderView,
                R.string.network_error_click_reload
            )

            is SocketTimeoutException -> TipUtil.showError(
                R.string.error_network_timeout,
                placeholderView,
                R.string.error_network_timeout
            )

            is HttpException -> handleHttpError(error.code(), placeholderView)
            is IllegalArgumentException -> TipUtil.showError(R.string.error_parameter, placeholderView)
            is ResponseSignException -> TipUtil.showError(R.string.error_response_sign, placeholderView)
            is ResponseDecryptException -> TipUtil.showError(R.string.error_response_decrypt, placeholderView)
            else -> {
                val localizedMessage = error.localizedMessage
                val message = if (StringUtils.isNotBlank(localizedMessage)) {
                    AppContext.getInstance().getString(R.string.error_unknown_format, localizedMessage)
                } else {
                    AppContext.getInstance().getString(R.string.error_unknown)
                }
                TipUtil.showError(message, placeholderView)
            }
        }
    }

    private fun handleHttpError(code: Int, placeholderView: PlaceholderView?) {
        when {
            code == 401 -> {
                TipUtil.showError(R.string.error_network_not_auth, placeholderView)
                AppContext.getInstance().logout()
            }

            code == 403 -> TipUtil.showError(R.string.error_network_not_permission, placeholderView)
            code == 404 -> TipUtil.showError(R.string.error_network_not_found, placeholderView)
            code >= 500 -> TipUtil.showError(R.string.error_network_server, placeholderView)
            else -> TipUtil.showError(R.string.error_unknown, placeholderView)
        }
    }
}
