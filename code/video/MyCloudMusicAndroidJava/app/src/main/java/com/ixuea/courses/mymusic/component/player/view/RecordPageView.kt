package com.ixuea.courses.mymusic.component.player.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.player.adapter.MusicPlayerRecordAdapter
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.superui.util.DensityUtil

/**
 * 黑胶唱片左右滚动页面view
 */
class RecordPageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ValueAnimator.AnimatorUpdateListener {

    @JvmField
    val list: ViewPager2 = ViewPager2(context)

    @JvmField
    var adapter: MusicPlayerRecordAdapter? = null

    private val recordThumb: ImageView = ImageView(context)

    private var isPlaying = true

    /**
     * 黑胶唱片指针播放状态动画
     */
    private lateinit var playThumbAnimator: ObjectAnimator

    /**
     * 黑胶唱片指针暂停状态动画
     */
    private lateinit var pauseThumbAnimator: ValueAnimator

    init {
        initViews()
        initDatum()
    }

    private fun initDatum() {
        setPlaying(false)
    }

    private fun initViews() {
        val rotate = DensityUtil.dip2px(context, 15F).toInt()
        addView(
            list.apply {
                id = View.generateViewId()
                overScrollMode = View.OVER_SCROLL_NEVER
            },
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                leftToLeft = LayoutParams.PARENT_ID
                rightToRight = LayoutParams.PARENT_ID
                topToTop = LayoutParams.PARENT_ID
                bottomToBottom = LayoutParams.PARENT_ID
            },
        )
        addView(
            recordThumb.apply {
                id = View.generateViewId()
                setImageResource(R.drawable.cd_thumb)
                pivotX = rotate.toFloat()
                pivotY = rotate.toFloat()
            },
            LayoutParams(dp(92), dp(138)).apply {
                leftToLeft = LayoutParams.PARENT_ID
                rightToRight = LayoutParams.PARENT_ID
                topToTop = LayoutParams.PARENT_ID
                leftMargin = dp(61)
                topMargin = resources.getDimensionPixelSize(R.dimen.padding_meddle)
            },
        )

        list.offscreenPageLimit = 3

        val child = list.getChildAt(0)
        if (child is RecyclerView) {
            child.overScrollMode = View.OVER_SCROLL_NEVER
        }

        playThumbAnimator = ObjectAnimator.ofFloat(
            recordThumb,
            "rotation",
            THUMB_ROTATION_PAUSE,
            THUMB_ROTATION_PLAY
        ).apply {
            duration = THUMB_DURATION
        }

        pauseThumbAnimator = ValueAnimator.ofFloat(
            THUMB_ROTATION_PLAY,
            THUMB_ROTATION_PAUSE
        ).apply {
            duration = THUMB_DURATION
            addUpdateListener(this@RecordPageView)
        }
    }

    fun initAdapter(fragmentActivity: FragmentActivity) {
        adapter = MusicPlayerRecordAdapter(fragmentActivity)
        list.adapter = adapter
    }

    fun setData(data: List<Song>?) {
        adapter?.setDatum(data)
    }

    /**
     * 选中当前音乐
     */
    fun scrollPosition(index: Int) {
        list.post {
            if (index != -1) {
                list.setCurrentItem(index, false)
            }
        }
    }

    /**
     * 设置是否在播放中状态
     */
    fun setPlaying(isPlaying: Boolean) {
        if (this.isPlaying == isPlaying) {
            return
        }

        this.isPlaying = isPlaying
        invalidatePlayingStatus()
    }

    private fun invalidatePlayingStatus() {
        if (isPlaying) {
            playThumbAnimator.start()
        } else {
            val thumbRotation = recordThumb.rotation
            if (THUMB_ROTATION_PAUSE == thumbRotation) {
                return
            }

            pauseThumbAnimator.start()
        }
    }

    /**
     * 属性动画回调
     */
    override fun onAnimationUpdate(animation: ValueAnimator) {
        recordThumb.rotation = animation.animatedValue as Float
    }

    override fun onDetachedFromWindow() {
        playThumbAnimator.cancel()
        pauseThumbAnimator.cancel()
        super.onDetachedFromWindow()
    }

    private fun dp(value: Int): Int {
        return DensityUtil.dip2px(context, value.toFloat()).toInt()
    }

    companion object {
        /**
         * 黑胶唱片指针暂停的角度
         */
        private const val THUMB_ROTATION_PAUSE = -25F

        /**
         * 黑胶唱片指针播放的角度
         */
        private const val THUMB_ROTATION_PLAY = 0F

        /**
         * 黑胶唱片指针动画时间
         */
        private const val THUMB_DURATION = 300L
    }
}
