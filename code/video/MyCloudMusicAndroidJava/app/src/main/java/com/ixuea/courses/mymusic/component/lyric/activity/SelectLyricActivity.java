package com.ixuea.courses.mymusic.component.lyric.activity;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.lyric.adapter.SelectLyricAdapter;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.databinding.ActivitySelectLyricBinding;
import com.ixuea.courses.mymusic.util.ShareUtil;
import com.ixuea.superui.toast.SuperToast;
import com.ixuea.superui.util.SuperRecyclerViewUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * 选择歌词界面
 */
public class SelectLyricActivity extends BaseTitleActivity<ActivitySelectLyricBinding> {

    private SelectLyricAdapter adapter;
    private Song data;

    @Override
    protected void initViews() {
        super.initViews();
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list);

        //状态栏文字白色
        QMUIStatusBarHelper.setStatusBarDarkMode(this);

        setStatusBarColor(getColor(R.color.black42));
    }


    @Override
    protected void initDatum() {
        super.initDatum();
        data = extraData();

        adapter = new SelectLyricAdapter(R.layout.item_select_lyric);
        binding.list.setAdapter(adapter);

        adapter.setNewInstance(data.getParsedLyric().getDatum());
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> a, @NonNull View view, int position) {
                adapter.setSelected(position, !adapter.isSelected(position));
            }
        });

        //分享歌词按钮点击
        binding.shareLyric.setOnClickListener(v -> {
            //获取选中的歌词
            String lyricString = getSelectLyricString("，");

            if (TextUtils.isEmpty(lyricString)) {
                SuperToast.show(R.string.hint_select_lyric);
                return;
            }

            Timber.d("share text %s", lyricString);

            ShareUtil.shareLyricText(getHostActivity(), data, lyricString);
//            ShareUtil.shareLyricTextToQQ(getHostActivity(), data, lyricString);
        });

        //歌词图片按钮点击了
        binding.shareLyricImage.setOnClickListener(v -> {
            //获取选择的歌词
            String lyricString = getSelectLyricString("\n");

            if (TextUtils.isEmpty(lyricString)) {
                SuperToast.show(R.string.hint_select_lyric);
                return;
            }

            //跳转到歌词图片分享界面
            ShareLyricImageActivity.start(getHostActivity(), data, lyricString);
        });
    }

    /***
     * 获取选择的歌词文本
     *
     * @param separator
     * @return
     */
    private String getSelectLyricString(String separator) {
        //创建一个列表
        //用来装选择字符串
        ArrayList<String> lyrics = new ArrayList<>();

        //获取选中的索引
        int[] selectedIndexes = adapter.getSelectedIndexes();

        //遍历数组
        for (int i = 0; i < selectedIndexes.length; i++) {
            if (selectedIndexes[i] == 1) {
                //选中了

                //将当前这行歌词数据添加到列表
                lyrics.add(adapter.getItem(i).getData());
            }
        }

        //使用分隔符连接字符串
        return StringUtils.join(lyrics, separator);
    }
}