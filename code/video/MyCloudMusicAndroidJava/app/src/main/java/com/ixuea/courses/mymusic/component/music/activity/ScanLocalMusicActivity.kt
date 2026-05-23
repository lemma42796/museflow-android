package com.ixuea.courses.mymusic.component.music.activity

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import androidx.lifecycle.lifecycleScope
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseTitleActivity
import com.ixuea.courses.mymusic.component.music.domain.NotifyLocalMusicScanCompleteUseCase
import com.ixuea.courses.mymusic.component.music.domain.ScanLocalMusicUseCase
import com.ixuea.courses.mymusic.databinding.ActivityScanLocalMusicBinding
import com.ixuea.courses.mymusic.util.Constant
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 扫描本地音乐界面
 */
class ScanLocalMusicActivity : BaseTitleActivity<ActivityScanLocalMusicBinding>() {
    private var isScanComplete = false
    private var isScanning = false
    private var scanJob: Job? = null
    private var hasFoundMusic = false
    private var lineAnimation: TranslateAnimation? = null
    private var zoomValueAnimator: ValueAnimator? = null
    private val scanLocalMusic = ScanLocalMusicUseCase()
    private val notifyScanComplete = NotifyLocalMusicScanCompleteUseCase()

    override fun initListeners() {
        super.initListeners()
        binding.primary.setOnClickListener {
            onScanClick()
        }
    }

    fun onScanClick() {
        if (isScanComplete) {
            finish()
            return
        }

        if (isScanning) {
            stopScan()
            binding.primary.backgroundTintList = getColorStateList(R.color.primary)
            binding.primary.setText(R.string.start_scan)
        } else {
            startScan()
            binding.primary.backgroundTintList = getColorStateList(R.color.black11)
            binding.primary.setText(R.string.stop_scan)
        }

        isScanning = !isScanning
    }

    private fun startScan() {
        startLineAnimation()
        startZoomAnimation()
        startScanMusic()
    }

    private fun startLineAnimation() {
        lineAnimation = TranslateAnimation(
            TranslateAnimation.RELATIVE_TO_PARENT,
            0f,
            TranslateAnimation.RELATIVE_TO_PARENT,
            0f,
            TranslateAnimation.RELATIVE_TO_PARENT,
            0f,
            TranslateAnimation.RELATIVE_TO_PARENT,
            0.7f,
        ).apply {
            interpolator = DecelerateInterpolator()
            duration = 2000
            repeatCount = Animation.INFINITE
            repeatMode = Animation.REVERSE
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    binding.scanMusicLine.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animation) {
                    binding.scanMusicLine.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {
                }
            })
        }

        binding.scanMusicLine.clearAnimation()
        binding.scanMusicLine.startAnimation(lineAnimation)
    }

    private fun startZoomAnimation() {
        zoomValueAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            interpolator = LinearInterpolator()
            duration = 30000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener { animation ->
                val angle = animation.animatedValue as Float
                val translateX = (
                    Constant.DEFAULT_RADIUS_SCAN_LOCAL_MUSIC_ZOOM *
                        kotlin.math.cos(angle.toDouble())
                    ).toFloat()
                val translateY = (
                    Constant.DEFAULT_RADIUS_SCAN_LOCAL_MUSIC_ZOOM *
                        kotlin.math.sin(angle.toDouble())
                    ).toFloat()
                binding.scanMusicZoom.translationX = translateX
                binding.scanMusicZoom.translationY = translateY
            }
            start()
        }
    }

    private fun startScanMusic() {
        scanJob?.cancel()
        scanJob = lifecycleScope.launch {
            val songs = scanLocalMusic(applicationContext) { path ->
                binding.progress.text = path
            }
            scanJob = null
            isScanComplete = true
            isScanning = false
            hasFoundMusic = songs.isNotEmpty()
            stopScan()
            binding.progress.text = resources.getString(R.string.found_music_count, songs.size)
            binding.primary.backgroundTintList = getColorStateList(R.color.primary)
            binding.primary.setText(R.string.to_my_music)
        }
    }

    private fun stopScan() {
        scanJob?.cancel()
        scanJob = null

        binding.scanMusicLine.clearAnimation()
        binding.scanMusicLine.visibility = View.GONE

        lineAnimation?.cancel()
        lineAnimation = null

        binding.scanMusicZoom.clearAnimation()
        zoomValueAnimator?.cancel()
        zoomValueAnimator = null
    }

    override fun onDestroy() {
        if (hasFoundMusic) {
            notifyScanComplete()
        }

        stopScan()
        super.onDestroy()
    }
}
