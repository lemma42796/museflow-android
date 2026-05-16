package com.ixuea.courses.mymusic.component.feed.task;

/**
 * 结果模型
 * <p>
 * 主要用于向UI返回结果
 *
 * @param <T>
 */
public class Result<T> {
    private T data;

    private Throwable throwable;

    public Result(T data) {
        this.data = data;
    }

    public Result(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * 成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(data);
    }

    /**
     * 失败
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> error(Throwable data) {
        return new Result<>(data);
    }

    public boolean isSucceeded() {
        return throwable == null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
