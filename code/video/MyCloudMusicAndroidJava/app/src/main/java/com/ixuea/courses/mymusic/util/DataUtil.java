package com.ixuea.courses.mymusic.util;

import com.google.common.collect.Ordering;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.component.user.model.User;
import com.ixuea.courses.mymusic.component.user.model.ui.TitleData;
import com.ixuea.courses.mymusic.component.user.model.ui.UserResult;
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据处理工具类
 */
public class DataUtil {
    /**
     * 根据用户首字母分组
     * <p>
     * 标题
     * 真实数据
     * 标题
     * 真实数据
     *
     * @param datum
     * @return
     */
    public static UserResult processUser(List<User> datum) {
        //创建结果数组
        List<BaseMultiItemEntity> results = new ArrayList<>();

        //创建字母列表
        ArrayList<String> letters = new ArrayList<>();

        //字母索引列表
        ArrayList<Integer> indexes = new ArrayList<>();

        //按照第一个字母排序
        //这里使用的Guava提供的排序方法
        //也可以使用Java的排序方法
        Ordering<User> byFirst = new Ordering<User>() {
            @Override
            public int compare(User left, User right) {
                //根据第一个字母排序
                return left.getFirst().compareTo(right.getFirst());
            }
        };

        //排序
        datum = byFirst.immutableSortedCopy(datum);

        //按照拼音首字母的第一个字母分组
        //这些操作都可以使用响应式编程方法
        //这里为了简单使用了最普通的方法
        //因为只要明白了原理
        //使用其他方法就是语法不同而已

        //循环所有数据

        //上一次用户
        User lastUser = null;
        for (User data : datum) {
            if (lastUser != null && lastUser.getFirst().equals(data.getFirst())) {
                //相等
            } else {
                //添加标题

                String letter = data.getFirst().toUpperCase();

                //添加字母索引
                indexes.add(results.size());

                //添加标题
                results.add(new TitleData(letter));

                //添加字母
                letters.add(letter);
            }

            //添加用
            results.add(data);

            lastUser = data;
        }

        //字母索引转为数组
        Integer[] indexArray = indexes.toArray(new Integer[indexes.size()]);

        return new UserResult(results, letters, indexArray);
    }

    /**
     * 更高是否在播放列表字段
     *
     * @param datum
     * @param value
     */
    public static void changePlayListFlag(List<Song> datum, boolean value) {
        for (Song data : datum
        ) {
            data.setPlayList(value);
        }
    }

    /**
     * 返回用户测试数据
     *
     * @return
     */
    public static List<User> getTestUserData() {
        //创建一个列表
        ArrayList<User> results = new ArrayList<>();

        //全中文
        User user = null;
        for (int i = 0; i < 50; i++) {
            user = new User();
            user.setNickname("我的云音乐" + i);
            results.add(user);
        }

        //有单词
        for (int i = 0; i < 50; i++) {
            user = new User();
            user.setNickname("爱学啊smile" + i);
            results.add(user);
        }

        //全中文
        for (int i = 0; i < 50; i++) {
            user = new User();
            user.setNickname("爱学啊李薇" + i);
            results.add(user);
        }

        //全英文
        for (int i = 0; i < 50; i++) {
            user = new User();
            user.setNickname("Jack li" + i);
            results.add(user);
        }

        return results;
    }

    /**
     * 根据用户昵称计算出拼音
     *
     * @param datum
     * @return
     */
    public static List<User> processUserPinyin(List<User> datum) {
        //循环所有数据
        for (User data : datum) {
            //获取全拼
            data.setPinyin(PinyinUtil.pinyin(data.getNickname()).toLowerCase());

            //获取拼音首字母
            //例如："爱学啊"
            //结果为：axa
            data.setPinyinFirst(PinyinUtil.pinyinFirst(data.getNickname()).toLowerCase());

            //拼音首字母的首字母
            //例如："爱学啊"
            //结果为：a
            data.setFirst(data.getPinyinFirst().substring(0, 1));
        }

        return datum;
    }

    /**
     * 过滤用户数据
     *
     * @param datum
     * @param query
     */
    public static List<User> filterUser(List<User> datum, String query) {
        //创建列表
        ArrayList<User> results = new ArrayList<>();

        //遍历所有数据
        for (User data : datum
        ) {
            if (isMatchUser(data, query)) {
                //如果匹配

                //就添加到列表
                results.add(data);
            }
        }

        //返回结果
        return results;

    }

    /**
     * 用户是否匹配给定的关键字
     *
     * @param data
     * @param query
     * @return
     */
    private static boolean isMatchUser(User data, String query) {
        //nickname是否包含搜索的字符串
        //全拼是否包含
        //首字母是否包含

        //如果需要更多的条件可以在加
        //条件越多
        //就更容易搜索到
        //但结果就越多
        return data.getNickname().toLowerCase().contains(query) ||
                data.getPinyin().contains(query) ||
                data.getPinyinFirst().contains(query);
    }
}
