package com.ixuea.superui.loading

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.fragment.BaseDialogFragment
import com.ixuea.courses.mymusic.util.Constant

/**
 * 类似 iOS 的圆角加载对话框。
 */
class SuperRoundLoadingDialogFragment : BaseDialogFragment(), DialogInterface.OnKeyListener {
    private lateinit var messageView: TextView

    override fun initDatum() {
        super.initDatum()
        messageView.text = arguments?.getString(Constant.ID) ?: getString(R.string.loading)
    }

    override fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val dialog = requireDialog()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnKeyListener(this)

        val context = requireContext()
        val contentContainer = LinearLayout(context).apply {
            background = ContextCompat.getDrawable(context, R.drawable.shape_round_dialog_background)
            gravity = Gravity.CENTER
            minimumWidth = dimen(R.dimen.d120)
            orientation = LinearLayout.VERTICAL
            setPadding(dimen(R.dimen.d20), 15.dp(), dimen(R.dimen.d20), 15.dp())
        }
        val progress = ProgressBar(context).apply {
            isIndeterminate = true
            indeterminateDrawable = ContextCompat.getDrawable(context, R.drawable.anim_super_loading_icon)
        }
        messageView = TextView(context).apply {
            setTextColor(ContextCompat.getColor(context, R.color.white))
        }

        contentContainer.addView(
            progress,
            LinearLayout.LayoutParams(dimen(R.dimen.d40), dimen(R.dimen.d40)),
        )
        contentContainer.addView(
            messageView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                topMargin = dimen(R.dimen.padding_meddle)
            },
        )

        return LinearLayout(context).apply {
            addView(
                contentContainer,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    dimen(R.dimen.d120),
                ),
            )
        }
    }

    override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
        return keyCode == KeyEvent.KEYCODE_BACK
    }

    private fun dimen(resId: Int): Int {
        return resources.getDimensionPixelSize(resId)
    }

    private fun Int.dp(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    companion object {
        @JvmStatic
        fun newInstance(message: String): SuperRoundLoadingDialogFragment {
            return SuperRoundLoadingDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(Constant.ID, message)
                }
            }
        }

        @JvmStatic
        fun newInstance(): SuperRoundLoadingDialogFragment {
            return SuperRoundLoadingDialogFragment().apply {
                arguments = Bundle()
            }
        }
    }
}
