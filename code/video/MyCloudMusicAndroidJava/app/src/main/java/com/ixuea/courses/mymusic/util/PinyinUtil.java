package com.ixuea.courses.mymusic.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 中文拼音相关工具方法
 */
public class PinyinUtil {
    /**
     * 全拼
     *
     * @param data
     * @return
     */
    public static String pinyin(String data) {
        return pinyin(data, false);
    }

    /**
     * 全拼
     * 除中文以外字符保存不变
     *
     * @param data
     * @param isFirst 是否是首字母
     * @return
     */
    public static String pinyin(String data, boolean isFirst) {
        //创建拼音配置
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

        //设置输出为小写
        //LOWERCASE:输出小写
        //UPPERCASE:输出大写
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);

        //如何显示音调
        //WITH_TONE_MARK:直接用音标符（必须设置WITH_U_UNICODE，否则会抛出异常）
        //WITH_TONE_NUMBER：1-4数字表示音标
        //WITHOUT_TONE：没有音标
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        //特殊音标ü设置
        //WITH_V：用v表示ü
        //WITH_U_AND_COLON：用"u:"表示ü
        //WITH_U_UNICODE：直接用ü
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        //将输入的值转为字符数组
        char[] input = data.trim().toCharArray();

        //创建一个字符串构建器
        StringBuilder sb = new StringBuilder();

        try {
            //遍历所有字符
            for (int i = 0; i < input.length; i++) {
                if (Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    //是汉字

                    //获取拼音
                    //如果是多音字
                    //会返回多个拼音
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);

                    //只取第一个
                    String pinyin = temp[0];

                    if (isFirst) {
                        //首字母
                        sb.append(pinyin.substring(0, 1));
                    } else {
                        //全拼
                        sb.append(pinyin);
                    }
                } else {
                    //普通字符

                    //直接添加
                    sb.append(input[i]);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }

        //返回结果
        return sb.toString();
    }

    /**
     * 首字母
     * 除中文以外字符保存不变
     *
     * @param data
     * @return
     */
    public static String pinyinFirst(String data) {
        return pinyin(data, true);
    }
}
