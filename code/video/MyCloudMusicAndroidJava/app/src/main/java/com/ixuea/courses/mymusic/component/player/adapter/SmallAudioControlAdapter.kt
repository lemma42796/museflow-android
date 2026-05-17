package com.ixuea.courses.mymusic.component.player.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ixuea.courses.mymusic.adapter.BaseFragmentStatePagerAdapter
import com.ixuea.courses.mymusic.component.player.fragment.SmallAudioControlFragment
import com.ixuea.courses.mymusic.component.song.model.Song

/**
 * 小音乐播放控制器ViewPager的Adapter
 */
class SmallAudioControlAdapter(
    context: Context,
    fm: FragmentManager
) : BaseFragmentStatePagerAdapter<Song>(context, fm) {

    override fun getItem(position: Int): Fragment {
        return SmallAudioControlFragment.newInstance(getData(position))
    }

    override fun getItemPosition(`object`: Any): Int {
        return FragmentStatePagerAdapter.POSITION_NONE
    }
}
