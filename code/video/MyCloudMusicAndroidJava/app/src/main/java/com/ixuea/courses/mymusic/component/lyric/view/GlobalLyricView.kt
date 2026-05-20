package com.ixuea.courses.mymusic.component.lyric.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.LinearLayout
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.databinding.ViewGlobalLyricBinding
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

    private val binding = ViewGlobalLyricBinding.inflate(LayoutInflater.from(context), this, true)
    private lateinit var sp: PreferenceUtil
    private var lyricDragListener: OnGlobalLyricDragListener? = null
    private var globalLyricListener: GlobalLyricListener? = null
    private var globalLyricOtherListener: GlobalLyricOtherListener? = null
    private var isIntercept: Boolean = false
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var touchSlop: Float = 0f

    init {
        initViews()
        initDatum()
        initListeners()
    }

    private fun initViews() {
        setGlobalBackground()
        binding.lyricLine1.setLineSelected(true)
    }

    private fun initDatum() {
        sp = PreferenceUtil.getInstance(context)
        val lyricTextColorIndex = sp.globalLyricTextColorIndex
        updateLyricTextColor(lyricTextColorIndex)

        val radioButtonId = RADIO_BUTTONS[lyricTextColorIndex]
        binding.radioGroup.check(radioButtonId)

        val lyricTextSize = sp.globalLyricTextSize
        binding.lyricLine1.setLyricTextSize(lyricTextSize)
        binding.lyricLine2.setLyricTextSize(lyricTextSize)

        binding.lyricLine1.setLyricTextColor(context.getColor(R.color.black165))
        binding.lyricLine2.setLyricTextColor(context.getColor(R.color.black165))

        touchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
    }

    private fun initListeners() {
        val thisClickListener = OnClickListener {
            if (binding.logo.visibility == View.VISIBLE) {
                simpleStyle()
            } else {
                normalStyle()
            }
        }
        setOnClickListener(thisClickListener)

        binding.logo.setOnClickListener {
            globalLyricListener?.onLogoClick()
        }

        binding.close.setOnClickListener {
            globalLyricOtherListener?.closeLyric()
        }

        binding.lock.setOnClickListener {
            globalLyricListener?.onLockClick()
        }

        binding.previous.setOnClickListener {
            globalLyricListener?.onPreviousClick()
        }

        binding.play.setOnClickListener {
            globalLyricListener?.onPlayClick()
        }

        binding.next.setOnClickListener {
            globalLyricListener?.onNextClick()
        }

        binding.settings.setOnClickListener {
            binding.lyricEditContainer.visibility =
                if (binding.lyricEditContainer.visibility == View.VISIBLE) {
                    GONE
                } else {
                    VISIBLE
                }
        }

        binding.radioGroup.setOnCheckedChangeListener(this)

        binding.fontSizeSmall.setOnClickListener {
            val currentSize = binding.lyricLine1.decrementTextSize()
            setLyricTextSize(currentSize)
            sp.globalLyricTextSize = currentSize
        }

        binding.fontSizeLarge.setOnClickListener {
            val currentSize = binding.lyricLine1.incrementTextSize()
            setLyricTextSize(currentSize)
            sp.globalLyricTextSize = currentSize
        }
    }

    private fun setLyricTextSize(currentSize: Int) {
        binding.lyricLine2.setLyricTextSize(currentSize)
    }

    private fun setGlobalBackground() {
        setBackgroundColor(context.getColor(R.color.global_lyric_background))
    }

    /**
     * 标准样式，控件都显示。
     */
    fun normalStyle() {
        setGlobalBackground()
        binding.logo.visibility = VISIBLE
        binding.close.visibility = View.VISIBLE
        binding.playContainer.visibility = View.VISIBLE
    }

    /**
     * 简单样式，只显示歌词。
     */
    fun simpleStyle() {
        setBackgroundColor(context.getColor(R.color.transparent))
        binding.logo.visibility = View.GONE
        binding.close.visibility = GONE
        binding.playContainer.visibility = GONE
        binding.lyricEditContainer.visibility = GONE
    }

    fun clearLyric() {
        binding.lyricLine1.setData(null)
        binding.lyricLine2.setData(null)
    }

    fun setPlay(playing: Boolean) {
        binding.play.setImageResource(if (playing) R.drawable.music_pause else R.drawable.music_play)
    }

    fun setAccurate(accurate: Boolean) {
        binding.lyricLine1.setAccurate(accurate)
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
        binding.lyricLine1.setData(line)

        if (lyric.isAccurate && line != null) {
            val lyricCurrentWordIndex = LyricUtil.getWordIndex(line, progress)
            val wordPlayedTime = LyricUtil.getWordPlayedTime(line, progress)
            binding.lyricLine1.setLyricCurrentWordIndex(lyricCurrentWordIndex)
            binding.lyricLine1.setWordPlayedTime(wordPlayedTime)
            binding.lyricLine1.onProgress()
        }

        val datum = lyric.datum.orEmpty()
        val lineNumber = LyricUtil.getLineNumber(lyric, progress.toInt())
        if (lineNumber < datum.size - 1) {
            binding.lyricLine2.setData(datum[lineNumber + 1])
        } else {
            binding.lyricLine2.setData(null)
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
        binding.lyricLine1.setLyricSelectedTextColor(color)
    }

    fun setGlobalLyricOtherListener(globalLyricOtherListener: GlobalLyricOtherListener?) {
        this.globalLyricOtherListener = globalLyricOtherListener
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

        private val RADIO_BUTTONS = intArrayOf(
            R.id.radio_button0,
            R.id.radio_button1,
            R.id.radio_button2,
            R.id.radio_button3,
            R.id.radio_button4,
        )
    }
}
