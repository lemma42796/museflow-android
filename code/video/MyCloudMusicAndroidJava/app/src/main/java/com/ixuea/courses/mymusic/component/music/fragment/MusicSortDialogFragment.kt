package com.ixuea.courses.mymusic.component.music.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ixuea.courses.mymusic.R

/**
 * 歌曲排序对话框
 */
class MusicSortDialogFragment : DialogFragment() {
    private var onClickListener: DialogInterface.OnClickListener? = null
    private var sortIndex: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.sort)
            .setSingleChoiceItems(R.array.dialog_song_sort, sortIndex, onClickListener)
            .create()
    }

    companion object {
        @JvmStatic
        fun newInstance(): MusicSortDialogFragment {
            return MusicSortDialogFragment().apply {
                arguments = Bundle()
            }
        }

        @JvmStatic
        fun show(
            fragmentManager: FragmentManager,
            sortIndex: Int,
            onClickListener: DialogInterface.OnClickListener,
        ) {
            newInstance().apply {
                this.sortIndex = sortIndex
                this.onClickListener = onClickListener
            }.show(fragmentManager, "MusicSortDialogFragment")
        }
    }
}
