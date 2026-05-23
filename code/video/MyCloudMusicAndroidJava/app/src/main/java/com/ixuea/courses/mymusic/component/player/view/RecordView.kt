package com.ixuea.courses.mymusic.component.player.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.ixuea.courses.mymusic.R
import de.hdodenhof.circleimageview.CircleImageView
import com.ixuea.superui.util.DensityUtil

/**
 * 黑胶唱片view
 */
class RecordView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    @JvmField
    val iconView: ImageView = CircleImageView(context)

    private val contentView: ConstraintLayout = ConstraintLayout(context)

    /**
     * 旋转的角度
     */
    private var recordRotation = 0F

    init {
        initViews()
    }

    private fun initViews() {
        addView(
            contentView.apply {
                id = View.generateViewId()
                addRecordBackground()
                addCover()
            },
            LayoutParams(0, 0).apply {
                dimensionRatio = "H,1:1"
                matchConstraintPercentWidth = 0.731F
                leftToLeft = LayoutParams.PARENT_ID
                rightToRight = LayoutParams.PARENT_ID
                topToTop = LayoutParams.PARENT_ID
                topMargin = dp(89)
            },
        )
    }

    private fun ConstraintLayout.addRecordBackground() {
        addView(
            ImageView(context).apply {
                id = View.generateViewId()
                setImageResource(R.drawable.cd_background)
                scaleType = ImageView.ScaleType.FIT_CENTER
            },
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT),
        )
    }

    private fun ConstraintLayout.addCover() {
        addView(
            iconView.apply {
                id = View.generateViewId()
                setImageResource(R.drawable.placeholder)
                scaleType = ImageView.ScaleType.CENTER_CROP
            },
            LayoutParams(0, 0).apply {
                dimensionRatio = "H,1:1"
                matchConstraintPercentWidth = 0.64F
                leftToLeft = LayoutParams.PARENT_ID
                rightToRight = LayoutParams.PARENT_ID
                topToTop = LayoutParams.PARENT_ID
                bottomToBottom = LayoutParams.PARENT_ID
            },
        )
    }

    /**
     * 增量旋转
     */
    fun incrementRotate() {
        if (recordRotation >= 360F) {
            recordRotation = 0F
        }

        recordRotation += ROTATION_PER
        contentView.rotation = recordRotation
    }

    private fun dp(value: Int): Int {
        return DensityUtil.dip2px(context, value.toFloat()).toInt()
    }

    companion object {
        /**
         * 每16毫秒旋转的角度
         * 16毫秒是通过每秒60帧计算出来的，也就是1000/60=16
         */
        private const val ROTATION_PER = 0.2304F
    }
}
