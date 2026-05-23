package com.ixuea.superui.dialog

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.fragment.BaseDialogFragment
import com.ixuea.superui.util.SuperViewUtil
import com.google.android.material.R as MaterialR

/**
 * 对话框封装。
 */
class SuperDialog : BaseDialogFragment() {
    private var fragmentManager: FragmentManager? = null
    private var isShowCancelButton = true
    private var title: String? = null
    private var titleRes = 0
    private var message: String? = null
    private var messageRes = 0
    private var confirmButtonColorRes = 0
    private var confirmButtonTextRes = 0
    private var cancelButtonTextRes = 0
    private var onClickListener: View.OnClickListener? = null
    private var onCancelClickListener: View.OnClickListener? = null
    private var isShowInput = false

    private lateinit var titleView: TextView
    private lateinit var messageView: TextView
    private lateinit var cancelView: TextView
    private lateinit var confirmView: TextView
    private lateinit var inputView: EditText
    private lateinit var verticalDivider: View

    override fun initDatum() {
        super.initDatum()
        isCancelable = false

        titleView.text = title ?: titleRes.takeIf { it != 0 }?.let(::getString).orEmpty()

        val resolvedMessage = message ?: messageRes.takeIf { it != 0 }?.let(::getString)
        if (resolvedMessage.isNullOrBlank()) {
            messageView.visibility = View.GONE
        } else {
            messageView.visibility = View.VISIBLE
            messageView.text = resolvedMessage
        }

        SuperViewUtil.gone(inputView, !isShowInput)
        cancelView.visibility = if (isShowCancelButton) View.VISIBLE else View.GONE
        verticalDivider.visibility = if (isShowCancelButton) View.VISIBLE else View.GONE

        if (confirmButtonColorRes != 0) {
            confirmView.setTextColor(ContextCompat.getColor(requireContext(), confirmButtonColorRes))
        }

        if (confirmButtonTextRes != 0) {
            confirmView.setText(confirmButtonTextRes)
        }

        if (cancelButtonTextRes != 0) {
            cancelView.setText(cancelButtonTextRes)
        }
    }

    override fun initListeners() {
        super.initListeners()
        cancelView.setOnClickListener { view ->
            dismiss()
            onCancelClickListener?.onClick(view)
        }

        confirmView.setOnClickListener { view ->
            dismiss()
            onClickListener?.onClick(view)
        }
    }

    fun show(): SuperDialog {
        show(checkNotNull(fragmentManager) { "FragmentManager is required." }, "SuperDialog")
        return this
    }

    fun deleteStyle(): SuperDialog {
        confirmButtonColorRes = R.color.warning
        confirmButtonTextRes = R.string.delete
        return this
    }

    fun alertStyle(): SuperDialog {
        isShowCancelButton = false
        return this
    }

    fun titleInputConfirmStyle(): SuperDialog {
        isShowInput = true
        return this
    }

    fun setOnClickListener(onClickListener: View.OnClickListener): SuperDialog {
        this.onClickListener = onClickListener
        return this
    }

    fun setOnClickListener(onClickListener: View.OnClickListener, titleRes: Int): SuperDialog {
        this.onClickListener = onClickListener
        confirmButtonTextRes = titleRes
        return this
    }

    fun setOnCancelClickListener(onCancelClickListener: View.OnClickListener, titleRes: Int): SuperDialog {
        this.onCancelClickListener = onCancelClickListener
        cancelButtonTextRes = titleRes
        return this
    }

    fun setTitle(title: String): SuperDialog {
        this.title = title
        return this
    }

    fun setTitleRes(titleRes: Int): SuperDialog {
        this.titleRes = titleRes
        return this
    }

    fun setCancelButtonTextRes(data: Int): SuperDialog {
        cancelButtonTextRes = data
        return this
    }

    fun setMessage(message: String): SuperDialog {
        this.message = message
        return this
    }

    fun setMessageRes(messageRes: Int): SuperDialog {
        this.messageRes = messageRes
        return this
    }

    fun getInputView(): EditText {
        return inputView
    }

    override fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return buildDialogView()
    }

    private fun buildDialogView(): View {
        val context = requireContext()
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(resolveColorAttr(MaterialR.attr.colorSurface))
        }

        val contentContainer = LinearLayout(context).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            orientation = LinearLayout.VERTICAL
        }
        titleView = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(resolveColorAttr(MaterialR.attr.colorOnSurface))
            setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.s18))
            visibility = View.VISIBLE
        }
        messageView = TextView(context).apply {
            setTextColor(ContextCompat.getColor(context, R.color.black66))
            setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.s16))
            visibility = View.GONE
        }
        contentContainer.addView(
            titleView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ),
        )
        contentContainer.addView(
            messageView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                topMargin = dimen(R.dimen.d25)
            },
        )
        root.addView(
            contentContainer,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                leftMargin = dimen(R.dimen.padding_outer)
                rightMargin = dimen(R.dimen.padding_outer)
                topMargin = dimen(R.dimen.d40)
                bottomMargin = dimen(R.dimen.d40)
            },
        )

        inputView = EditText(context).apply {
            background = ContextCompat.getDrawable(context, R.drawable.selector_edit_text_border)
            hint = getString(R.string.please_enter)
            maxLines = 1
            setPadding(dimen(R.dimen.padding_meddle), 0, dimen(R.dimen.padding_meddle), 0)
            setTextColor(resolveColorAttr(MaterialR.attr.colorOnSurface))
            setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_meddle))
            visibility = View.GONE
        }
        root.addView(
            inputView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dimen(R.dimen.d50),
            ).apply {
                leftMargin = dimen(R.dimen.d20)
                rightMargin = dimen(R.dimen.d20)
                bottomMargin = dimen(R.dimen.d20)
            },
        )

        root.addView(createDivider(horizontal = true))

        val buttonContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        cancelView = createButtonTextView().apply {
            text = getString(R.string.cancel)
            setTextColor(resolveColorAttr(MaterialR.attr.colorOnSurface))
        }
        confirmView = createButtonTextView().apply {
            text = getString(R.string.confirm)
            setTextColor(resolveColorAttr(MaterialR.attr.colorPrimary))
        }
        verticalDivider = createDivider(horizontal = false)
        buttonContainer.addView(cancelView, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f))
        buttonContainer.addView(verticalDivider)
        buttonContainer.addView(confirmView, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f))
        root.addView(
            buttonContainer,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dimen(R.dimen.d50),
            ),
        )

        return root
    }

    private fun createButtonTextView(): TextView {
        return TextView(requireContext()).apply {
            background = ContextCompat.getDrawable(requireContext(), R.drawable.selector_surface)
            gravity = Gravity.CENTER
            isClickable = true
            setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.s17))
        }
    }

    private fun createDivider(horizontal: Boolean): View {
        return View(requireContext()).apply {
            setBackgroundColor(resolveColorAttr(R.attr.colorDivider))
            layoutParams = if (horizontal) {
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dimen(R.dimen.d0_5))
            } else {
                LinearLayout.LayoutParams(dimen(R.dimen.d0_5), ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }
    }

    private fun resolveColorAttr(@AttrRes attr: Int): Int {
        val typedValue = TypedValue()
        val resolved = requireContext().theme.resolveAttribute(attr, typedValue, true)
        if (!resolved) {
            return ContextCompat.getColor(requireContext(), R.color.white)
        }
        return if (typedValue.resourceId != 0) {
            ContextCompat.getColor(requireContext(), typedValue.resourceId)
        } else {
            typedValue.data
        }
    }

    private fun dimen(resId: Int): Int {
        return resources.getDimensionPixelSize(resId)
    }

    companion object {
        @JvmStatic
        fun newInstance(fragmentManager: FragmentManager): SuperDialog {
            return SuperDialog().apply {
                this.fragmentManager = fragmentManager
                arguments = Bundle()
            }
        }
    }
}
