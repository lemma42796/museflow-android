package com.ixuea.courses.mymusic.component.lyric.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.annotation.DimenRes
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.util.LyricUtil
import com.ixuea.courses.mymusic.util.PreferenceUtil
import me.shihao.library.XRadioGroup
import kotlin.math.abs

/**
 * 全局（桌面）歌词。
 */
class GlobalLyricView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes),
    XRadioGroup.OnCheckedChangeListener {

    private val logo = ImageView(context)
    private val lyricLine1 = LyricLineView(context)
    private val lyricLine2 = LyricLineView(context)
    private val close = ImageView(context)
    private val playContainer = LinearLayout(context)
    private val lock = ImageView(context)
    private val previous = ImageView(context)
    private val play = ImageView(context)
    private val next = ImageView(context)
    private val settings = ImageView(context)
    private val lyricEditContainer = LinearLayout(context)
    private val radioGroup = XRadioGroup(context)
    private val fontSizeSmall = ImageView(context)
    private val fontSizeLarge = ImageView(context)
    private val radioButtonIds = IntArray(LYRIC_COLORS.size) { View.generateViewId() }
    private lateinit var sp: PreferenceUtil
    private var lyricDragListener: OnGlobalLyricDragListener? = null
    private var globalLyricListener: GlobalLyricListener? = null
    private var globalLyricOtherListener: GlobalLyricOtherListener? = null
    private var isIntercept: Boolean = false
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var touchSlop: Float = 0f

    init {
        buildLayout()
        initViews()
        initDatum()
        initListeners()
    }

    private fun initViews() {
        setGlobalBackground()
        lyricLine1.setLineSelected(true)
    }

    private fun initDatum() {
        sp = PreferenceUtil.getInstance(context)
        val lyricTextColorIndex = sp.globalLyricTextColorIndex.coerceIn(radioButtonIds.indices)
        updateLyricTextColor(lyricTextColorIndex)

        val radioButtonId = radioButtonIds[lyricTextColorIndex]
        radioGroup.check(radioButtonId)

        val lyricTextSize = sp.globalLyricTextSize
        lyricLine1.setLyricTextSize(lyricTextSize)
        lyricLine2.setLyricTextSize(lyricTextSize)

        lyricLine1.setLyricTextColor(context.getColor(R.color.black165))
        lyricLine2.setLyricTextColor(context.getColor(R.color.black165))

        touchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
    }

    private fun initListeners() {
        val thisClickListener = OnClickListener {
            if (logo.visibility == View.VISIBLE) {
                simpleStyle()
            } else {
                normalStyle()
            }
        }
        setOnClickListener(thisClickListener)

        logo.setOnClickListener {
            globalLyricListener?.onLogoClick()
        }

        close.setOnClickListener {
            globalLyricOtherListener?.closeLyric()
        }

        lock.setOnClickListener {
            globalLyricListener?.onLockClick()
        }

        previous.setOnClickListener {
            globalLyricListener?.onPreviousClick()
        }

        play.setOnClickListener {
            globalLyricListener?.onPlayClick()
        }

        next.setOnClickListener {
            globalLyricListener?.onNextClick()
        }

        settings.setOnClickListener {
            lyricEditContainer.visibility =
                if (lyricEditContainer.visibility == View.VISIBLE) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
        }

        radioGroup.setOnCheckedChangeListener(this)

        fontSizeSmall.setOnClickListener {
            val currentSize = lyricLine1.decrementTextSize()
            setLyricTextSize(currentSize)
            sp.globalLyricTextSize = currentSize
        }

        fontSizeLarge.setOnClickListener {
            val currentSize = lyricLine1.incrementTextSize()
            setLyricTextSize(currentSize)
            sp.globalLyricTextSize = currentSize
        }
    }

    private fun setLyricTextSize(currentSize: Int) {
        lyricLine2.setLyricTextSize(currentSize)
    }

    private fun setGlobalBackground() {
        setBackgroundColor(context.getColor(R.color.global_lyric_background))
    }

    /**
     * 标准样式，控件都显示。
     */
    fun normalStyle() {
        setGlobalBackground()
        logo.visibility = View.VISIBLE
        close.visibility = View.VISIBLE
        playContainer.visibility = View.VISIBLE
    }

    /**
     * 简单样式，只显示歌词。
     */
    fun simpleStyle() {
        setBackgroundColor(context.getColor(R.color.transparent))
        logo.visibility = View.GONE
        close.visibility = View.GONE
        playContainer.visibility = View.GONE
        lyricEditContainer.visibility = View.GONE
    }

    fun clearLyric() {
        lyricLine1.setData(null)
        lyricLine2.setData(null)
    }

    fun setPlay(playing: Boolean) {
        play.setImageResource(if (playing) R.drawable.music_pause else R.drawable.music_play)
    }

    fun setAccurate(accurate: Boolean) {
        lyricLine1.setAccurate(accurate)
    }

    /**
     * 音乐进度回调。
     */
    fun onProgress(data: Song) {
        val lyric = data.parsedLyric
        if (lyric == null) {
            clearLyric()
            return
        }

        val progress = data.progress
        val line = LyricUtil.getLyricLine(lyric, progress)
        lyricLine1.setData(line)

        if (lyric.isAccurate && line != null) {
            val lyricCurrentWordIndex = LyricUtil.getWordIndex(line, progress)
            val wordPlayedTime = LyricUtil.getWordPlayedTime(line, progress)
            lyricLine1.setLyricCurrentWordIndex(lyricCurrentWordIndex)
            lyricLine1.setWordPlayedTime(wordPlayedTime)
            lyricLine1.onProgress()
        }

        val datum = lyric.datum.orEmpty()
        val lineNumber = LyricUtil.getLineNumber(lyric, progress.toInt())
        if (lineNumber < datum.size - 1) {
            lyricLine2.setData(datum[lineNumber + 1])
        } else {
            lyricLine2.setData(null)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        isIntercept = false
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isIntercept = false
                lastX = ev.x
                lastY = ev.y
            }

            MotionEvent.ACTION_MOVE -> {
                if (abs(ev.y - lastY) > touchSlop) {
                    isIntercept = true
                }
            }

            MotionEvent.ACTION_UP -> {
                isIntercept = false
            }
        }
        return isIntercept
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                val distanceY = event.y - lastY
                if (abs(distanceY) > touchSlop) {
                    val rawY = event.rawY
                    val moveY = rawY - lastY
                    lyricDragListener?.onGlobalLyricDrag(moveY.toInt())
                }
            }
        }
        return super.onTouchEvent(event)
    }

    fun setLyricDragListener(lyricDragListener: OnGlobalLyricDragListener?) {
        this.lyricDragListener = lyricDragListener
    }

    fun setGlobalLyricListener(globalLyricListener: GlobalLyricListener?) {
        this.globalLyricListener = globalLyricListener
    }

    override fun onCheckedChanged(group: XRadioGroup, checkedId: Int) {
        val tag = group.findViewById<View>(checkedId)?.tag as? String ?: return
        val index = tag.toIntOrNull() ?: return
        updateLyricTextColor(index)
        sp.globalLyricTextColorIndex = index
    }

    private fun updateLyricTextColor(index: Int) {
        if (index !in LYRIC_COLORS.indices) {
            return
        }

        val color = context.getColor(LYRIC_COLORS[index])
        lyricLine1.setLyricSelectedTextColor(color)
    }

    fun setGlobalLyricOtherListener(globalLyricOtherListener: GlobalLyricOtherListener?) {
        this.globalLyricOtherListener = globalLyricOtherListener
    }

    private fun buildLayout() {
        orientation = VERTICAL
        setPaddingByDimen(R.dimen.padding_outer)

        addView(buildTitleContainer())
        addView(buildPlayContainer())
        addView(buildEditContainer())
    }

    private fun buildTitleContainer(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = HORIZONTAL
            addView(
                logo.apply {
                    setPaddingByDimen(R.dimen.d10)
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    setImageResource(R.mipmap.ic_launcher)
                },
                LayoutParams(dimenPx(R.dimen.d40), dimenPx(R.dimen.d40)),
            )
            addView(buildLyricLinesContainer(), LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
            addView(
                close.apply {
                    setPaddingByDimen(R.dimen.d5)
                    setImageResource(R.drawable.close)
                },
                LayoutParams(dimenPx(R.dimen.d40), dimenPx(R.dimen.d40)),
            )
        }
    }

    private fun buildLyricLinesContainer(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = VERTICAL
            val lineHeight = dimenPx(R.dimen.global_lyric_height)
            addView(lyricLine1, LayoutParams(LayoutParams.MATCH_PARENT, lineHeight))
            addView(lyricLine2, LayoutParams(LayoutParams.MATCH_PARENT, lineHeight))
        }
    }

    private fun buildPlayContainer(): LinearLayout {
        return playContainer.apply {
            gravity = Gravity.CENTER_VERTICAL
            orientation = HORIZONTAL
            addWeightedControl(lock, R.drawable.lock_desktop_lyric, R.dimen.d14)
            addWeightedControl(previous, R.drawable.music_previous, R.dimen.d14)
            addWeightedControl(play, R.drawable.music_play, R.dimen.padding_small)
            addWeightedControl(next, R.drawable.music_next, R.dimen.d14)
            addWeightedControl(settings, R.drawable.setting, R.dimen.d14)
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                dimenPx(R.dimen.d50),
            ).apply {
                topMargin = dimenPx(R.dimen.padding_meddle)
            }
        }
    }

    private fun buildEditContainer(): LinearLayout {
        return lyricEditContainer.apply {
            orientation = HORIZONTAL
            visibility = View.GONE
            addView(buildRadioGroup(), LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
            ).apply {
                topMargin = dimenPx(R.dimen.padding_meddle)
            }
        }
    }

    private fun buildRadioGroup(): XRadioGroup {
        return radioGroup.apply {
            gravity = Gravity.CENTER
            orientation = HORIZONTAL
            LYRIC_COLOR_BACKGROUNDS.forEachIndexed { index, background ->
                addView(createColorRadioContainer(index, background))
            }
            addView(createFontSizeControl(fontSizeSmall, R.drawable.ic_global_font_size_small))
            addView(createFontSizeControl(fontSizeLarge, R.drawable.ic_global_font_size_large))
        }
    }

    private fun createColorRadioContainer(index: Int, background: Int): LinearLayout {
        return LinearLayout(context).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            addView(
                RadioButton(context).apply {
                    id = radioButtonIds[index]
                    tag = index.toString()
                    setBackgroundResource(background)
                    setButtonDrawable(R.drawable.selector_desktop_lyric_radio_button)
                },
                LayoutParams(dimenPx(R.dimen.icon_height), dimenPx(R.dimen.icon_height)),
            )
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        }
    }

    private fun createFontSizeControl(target: ImageView, icon: Int): ImageView {
        return target.apply {
            setImageResource(icon)
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        }
    }

    private fun LinearLayout.addWeightedControl(
        imageView: ImageView,
        icon: Int,
        @DimenRes padding: Int,
    ) {
        addView(
            imageView.apply {
                setPaddingByDimen(padding)
                setImageResource(icon)
            },
            LayoutParams(0, LayoutParams.MATCH_PARENT, 1f),
        )
    }

    private fun View.setPaddingByDimen(@DimenRes resId: Int) {
        val padding = dimenPx(resId)
        setPadding(padding, padding, padding, padding)
    }

    private fun dimenPx(@DimenRes resId: Int): Int {
        return resources.getDimensionPixelSize(resId)
    }

    /**
     * 全局歌词拖拽接口。
     */
    interface OnGlobalLyricDragListener {
        fun onGlobalLyricDrag(y: Int)
    }

    /**
     * 全局歌词 View 监听器。
     */
    interface GlobalLyricListener {
        fun onLogoClick()

        fun onLockClick()

        fun onPreviousClick()

        fun onPlayClick()

        fun onNextClick()
    }

    interface GlobalLyricOtherListener {
        fun closeLyric()
    }

    companion object {
        private val LYRIC_COLORS = intArrayOf(
            R.color.lyric_color0,
            R.color.lyric_color1,
            R.color.lyric_color2,
            R.color.lyric_color3,
            R.color.lyric_color4,
        )

        private val LYRIC_COLOR_BACKGROUNDS = intArrayOf(
            R.drawable.shape_lyric_color0,
            R.drawable.shape_lyric_color1,
            R.drawable.shape_lyric_color2,
            R.drawable.shape_lyric_color3,
            R.drawable.shape_lyric_color4,
        )
    }
}
