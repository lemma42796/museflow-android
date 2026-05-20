package com.ixuea.courses.mymusic.component.music.activity

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseTitleActivity
import com.ixuea.courses.mymusic.component.music.model.event.ScanLocalMusicCompleteEvent
import com.ixuea.courses.mymusic.component.music.task.ScanLocalMusicAsyncTask
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.databinding.ActivityScanLocalMusicBinding
import com.ixuea.courses.mymusic.util.Constant
import org.greenrobot.eventbus.EventBus

/**
 * 扫描本地音乐界面
 */
class ScanLocalMusicActivity : BaseTitleActivity<ActivityScanLocalMusicBinding>() {
    private var isScanComplete = false
    private var isScanning = false
    private var scanLocalMusicAsyncTask: ScanLocalMusicAsyncTask? = null
    private var hasFoundMusic = false
    private var lineAnimation: TranslateAnimation? = null
    private var zoomValueAnimator: ValueAnimator? = null

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
        scanLocalMusicAsyncTask = object : ScanLocalMusicAsyncTask(applicationContext) {
            override fun onPostExecute(songs: List<Song>) {
                super.onPostExecute(songs)
                scanLocalMusicAsyncTask = null
                isScanComplete = true
                hasFoundMusic = songs.isNotEmpty()
                stopScan()
                binding.progress.text = resources.getString(R.string.found_music_count, songs.size)
                binding.primary.backgroundTintList = getColorStateList(R.color.primary)
                binding.primary.setText(R.string.to_my_music)
            }

            override fun onProgressUpdate(vararg values: String) {
                super.onProgressUpdate(*values)
                binding.progress.text = values.firstOrNull().orEmpty()
            }
        }

        scanLocalMusicAsyncTask?.execute()
    }

    private fun stopScan() {
        scanLocalMusicAsyncTask?.cancel(true)
        scanLocalMusicAsyncTask = null

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
            EventBus.getDefault().post(ScanLocalMusicCompleteEvent())
        }

        stopScan()
        super.onDestroy()
    }
}
