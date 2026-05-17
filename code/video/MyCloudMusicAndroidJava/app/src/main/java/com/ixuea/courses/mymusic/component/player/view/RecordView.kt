package com.ixuea.courses.mymusic.component.player.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.ixuea.courses.mymusic.databinding.RecordViewBinding

/**
 * 黑胶唱片view
 */
class RecordView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    @JvmField
    val binding: RecordViewBinding =
        RecordViewBinding.inflate(LayoutInflater.from(context), this, true)

    /**
     * 旋转的角度
     */
    private var recordRotation = 0F

    /**
     * 增量旋转
     */
    fun incrementRotate() {
        if (recordRotation >= 360F) {
            recordRotation = 0F
        }

        recordRotation += ROTATION_PER
        binding.content.rotation = recordRotation
    }

    companion object {
        /**
         * 每16毫秒旋转的角度
         * 16毫秒是通过每秒60帧计算出来的，也就是1000/60=16
         */
        private const val ROTATION_PER = 0.2304F
    }
}
