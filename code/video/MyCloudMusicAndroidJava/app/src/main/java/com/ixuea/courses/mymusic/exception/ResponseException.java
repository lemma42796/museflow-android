package com.ixuea.courses.mymusic.exception;

import com.ixuea.courses.mymusic.model.response.BaseResponse;

/**
 * 网络响应错误
 * <p>
 * data字段就是网络响应返回的，根据他做处理
 * 之所以这样封装，是因为使用PagingSource获取数据时，错误统一封装为Throwable
 */
public class ResponseException extends RuntimeException {
    /**
     * 网络响应对象
     */
    private BaseResponse data;

    public ResponseException(BaseResponse data) {
        this.data = data;
    }

    public static Throwable create(BaseResponse data) {
        ResponseException result = new ResponseException(data);
        return result;
    }

    public BaseResponse getData() {
        return data;
    }

    public void setData(BaseResponse data) {
        this.data = data;
    }
}
