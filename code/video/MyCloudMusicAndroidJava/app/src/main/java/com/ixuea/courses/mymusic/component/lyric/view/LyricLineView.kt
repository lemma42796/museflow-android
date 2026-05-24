package com.ixuea.courses.mymusic.component.lyric.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.lyric.model.Line
import com.ixuea.courses.mymusic.util.TextUtil
import com.ixuea.superui.util.DensityUtil

/**
 * 一行歌词控件。
 *
 * TODO: 歌词超出一行时滚动显示。
 */
class LyricLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private var data: Line? = null
    private val backgroundTextPaint = Paint()
    private val foregroundTextPaint = Paint()
    private val selectedBackgroundPaint = Paint()
    private val fontMetrics = Paint.FontMetrics()
    private val selectedBackgroundBounds = RectF()

    private var lyricTextColor: Int = DEFAULT_LYRIC_TEXT_COLOR
    private var lyricTextSize: Int = DensityUtil.dip2px(context, DEFAULT_LYRIC_TEXT_SIZE).toInt()
    private var lyricSelectedTextColor: Int = DEFAULT_LYRIC_SELECTED_TEXT_COLOR
    private var lyricSelectedBackgroundColor: Int = DEFAULT_SELECTED_BACKGROUND_COLOR
    private var lineSelected: Boolean = false
    private var accurate: Boolean = false
    private var lyricCurrentWordIndex: Int = 0
    private var lineLyricPlayedWidth: Float = 0f
    private var wordPlayedTime: Float = 0f
    private val stringBuilder = StringBuilder()
    private var lyricGravity: Int = GRAVITY_CENTER

    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LyricLineView)
            lyricTextSize = typedArray.getDimension(
                R.styleable.LyricLineView_text_size,
                lyricTextSize.toFloat(),
            ).toInt()
            lyricTextColor = typedArray.getColor(
                R.styleable.LyricLineView_text_color,
                lyricTextColor,
            )
            lyricSelectedTextColor = typedArray.getColor(
                R.styleable.LyricLineView_selected_text_color,
                lyricSelectedTextColor,
            )
            lyricGravity = typedArray.getInt(R.styleable.LyricLineView_gravity, GRAVITY_CENTER)
            typedArray.recycle()
        }

        backgroundTextPaint.isDither = true
        backgroundTextPaint.isAntiAlias = true
        backgroundTextPaint.color = lyricTextColor

        foregroundTextPaint.isDither = true
        foregroundTextPaint.isAntiAlias = true

        selectedBackgroundPaint.isDither = true
        selectedBackgroundPaint.isAntiAlias = true
        selectedBackgroundPaint.color = lyricSelectedBackgroundColor

        setLayerType(LAYER_TYPE_SOFTWARE, null)
        updateTextColor()
        updateTextSize()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        if (!isEmptyLyric()) {
            drawLyricText(canvas)
        }
        canvas.restore()
    }

    private fun drawLyricText(canvas: Canvas) {
        val line = data ?: return
        val text = line.data.orEmpty()
        val textWidth = TextUtil.getTextWidth(backgroundTextPaint, text)
        val textHeight = TextUtil.getTextHeight(backgroundTextPaint)
        val centerX = getCenterX(textWidth)

        backgroundTextPaint.getFontMetrics(fontMetrics)
        val centerY = (measuredHeight - textHeight) / 2 + kotlin.math.abs(fontMetrics.top)

        if (lineSelected) {
            drawSelectedBackground(canvas, centerX, textWidth)
        }

        canvas.drawText(text, centerX, centerY, backgroundTextPaint)

        if (lineSelected) {
            if (accurate) {
                lineLyricPlayedWidth = resolvePlayedWidth(line, textWidth)
                canvas.clipRect(centerX, 0f, centerX + lineLyricPlayedWidth, measuredHeight.toFloat())
            }
            canvas.drawText(text, centerX, centerY, foregroundTextPaint)
        }
    }

    private fun drawSelectedBackground(canvas: Canvas, centerX: Float, textWidth: Float) {
        val horizontalPadding = dp(24f)
        val verticalPadding = dp(9f)
        val backgroundWidth = (textWidth + horizontalPadding * 2)
            .coerceAtMost(measuredWidth - dp(32f))
        val left = (centerX - horizontalPadding)
            .coerceAtLeast(dp(16f))
            .coerceAtMost(measuredWidth - backgroundWidth - dp(16f))
        val top = ((measuredHeight - lyricTextSize) / 2f - verticalPadding)
            .coerceAtLeast(dp(4f))
        val bottom = ((measuredHeight + lyricTextSize) / 2f + verticalPadding)
            .coerceAtMost(measuredHeight - dp(4f))
        selectedBackgroundBounds.set(left, top, left + backgroundWidth, bottom)
        canvas.drawRoundRect(
            selectedBackgroundBounds,
            selectedBackgroundBounds.height() / 2f,
            selectedBackgroundBounds.height() / 2f,
            selectedBackgroundPaint,
        )
    }

    private fun resolvePlayedWidth(line: Line, textWidth: Float): Float {
        if (lyricCurrentWordIndex == -1) {
            return textWidth
        }

        val lyricWords = line.words ?: return textWidth
        val wordDurations = line.wordDurations ?: return textWidth
        if (lyricCurrentWordIndex !in lyricWords.indices ||
            lyricCurrentWordIndex !in wordDurations.indices ||
            wordDurations[lyricCurrentWordIndex] <= 0
        ) {
            return textWidth
        }

        val beforeText = getBeforeText(line, lyricCurrentWordIndex)
        val beforeTextWidth = TextUtil.getTextWidth(foregroundTextPaint, beforeText)
        val currentWord = lyricWords[lyricCurrentWordIndex]
        val currentWordTextWidth = TextUtil.getTextWidth(foregroundTextPaint, currentWord)
        val currentWordPlayedWidth =
            currentWordTextWidth / wordDurations[lyricCurrentWordIndex] * wordPlayedTime
        return beforeTextWidth + currentWordPlayedWidth
    }

    private fun getBeforeText(data: Line, index: Int): String {
        stringBuilder.setLength(0)
        val words = data.words.orEmpty()
        for (i in 0 until index.coerceAtMost(words.size)) {
            stringBuilder.append(words[i])
        }
        return stringBuilder.toString()
    }

    private fun getCenterX(textWidth: Float): Float {
        return when (lyricGravity) {
            GRAVITY_LEFT -> 0f
            else -> (measuredWidth - textWidth) / 2
        }
    }

    private fun isEmptyLyric(): Boolean {
        return data == null
    }

    fun setData(data: Line?) {
        this.data = data
        invalidate()
    }

    fun setLyricTextColor(lyricTextColor: Int) {
        this.lyricTextColor = lyricTextColor
        backgroundTextPaint.color = lyricTextColor
        invalidate()
    }

    fun setLineSelected(lineSelected: Boolean) {
        this.lineSelected = lineSelected
        backgroundTextPaint.isFakeBoldText = lineSelected
        foregroundTextPaint.isFakeBoldText = lineSelected
        backgroundTextPaint.alpha = if (lineSelected) SELECTED_TEXT_ALPHA else NORMAL_TEXT_ALPHA
        foregroundTextPaint.alpha = SELECTED_TEXT_ALPHA
        invalidate()
    }

    fun setAccurate(accurate: Boolean) {
        this.accurate = accurate
    }

    /**
     * 歌词进度。
     */
    fun onProgress() {
        if (!isEmptyLyric()) {
            invalidate()
        }
    }

    fun setLyricCurrentWordIndex(lyricCurrentWordIndex: Int) {
        this.lyricCurrentWordIndex = lyricCurrentWordIndex
    }

    fun setWordPlayedTime(wordPlayedTime: Float) {
        this.wordPlayedTime = wordPlayedTime
    }

    fun setLyricSelectedTextColor(lyricSelectedTextColor: Int) {
        this.lyricSelectedTextColor = lyricSelectedTextColor
        updateTextColor()
    }

    private fun updateTextColor() {
        foregroundTextPaint.color = lyricSelectedTextColor
        invalidate()
    }

    fun decrementTextSize(): Int {
        lyricTextSize--
        updateTextSize()
        return lyricTextSize
    }

    fun incrementTextSize(): Int {
        lyricTextSize++
        updateTextSize()
        return lyricTextSize
    }

    private fun updateTextSize() {
        backgroundTextPaint.textSize = lyricTextSize.toFloat()
        foregroundTextPaint.textSize = lyricTextSize.toFloat()
        backgroundTextPaint.setShadowLayer(dp(6f), 0f, dp(2f), Color.argb(80, 0, 0, 0))
        foregroundTextPaint.setShadowLayer(dp(8f), 0f, dp(2f), Color.argb(100, 0, 0, 0))
        invalidate()
    }

    fun setLyricTextSize(lyricTextSize: Int) {
        this.lyricTextSize = lyricTextSize
        updateTextSize()
    }

    fun setLyricSelectedBackgroundColor(color: Int) {
        lyricSelectedBackgroundColor = color
        selectedBackgroundPaint.color = color
        invalidate()
    }

    private fun dp(value: Float): Float {
        return DensityUtil.dip2px(context, value).toFloat()
    }

    companion object {
        private const val DEFAULT_LYRIC_TEXT_SIZE = 16f
        private val DEFAULT_LYRIC_TEXT_COLOR = Color.argb(204, 255, 255, 255)
        private val DEFAULT_LYRIC_SELECTED_TEXT_COLOR = Color.parseColor("#3FF8DC")
        private val DEFAULT_SELECTED_BACKGROUND_COLOR = Color.argb(38, 255, 255, 255)
        private const val NORMAL_TEXT_ALPHA = 204
        private const val SELECTED_TEXT_ALPHA = 255
        private const val GRAVITY_LEFT = 0x01
        private const val GRAVITY_CENTER = 0x10
    }
}
