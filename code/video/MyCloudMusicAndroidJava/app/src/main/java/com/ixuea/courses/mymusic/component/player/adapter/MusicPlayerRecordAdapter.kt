package com.ixuea.courses.mymusic.component.player.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ixuea.courses.mymusic.adapter.BaseFragmentStateAdapter
import com.ixuea.courses.mymusic.component.player.fragment.RecordFragment
import com.ixuea.courses.mymusic.component.song.model.Song

/**
 * 黑胶唱片adapter
 */
class MusicPlayerRecordAdapter(
    fragmentActivity: FragmentActivity
) : BaseFragmentStateAdapter<Song>(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return RecordFragment.newInstance(getData(position))
    }
}
