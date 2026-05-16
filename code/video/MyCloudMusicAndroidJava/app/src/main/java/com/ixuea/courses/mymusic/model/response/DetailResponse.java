package com.ixuea.courses.mymusic.model.response;

/**
 * 详情网络请求解析类
 * <p>
 * <p>
 * 继承BaseResponse
 * 定义了一个泛型T
 */
public class DetailResponse<T> extends BaseResponse {
    /**
     * 真实数据
     * 类似是泛型
     */
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
