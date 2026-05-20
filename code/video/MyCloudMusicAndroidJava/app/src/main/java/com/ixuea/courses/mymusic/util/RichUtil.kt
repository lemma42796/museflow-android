package com.ixuea.courses.mymusic.util

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import com.ixuea.courses.mymusic.R
import com.ixuea.superui.text.SuperClickableSpan
import java.util.regex.Pattern

/**
 * 富文本工具类
 */
object RichUtil {
    /**
     * mention开始
     */
    const val MENTION = "@"

    /**
     * hashTag开始
     */
    const val HAST_TAG = "#"

    /**
     * 匹配mention的正则表达式
     * 详细的请参考《详解正则表达式》课程
     */
    private const val REG_MENTION = "(@[\\u4e00-\\u9fa5a-zA-Z0-9_-]{1,30})"

    /**
     * 匹配hashTag的正则表达式
     * ？表示禁用贪婪模式
     */
    private const val REG_HASH_TAG = "(#.*?#)"

    /**
     * 处理文本添加点击事件
     */
    @JvmStatic
    fun processContent(
        context: Context,
        data: String,
        onMentionClickListener: OnTagClickListener,
        onHashTagClickListener: OnTagClickListener,
    ): SpannableString {
        val result = SpannableString(data)

        var tags = findMentions(data)
        for (matchResult in tags) {
            processInner(result, matchResult, onMentionClickListener)
        }

        tags = findHash(data)
        for (matchResult in tags) {
            processInner(result, matchResult, onHashTagClickListener)
        }

        return result
    }

    /**
     * 文本进行高亮
     * 不添加点击事件
     */
    @JvmStatic
    fun processHighlight(context: Context, data: String): SpannableString {
        val mentionsAndHashTags = findMentions(data)
        mentionsAndHashTags.addAll(findHash(data))

        val result = SpannableString(data)
        for (matchResult in mentionsAndHashTags) {
            val span = ForegroundColorSpan(context.resources.getColor(R.color.text_highlight))
            result.setSpan(
                span,
                matchResult.start,
                matchResult.end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return result
    }

    private fun findHash(data: String): MutableList<MatchResult> {
        return find(REG_HASH_TAG, data)
    }

    private fun processInner(
        result: SpannableString,
        matchResult: MatchResult,
        tagClickListener: OnTagClickListener,
    ) {
        result.setSpan(
            object : SuperClickableSpan() {
                override fun onClick(widget: View) {
                    tagClickListener.onTagClick(matchResult.content, matchResult)
                }
            },
            matchResult.start,
            matchResult.end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun findMentions(data: String): MutableList<MatchResult> {
        return find(REG_MENTION, data)
    }

    /**
     * 正则表达式查找
     *
     * @param reg 正则表达式
     * @param data 被查找的数据
     */
    private fun find(reg: String, data: String): MutableList<MatchResult> {
        val results = ArrayList<MatchResult>()
        val pattern = Pattern.compile(reg)
        val matcher = pattern.matcher(data)

        while (matcher.find()) {
            val matchResult = MatchResult(matcher.start(), matcher.end(), matcher.group(0)!!.trim())
            results.add(matchResult)
        }

        return results
    }

    /**
     * 移除字符串中首的@
     * 移除首尾的#
     */
    @JvmStatic
    fun removePlaceholderString(data: String): String {
        return if (data.startsWith(MENTION)) {
            data.substring(1)
        } else if (data.startsWith(HAST_TAG)) {
            data.substring(1, data.length - 1)
        } else {
            data
        }
    }

    fun interface OnTagClickListener {
        /**
         * 点击回调方法
         *
         * @param data 点击的内容
         * @param matchResult 点击范围
         */
        fun onTagClick(data: String, matchResult: MatchResult)
    }

    /**
     * 匹配的结果
     */
    class MatchResult(
        /**
         * 开始位置
         */
        var start: Int,
        /**
         * 结束位置
         */
        var end: Int,
        /**
         * 匹配到的内容
         */
        var content: String,
    )
}
