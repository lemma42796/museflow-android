package com.ixuea.courses.mymusic.component.lyric.parser;

import static com.ixuea.courses.mymusic.util.Constant.KSC;
import static com.ixuea.courses.mymusic.util.Constant.LRC;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.ixuea.courses.mymusic.component.lyric.model.Lyric;

import org.junit.Test;

/**
 * 测试歌词解析
 */
public class LyricParserTest {
    /**
     * LRC歌词
     * 其中的\n不要删除
     * 是每一行歌词的分隔符
     */
    private String lrcLyric = "[ti:爱的代价]\n[ar:李宗盛]\n[al:爱的代价]\n[00:00.50]爱的代价\n[00:02.50]演唱：李宗盛\n[00:06.50]\n[00:16.70]还记得年少时的梦吗\n[00:21.43]像朵永远不调零的花\n[00:25.23]陪我经过那风吹雨打\n[00:28.59]看世事无常\n[00:30.57]看沧桑变化\n[00:33.31]那些为爱所付出的代价\n[00:37.10]是永远都难忘的啊\n[00:41.10]所有真心的痴心的话\n[00:44.57]永在我心中虽然已没有他\n[00:51.46]走吧 走吧 人总要学着自己长大\n[00:59.53]走吧 走吧 人生难免经历苦痛挣扎\n[01:07.19]走吧 走吧 为自己的心找一个家\n[01:15.41]也曾伤心流泪\n[01:17.55]也曾黯然心碎\n[01:19.57]这是爱的代价\n[01:22.57]\n[01:40.67]也许我偶尔还是会想他\n[01:45.28]偶尔难免会惦记着他\n[01:49.10]就当他是个老朋友吧\n[01:52.60]也让我心疼也让我牵挂\n[01:57.37]只是我心中不再有火花\n[02:01.21]让往事都随风去吧\n[02:05.10]所有真心的痴心的话\n[02:08.53]仍在我心中\n[02:10.39]虽然已没有他\n[02:15.26]走吧 走吧 人总要学着自己长大\n[02:23.14]走吧 走吧 人生难免经历苦痛挣扎\n[02:31.26]走吧 走吧 为自己的心找一个家\n[02:39.22]也曾伤心流泪\n[02:41.54]也曾黯然心碎\n[02:43.60]这是爱的代价\n[02:46.44]\n[03:22.77]走吧 走吧 人总要学着自己长大\n[03:31.16]走吧 走吧 人生难免经历苦痛挣扎\n[03:39.08]走吧 走吧 为自己的心找一个家\n[03:47.12]也曾伤心流泪\n[03:49.41]也曾黯然心碎\n[03:51.58]这是爱的代价\n";

    /**
     * KSC歌词
     */
//    private String kscLyric = "karaoke := CreateKaraokeObject;\nkaraoke.rows := 2;\nkaraoke.TimeAfterAnimate := 2000;\nkaraoke.TimeBeforeAnimate := 4000;\nkaraoke.clear;\n\nkaraoke.add('00:27.487', '00:32.068', '一时失志不免怨叹', '347,373,1077,320,344,386,638,1096');\nkaraoke.add('00:33.221', '00:38.068', '一时落魄不免胆寒', '282,362,1118,296,317,395,718,1359');\nkaraoke.add('00:38.914', '00:42.164', '那通失去希望', '290,373,348,403,689,1147');\nkaraoke.add('00:42.485', '00:44.530', '每日醉茫茫', '298,346,366,352,683');\nkaraoke.add('00:45.273', '00:49.029', '无魂有体亲像稻草人', '317,364,380,351,326,351,356,389,922');\nkaraoke.add('00:50.281', '00:55.585', '人生可比是海上的波浪', '628,1081,376,326,406,371,375,1045,378,318');\nkaraoke.add('00:56.007', '01:00.934', '有时起有时落', '303,362,1416,658,750,1438');\nkaraoke.add('01:02.020', '01:04.581', '好运歹运', '360,1081,360,760');\nkaraoke.add('01:05.283', '01:09.453', '总嘛要照起来行', '303,338,354,373,710,706,1386');\nkaraoke.add('01:10.979', '01:13.029', '三分天注定', '304,365,353,338,690');\nkaraoke.add('01:13.790', '01:15.950', '七分靠打拼', '356,337,338,421,708');\nkaraoke.add('01:16.339', '01:20.870', '爱拼才会赢', '325,1407,709,660,1430');\nkaraoke.add('01:33.068', '01:37.580', '一时失志不免怨叹', '307,384,1021,363,357,374,677,1029');\nkaraoke.add('01:38.660', '01:43.656', '一时落魄不免胆寒', '381,411,1067,344,375,381,648,1389');\nkaraoke.add('01:44.473', '01:47.471', '那通失去希望', '315,365,340,369,684,925');\nkaraoke.add('01:48.000', '01:50.128', '每日醉茫茫', '338,361,370,370,689');\nkaraoke.add('01:50.862', '01:54.593', '无魂有体亲像稻草人', '330,359,368,376,325,334,352,389,898');\nkaraoke.add('01:55.830', '02:01.185', '人生可比是海上的波浪', '654,1056,416,318,385,416,373,1032,342,363');\nkaraoke.add('02:01.604', '02:06.716', '有时起有时落', '303,330,1432,649,704,1694');\nkaraoke.add('02:07.624', '02:10.165', '好运歹运', '329,1090,369,753');\nkaraoke.add('02:10.829', '02:15.121', '总嘛要照起来行', '313,355,362,389,705,683,1485');\nkaraoke.add('02:16.609', '02:18.621', '三分天注定', '296,363,306,389,658');\nkaraoke.add('02:19.426', '02:21.428', '七分靠打拼', '330,359,336,389,588');\nkaraoke.add('02:21.957', '02:26.457', '爱拼才会赢', '315,1364,664,767,1390');\nkaraoke.add('02:50.072', '02:55.341', '人生可比是海上的波浪', '656,1086,349,326,359,356,364,1095,338,340');\nkaraoke.add('02:55.774', '03:01.248', '有时起有时落', '312,357,1400,670,729,2006');\nkaraoke.add('03:01.787', '03:04.369', '好运歹运', '341,1084,376,781');\nkaraoke.add('03:05.041', '03:09.865', '总嘛要起工来行', '305,332,331,406,751,615,2084');\nkaraoke.add('03:10.754', '03:12.813', '三分天注定', '309,359,361,366,664');\nkaraoke.add('03:13.571', '03:15.596', '七分靠打拼', '320,362,349,352,642');\nkaraoke.add('03:16.106', '03:20.688', '爱拼才会赢', '304,1421,661,706,1490');";
    private String kscLyric = "karaoke := CreateKaraokeObject; karaoke.rows := 2; karaoke.clear; karaoke.songname := 'SAY YOU SAY ME'; // 歌名 karaoke.internalnumber := 231; // 歌曲编号 karaoke.singer := 'Lionel Richie'; // 演唱者，对唱则用分号分隔 karaoke.wordcount := 4; // 歌名字数 karaoke.pinyin := ' '; // 歌名的拼音声母 karaoke.langclass := '英语,外语'; // 歌曲语言种类 karaoke.songclass := '男'; // 歌类，如男女乐队等 karaoke.songstyle := '流行'; // 歌曲风格 karaoke.videofilename :='风景'; karaoke.add('00:16.857', '00:19.707', '[Say ][you ][Say ][me]', '410,1456,450,531'); karaoke.add('00:22.569', '00:24.507', '[Say ][it ][for ][always]', '278,205,258,1194'); karaoke.add('00:26.734', '00:27.340', '[That's ][the ][way]', '250,196,156'); karaoke.add('00:27.521', '00:28.664', '[it ][should ][be]', '196,255,686'); karaoke.add('00:32.317', '00:35.263', '[Say ][you ][Say ][me]', '363,1519,380,679'); karaoke.add('00:38.099', '00:39.650', '[Say ][it ][together]', '269,245,1032'); karaoke.add('00:41.926', '00:43.392', '[naturally]', '1460'); karaoke.add('00:48.153', '00:49.234', '[I ][had ][a ][dream]', '185,200,191,500'); karaoke.add('00:50.127', '00:50.647', '[I ][had ][an]', '175,175,166'); karaoke.add('00:50.876', '00:52.270', '[awesome ][dream ]', '383,1007'); karaoke.add('00:54.588', '00:56.646', '[People ][in ][the ][park]', '889,340,242,583'); karaoke.add('00:58.021', '00:58.721', '[Playing ][games]', '376,319'); karaoke.add('00:59.079', '01:00.592', '[in ][the ][park]', '412,408,688'); karaoke.add('01:03.661', '01:04.755', '[And ][what ][they ][played]', '216,228,247,399'); karaoke.add('01:05.568', '01:07.960', '[was ][a ][masquerade]', '342,247,1798'); karaoke.add('01:09.707', '01:10.869', '[From ][behind ][the]', '272,626,260'); karaoke.add('01:11.068', '01:12.300', '[walls ][of ][doubt]', '318,300,609'); karaoke.add('01:13.721', '01:14.651', '[A ][voice ][was]', '312,407,207'); karaoke.add('01:14.854', '01:16.347', '[crying ][out]', '441,1047'); karaoke.add('01:22.657', '01:25.599', '[Say ][you ][Say ][me]', '380,1530,437,591'); karaoke.add('01:28.465', '01:30.371', '[Say ][it ][for ][always]', '222,199,180,1300'); karaoke.add('01:32.714', '01:33.279', '[That's ][the ][way]', '191,175,194'); karaoke.add('01:33.489', '01:34.731', '[it ][should ][be]', '245,325,667'); karaoke.add('01:38.243', '01:41.068', '[Say ][you ][say ][me]', '304,1530,383,604'); karaoke.add('01:43.977', '01:45.775', '[Say ][it ][together]', '305,295,1193'); karaoke.add('01:47.833', '01:49.130', '[naturally]', '1292'); karaoke.add('01:54.005', '01:55.441', '[As ][we ][go ][down]', '206,284,278,662'); karaoke.add('01:56.017', '01:58.700', '[life's ][lonesome ][highway]', '688,775,1215'); karaoke.add('01:59.945', '02:01.063', '[Seems ][the ][hardest]', '303,199,612'); karaoke.add('02:01.285', '02:02.673', '[thing ][to ][do]', '352,245,786'); karaoke.add('02:03.905', '02:05.030', '[Is ][to ][find ][a ][friend]', '210,191,263,230,225'); karaoke.add('02:05.236', '02:06.394', '[or ][two]', '332,822'); karaoke.add('02:09.572', '02:10.844', '[That ][helping ][hand]', '256,676,336'); karaoke.add('02:11.430', '02:14.019', '[someone ][who ][understands]', '481,380,1723'); karaoke.add('02:15.519', '02:16.194', '[When ][you ][feel]', '209,263,199'); karaoke.add('02:16.423', '02:18.152', '[you've ][lost ][your ][way]', '292,403,345,685'); karaoke.add('02:19.182', '02:20.493', '[You've ][got ][some ][one]', '315,344,334,313'); karaoke.add('02:20.674', '02:21.984', '[there ][to ][say]', '392,228,685'); karaoke.add('02:23.085', '02:25.456', '[I'll ][show ][you]', '314,421,1632'); karaoke.add('02:28.569', '02:31.356', '[Say ][you ][say ][me]', '318,1529,402,533'); karaoke.add('02:34.341', '02:36.112', '[Say ][it ][for ][always]', '237,267,258,1005'); karaoke.add('02:38.693', '02:39.333', '[That's ][the ][way]', '191,247,197'); karaoke.add('02:39.587', '02:40.777', '[it ][should ][be]', '231,202,752'); karaoke.add('02:44.050', '02:46.977', '[Say ][you ][Say ][me]', '377,1471,445,629'); karaoke.add('02:49.834', '02:51.376', '[Say ][it ][together]', '214,262,1063'); karaoke.add('02:53.694', '02:54.900', '[naturally]', '1202'); karaoke.add('02:57.040', '02:57.769', '[So ][you ][think ][you]', '154,178,206,186'); karaoke.add('02:57.975', '02:58.944', '[know ][the ][answer]', '229,218,518'); karaoke.add('02:59.787', '03:01.304', '[oh ][no]', '855,658'); karaoke.add('03:01.970', '03:02.718', '[Well ][the ][whole ][worlds]', '221,177,164,183'); karaoke.add('03:02.930', '03:03.509', '[got'yya ][dancin']', '215,359'); karaoke.add('03:03.855', '03:04.368', '[That's ][right]', '264,245'); karaoke.add('03:04.576', '03:05.832', '[I'm ][telling ][you]', '220,447,583'); karaoke.add('03:07.374', '03:09.031', '[time ][to ][start ][believin']', '220,293,284,855'); karaoke.add('03:09.891', '03:11.373', '[Oh ][yes]', '885,595'); karaoke.add('03:12.400', '03:14.652', '[Believe ][in ][who ][you ][are]', '478,340,316,347,765'); karaoke.add('03:16.048', '03:18.820', '[You ][are ][a ][shining ][star]', '279,364,283,579,1262'); karaoke.add('03:23.961', '03:26.828', '[Say ][you ][say ][me]', '478,1445,394,545'); karaoke.add('03:29.736', '03:31.316', '[Say ][it ][for ][always]', '293,305,279,699'); karaoke.add('03:32.917', '03:34.827', '[Oh ][that's ][the ][way]', '1156,238,290,222'); karaoke.add('03:35.077', '03:36.096', '[it ][should ][be]', '239,231,536'); karaoke.add('03:39.505', '03:42.516', '[Say ][you ][say ][me]', '395,1535,402,676'); karaoke.add('03:45.277', '03:46.338', '[Say ][it ][together]', '285,301,471'); karaoke.add('03:49.227', '03:50.465', '[naturally]', '1233'); karaoke.add('03:53.083', '03:56.297', '[Say ][it ][together]', '292,268,2649'); karaoke.add('03:57.595', '04:01.109', '[naturally]', '3510');";

    /**
     * 测试LRC歌词解析
     */
    @Test
    public void testLRCParse() {
        //解析歌词
        Lyric lyric = LyricParser.parse(LRC, lrcLyric);

        //确认返回的数组大于0
        //因为我们给的数据是正确的
        //所以结果肯定大于0才正确
        assertTrue(lyric.getDatum().size() > 0);

        //不太好判断歌词是否解析正确
        //所以就判断第10行歌词的开始时间必须大于0
        //第10行歌词必须有内容
        //因为我们提供的歌词是正确的
        assertTrue(lyric.getDatum().get(10).getStartTime() > 0);

        //歌词内容也不为空
        assertNotNull(lyric.getDatum().get(10).getData());
    }

    /**
     * 测试KSC歌词解析
     */
    @Test
    public void testKSCParse() {
        //解析歌词
        Lyric lyric = LyricParser.parse(KSC, kscLyric);

        //确认返回的数组大于0
        //因为我们给的数据是正确的
        //所以结果肯定大于0才正确
        assertTrue(lyric.getDatum().size() > 0);

        //不太好判断歌词是否解析正确
        //所以就判断第10行歌词的开始时间必须大于0
        //第10行歌词必须有内容
        //因为我们提供的歌词是正确的
        assertTrue(lyric.getDatum().get(10).getStartTime() > 0);

        //必须有内容
        assertNotNull(lyric.getDatum().get(10).getData());

        //歌词字数
        //每个字时间数
        //都要大于0
        assertTrue(lyric.getDatum().get(10).getWords().length > 0);

        assertTrue(lyric.getDatum().get(10).getWordDurations().length > 0);

        //歌词字数和每个字时间数相等
        assertTrue(lyric.getDatum().get(10).getWords().length == lyric.getDatum().get(10).getWordDurations().length);
    }
}
