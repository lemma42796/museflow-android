package com.ixuea.courses.mymusic.util;

import java.util.List;

/**
 * 列表工具类
 */
public class ListUtil {
    /**
     * 变量每一个接口
     *
     * @param datum
     * @param action
     * @param <T>
     */
    public static <T> void eachListener(List<T> datum, Consumer<T> action) {
        for (T listener : datum
        ) {
            //将列表中每一个对象传递给action
            action.accept(listener);
        }
    }

    /**
     * 消费者接口
     * <p>
     * 名字可以随便取
     * 只是Java中是这样命名的
     * 但他的类只有在API为24才能使用
     * 所以我们就自定义一个接口
     *
     * @param <T>
     */
    public interface Consumer<T> {
        /**
         * 方法名也可随便定义
         *
         * @param t
         */
        void accept(T t);
    }
}
