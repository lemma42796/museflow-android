package com.ixuea.courses.mymusic.util;

import android.text.TextUtils;

import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.exception.ResponseDecryptException;
import com.ixuea.courses.mymusic.exception.ResponseSignException;
import com.ixuea.courses.mymusic.model.response.BaseResponse;
import com.ixuea.courses.mymusic.view.PlaceholderView;

import org.apache.commons.lang3.StringUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;
import retrofit2.Response;

/**
 * 错误处理辅助方法
 */
public class ExceptionHandlerUtil {
    /**
     * 网络请求错误处理
     *
     * @param data
     * @param error
     * @param placeholderView
     */
    public static <T> void handlerRequest(T data, Throwable error, PlaceholderView placeholderView) {
        if (error != null) {
            //先处理有异常的请求
            handleException(error, placeholderView);
        } else {
            if (data instanceof Response) {
                //retrofit里面的对象

                //获取响应对象
                Response response = (Response) data;

                //获取响应码
                int code = response.code();

                //判断响应码
                if (code >= 200 && code <= 299) {
                    //网络请求正常
                } else {
                    handleHttpError(code, placeholderView);
                }
            } else if (data instanceof BaseResponse) {
                //判断具体的业务请求是否成功
                BaseResponse response = (BaseResponse) data;

                if (TextUtils.isEmpty(response.getMessage())) {
                    //没有错误提示信息
                    TipUtil.showError(R.string.error_unknown, placeholderView);
                } else {
                    TipUtil.showError(response.getMessage(), placeholderView);
                }
            }
        }
    }

    /**
     * 处理异常
     */
    public static void handleException(Throwable error, PlaceholderView placeholderView) {
//        if (error.getCause() != null) {
//            //如果发生这个异常有原因，就先判断原因
//            error = error.getCause();
//        }

        //判断错误类型
        if (error instanceof UnknownHostException) {
            TipUtil.showError(R.string.error_network_unknown_host, placeholderView, R.string.network_error_click_reload);
        } else if (error instanceof ConnectException) {
            TipUtil.showError(R.string.network_error, placeholderView, R.string.network_error_click_reload);
        } else if (error instanceof SocketTimeoutException) {
            TipUtil.showError(R.string.error_network_timeout, placeholderView, R.string.error_network_timeout);
        } else if (error instanceof HttpException) {
            HttpException exception = (HttpException) error;

            //获取响应码
            int code = exception.code();

            handleHttpError(code, placeholderView);
        } else if (error instanceof IllegalArgumentException) {
            //本地参数错误
            TipUtil.showError(R.string.error_parameter, placeholderView);
        } else if (error instanceof ResponseSignException) {
            //真实项目中一般很少会提示这么明显
            //因为错误提示的越详细
            //对攻击者也就越详细了
            TipUtil.showError(R.string.error_response_sign, placeholderView);
        } else if (error instanceof ResponseDecryptException) {
            //真实项目中一般很少会提示这么明显
            //因为错误提示的越详细
            //对攻击者也就越详细了
            TipUtil.showError(R.string.error_response_decrypt, placeholderView);
        } else {
            String message = error.getLocalizedMessage();
            if (StringUtils.isNotBlank(message)) {
                message = AppContext.getInstance().getString(R.string.error_unknown_format, message);
            } else {
                message = AppContext.getInstance().getString(R.string.error_unknown);
            }
            TipUtil.showError(message, placeholderView);
        }
    }

    /**
     * 处理Http错误
     *
     * @param code
     * @param placeholderView
     */
    private static void handleHttpError(int code, PlaceholderView placeholderView) {
        if (code == 401) {
            TipUtil.showError(R.string.error_network_not_auth, placeholderView);

            AppContext.getInstance().logout();
        } else if (code == 403) {
            TipUtil.showError(R.string.error_network_not_permission, placeholderView);
        } else if (code == 404) {
            TipUtil.showError(R.string.error_network_not_found, placeholderView);
        } else if (code >= 500) {
            TipUtil.showError(R.string.error_network_server, placeholderView);
        } else {
            TipUtil.showError(R.string.error_unknown, placeholderView);
        }
    }
}
