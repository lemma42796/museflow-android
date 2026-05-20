package com.ixuea.courses.mymusic.component.comment.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ixuea.courses.mymusic.R

/**
 * 评论更多对话框
 */
class CommentMoreDialogFragment : DialogFragment() {
    private var onClickListener: DialogInterface.OnClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
            .setItems(R.array.dialog_comment_more, onClickListener)
            .create()
    }

    companion object {
        @JvmStatic
        fun showDialog(
            fragmentManager: FragmentManager,
            onClickListener: DialogInterface.OnClickListener,
        ) {
            val fragment = newInstance()
            fragment.onClickListener = onClickListener
            fragment.show(fragmentManager, "CommentMoreDialogFragment")
        }

        @JvmStatic
        fun newInstance(): CommentMoreDialogFragment {
            return CommentMoreDialogFragment().apply {
                arguments = Bundle()
            }
        }
    }
}
