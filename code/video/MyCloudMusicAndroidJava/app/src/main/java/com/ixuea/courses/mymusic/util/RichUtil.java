package com.ixuea.courses.mymusic.util;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.annotation.NonNull;

import com.ixuea.courses.mymusic.R;
import com.ixuea.superui.text.SuperClickableSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 富文本工具类
 */
public class RichUtil {
    /**
     * mention开始
     */
    public static final String MENTION = "@";
    /**
     * hashTag开始
     */
    public static final String HAST_TAG = "#";
    /**
     * 匹配mention的正则表达式
     * 详细的请参考《详解正则表达式》课程
     */
    private static final String REG_MENTION = "(@[\\u4e00-\\u9fa5a-zA-Z0-9_-]{1,30})";
    /**
     * 匹配hashTag的正则表达式
     * ？表示禁用贪婪模式
     */
    private static final String REG_HASH_TAG = "(#.*?#)";

    /**
     * 处理文本添加点击事件
     *
     * @param context
     * @param data
     * @param onMentionClickListener
     * @param onHashTagClickListener
     * @return
     */
    public static SpannableString processContent(Context context,
                                                 String data,
                                                 OnTagClickListener onMentionClickListener,
                                                 OnTagClickListener onHashTagClickListener) {
        //创建结果字符串
        SpannableString result = new SpannableString(data);

        //查找@
        List<MatchResult> tags = findMentions(data);

        //遍历
        for (MatchResult matchResult : tags) {
            processInner(result, matchResult, onMentionClickListener);
        }

        //查找话题
        tags = findHash(data);
        for (MatchResult matchResult : tags) {
            processInner(result, matchResult, onHashTagClickListener);
        }

        //返回结果
        return result;
    }

    /**
     * 文本进行高亮
     * 不添加点击事件
     *
     * @param context
     * @param data
     * @return
     */
    public static SpannableString processHighlight(Context context, String data) {
        //查找@
        List<MatchResult> mentionsAndHashTags = findMentions(data);

        //匹配话题
        mentionsAndHashTags.addAll(findHash(data));

        //创建结果字符串
        //这个就是Android中的富文本字符串
        SpannableString result = new SpannableString(data);

        //遍历所有数据并处理
        for (MatchResult matchResult : mentionsAndHashTags) {
            //高亮文本
            ForegroundColorSpan span = new ForegroundColorSpan(context.getResources().getColor(R.color.text_highlight));

            //设置span
            //SPAN_EXCLUSIVE_EXCLUSIVE:不包括开始结束位置
            result.setSpan(span, matchResult.getStart(), matchResult.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //返回结果
        return result;
    }

    private static List<MatchResult> findHash(String data) {
        return find(REG_HASH_TAG, data);
    }

    private static void processInner(SpannableString result, MatchResult matchResult, OnTagClickListener tagClickListener) {
        result.setSpan(new SuperClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                tagClickListener.onTagClick(matchResult.getContent(), matchResult);
            }
        }, matchResult.getStart(), matchResult.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static List<MatchResult> findMentions(String data) {
        return find(REG_MENTION, data);
    }

    /**
     * 正则表达式查找
     *
     * @param reg  正则表达式
     * @param data 被查找的数据
     * @return
     */
    private static List<MatchResult> find(String reg, String data) {
        //创建结果列表
        List<MatchResult> results = new ArrayList<>();

        //编译正则表达式
        Pattern pattern = Pattern.compile(reg);

        //通过正则表达式匹配内容
        Matcher matcher = pattern.matcher(data);

        while (matcher.find()) {
            //将开始位置
            //结束位置
            //保存到一个类中
            MatchResult matchResult = new MatchResult(matcher.start(), matcher.end(), matcher.group(0).trim());
            results.add(matchResult);
        }

        //返回结果
        return results;
    }

    /**
     * 移除字符串中首的@
     * 移除首尾的#
     *
     * @param data
     * @return
     */
    public static String removePlaceholderString(String data) {
        if (data.startsWith(MENTION)) {
            //@人字符串

            //从1开始截取到末尾
            return data.substring(1);
        } else if (data.startsWith(HAST_TAG)) {
            //话题字符串

            //截取1~最后一个字符串
            return data.substring(1, data.length() - 1);
        }

        //如果不满足格式
        //就直接返回
        return data;
    }

    public static interface OnTagClickListener {
        /**
         * 点击回调方法
         *
         * @param data        点击的内容
         * @param matchResult 点击范围
         */
        void onTagClick(String data, MatchResult matchResult);
    }

    /**
     * 匹配的结果
     */
    public static class MatchResult {
        /**
         * 开始位置
         */
        private int start;

        /**
         * 结束位置
         */
        private int end;

        /**
         * 匹配到的内容
         */
        private String content;

        /**
         * 构造方法
         *
         * @param start
         * @param end
         * @param content
         */
        public MatchResult(int start, int end, String content) {
            this.start = start;
            this.end = end;
            this.content = content;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
