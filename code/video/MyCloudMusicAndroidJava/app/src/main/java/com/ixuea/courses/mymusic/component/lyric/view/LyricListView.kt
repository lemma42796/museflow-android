package com.ixuea.courses.mymusic.component.lyric.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.lyric.adapter.LyricAdapter
import com.ixuea.courses.mymusic.component.lyric.model.Line
import com.ixuea.courses.mymusic.component.lyric.model.Lyric
import com.ixuea.courses.mymusic.databinding.LyricListViewBinding
import com.ixuea.courses.mymusic.service.MusicPlayerService
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.LyricUtil
import com.ixuea.courses.mymusic.util.SuperDateUtil
import com.ixuea.superui.util.DensityUtil
import com.ixuea.superui.util.SuperViewUtil
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask
import kotlin.math.ceil

/**
 * 垂直显示多行歌词，使用 RecyclerView 实现。
 */
class LyricListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val binding = LyricListViewBinding.inflate(LayoutInflater.from(context), this, true)
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var lyricAdapter: LyricAdapter
    private var data: Lyric? = null
    private var lyricLineNumber: Int = 0
    private var lyricPlaceholderSize: Int = 0
    private var lyricOffsetY: Int = 0
    private var isDrag: Boolean = false
    private var lyricTimerTask: TimerTask? = null
    private var lyricTimer: Timer? = null
    private var scrollSelectedLyricLine: Line? = null
    private var lyricListListener: LyricListListener? = null

    init {
        initViews()
        initDatum()
        initListeners()
    }

    private fun initListeners() {
        binding.lyricPlay.setOnClickListener {
            cancelLyricTask()
            showScrollLyricView()
            scrollSelectedLyricLine?.let { line ->
                MusicPlayerService.getListManager(context).seekTo(line.startTime.toInt())
            }
        }

        binding.lyricList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> showDragView()
                    RecyclerView.SCROLL_STATE_IDLE -> prepareScrollLyricView()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisibleItemPosition =
                    layoutManager.findFirstVisibleItemPosition() + lyricPlaceholderSize - 1
                Timber.d(
                    "onPageScrolled dy:%d firstVisibleItemPosition:%d",
                    dy,
                    firstVisibleItemPosition,
                )

                if (isDrag) {
                    updateScrollSelectedLyricLine(firstVisibleItemPosition)
                }
            }
        })

        binding.root.setOnClickListener {
            lyricListListener?.onLyricClick()
        }

        lyricAdapter.setOnItemClickListener { _, _, _ ->
            lyricListListener?.onLyricClick()
        }

        lyricAdapter.setOnItemLongClickListener { _, _, _ ->
            lyricListListener?.onLyricLongClick() ?: false
        }
    }

    private fun updateScrollSelectedLyricLine(firstVisibleItemPosition: Int) {
        if (lyricAdapter.itemCount == 0) {
            return
        }

        val boundedPosition = firstVisibleItemPosition.coerceIn(0, lyricAdapter.itemCount - 1)
        val item = lyricAdapter.getItem(boundedPosition)
        scrollSelectedLyricLine = if (item is String) {
            if (boundedPosition < lyricPlaceholderSize) {
                lyricAdapter.getItemOrNull(lyricPlaceholderSize) as? Line
            } else {
                val index = lyricAdapter.itemCount - 1 - lyricPlaceholderSize
                lyricAdapter.getItemOrNull(index) as? Line
            }
        } else {
            item as? Line
        }

        scrollSelectedLyricLine?.let { line ->
            binding.lyricTime.text = SuperDateUtil.ms2ms(line.startTime.toInt())
        }
    }

    /**
     * 准备滚动歌词。
     */
    private fun prepareScrollLyricView() {
        cancelLyricTask()
        lyricTimerTask = object : TimerTask() {
            override fun run() {
                binding.lyricList.post {
                    showScrollLyricView()
                }
            }
        }

        lyricTimer = Timer().apply {
            schedule(lyricTimerTask, Constant.LYRIC_HIDE_DRAG_TIME)
        }
    }

    private fun cancelLyricTask() {
        lyricTimerTask?.cancel()
        lyricTimerTask = null

        lyricTimer?.cancel()
        lyricTimer = null
    }

    private fun showScrollLyricView() {
        isDrag = false
        SuperViewUtil.gone(binding.lyricDragContainer)
    }

    private fun showDragView() {
        if (isLyricEmpty()) {
            return
        }

        isDrag = true
        SuperViewUtil.show(binding.lyricDragContainer)
    }

    private fun isLyricEmpty(): Boolean {
        return lyricAdapter.itemCount == 0
    }

    private fun initDatum() {
        lyricAdapter = LyricAdapter(R.layout.item_lyric)
        binding.lyricList.adapter = lyricAdapter
    }

    private fun initViews() {
        binding.lyricList.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
        binding.lyricList.layoutManager = layoutManager
    }

    override fun onDetachedFromWindow() {
        cancelLyricTask()
        super.onDetachedFromWindow()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (lyricOffsetY != 0) {
            return
        }

        lyricOffsetY = (measuredHeight / 2 - (DensityUtil.dip2px(context, 40f) / 2)).toInt()
        lyricPlaceholderSize = ceil(
            measuredHeight / 1.0 / 2 / DensityUtil.dip2px(context, 40f),
        ).toInt()

        next()
    }

    fun setData(data: Lyric?) {
        this.data = data

        if (lyricPlaceholderSize > 0) {
            next()
        }
    }

    private fun next() {
        val currentLyric = data
        if (currentLyric == null) {
            lyricAdapter.setNewInstance(arrayListOf())
            SuperViewUtil.gone(binding.lyricList)
        } else {
            SuperViewUtil.show(binding.lyricList)
            val datum = arrayListOf<Any>()
            addLyricFillData(datum)
            datum.addAll(currentLyric.datum.orEmpty())
            addLyricFillData(datum)
            lyricAdapter.setAccurate(currentLyric.isAccurate)
            lyricAdapter.setNewInstance(datum)
        }
    }

    /**
     * 添加歌词占位数据。
     */
    fun addLyricFillData(datum: MutableList<Any>) {
        for (i in 0 until lyricPlaceholderSize) {
            datum.add("fill")
        }
    }

    /**
     * 设置播放进度。
     */
    fun setProgress(progress: Int) {
        val currentLyric = data ?: return
        if (lyricAdapter.data.isEmpty() || isDrag) {
            return
        }

        val newLineNumber = LyricUtil.getLineNumber(currentLyric, progress) + lyricPlaceholderSize
        if (newLineNumber != lyricLineNumber) {
            scrollPosition(newLineNumber)
            lyricLineNumber = newLineNumber
        }

        if (currentLyric.isAccurate) {
            val line = lyricAdapter.data.getOrNull(lyricLineNumber) as? Line ?: return
            val progressTime = progress.toLong()
            val lyricCurrentWordIndex = LyricUtil.getWordIndex(line, progressTime)
            val wordPlayedTime = LyricUtil.getWordPlayedTime(line, progressTime)
            val view = layoutManager.findViewByPosition(lyricLineNumber) ?: return
            val contentView = view.findViewById<LyricLineView>(R.id.content)
            contentView.setLyricCurrentWordIndex(lyricCurrentWordIndex)
            contentView.setWordPlayedTime(wordPlayedTime)
            contentView.onProgress()
        }
    }

    private fun scrollPosition(lineNumber: Int) {
        binding.lyricList.post {
            lyricAdapter.setSelectedIndex(lineNumber)

            if (lyricOffsetY > 0) {
                layoutManager.scrollToPositionWithOffset(lineNumber, lyricOffsetY)
            }
        }
    }

    fun setLyricListListener(lyricListListener: LyricListListener?) {
        this.lyricListListener = lyricListListener
    }

    /**
     * 歌词列表控件监听器。
     */
    interface LyricListListener {
        fun onLyricClick() {
        }

        fun onLyricLongClick(): Boolean {
            return false
        }
    }
}
