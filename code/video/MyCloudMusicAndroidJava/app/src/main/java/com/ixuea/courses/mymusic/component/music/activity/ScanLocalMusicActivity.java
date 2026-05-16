package com.ixuea.courses.mymusic.component.music.activity;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.music.model.event.ScanLocalMusicCompleteEvent;
import com.ixuea.courses.mymusic.component.music.task.ScanLocalMusicAsyncTask;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.databinding.ActivityScanLocalMusicBinding;
import com.ixuea.courses.mymusic.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 扫描本地音乐界面
 */
public class ScanLocalMusicActivity extends BaseTitleActivity<ActivityScanLocalMusicBinding> {
    /**
     * 是否扫描完成
     */
    private boolean isScanComplete;

    /**
     * 是否在扫描中
     */
    private boolean isScanning;

    /**
     * 扫描音乐的异步任务
     */
    private ScanLocalMusicAsyncTask scanLocalMusicAsyncTask;

    /**
     * 是否找到了本地音乐
     */
    private boolean hasFoundMusic;

    /**
     * 扫描线动画
     */
    private TranslateAnimation lineAnimation;

    /**
     * 放大镜动画
     */
    private ValueAnimator zoomValueAnimator;

    @Override
    protected void initListeners() {
        super.initListeners();
        binding.primary.setOnClickListener(v -> onScanClick());
    }

    /**
     * 扫描按钮点击了
     */
    public void onScanClick() {
        //如果扫描完成了
        //点击按钮就是关闭界面
        //扫描按钮的样式在扫描完成的方法里面更改了
        if (isScanComplete) {
            finish();
            return;
        }

        if (isScanning) {
            //扫描中

            //停止扫描
            stopScan();

            //背景
            binding.primary.setBackgroundTintList(getColorStateList(R.color.primary));

            //设置按钮文本
            binding.primary.setText(R.string.start_scan);
        } else {
            //没有扫描

            //开始扫描
            startScan();

            //背景
            binding.primary.setBackgroundTintList(getColorStateList(R.color.black11));

            //文本
            binding.primary.setText(R.string.stop_scan);
        }

        //改变扫描状态
        isScanning = !isScanning;
    }

    /**
     * 开始扫描
     */
    private void startScan() {
        //region 扫描线动画
        //不使用属性动画
        //是因为属性动画要获取坐标

        //创建一个位移动画
        //RELATIVE_TO_PARENT：表示位置是相对父类的
        //0：就表示现在的位置
        //1：就表示在父类的另一边
        //例如：y开始位置为0
        //结束位置为1
        //就表示从父容器顶部移动到父容器底部
        lineAnimation = new TranslateAnimation(
                //原x坐标类型；坐标值
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,

                //目标x坐标类型；坐标值
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,

                //原y坐标类型；坐标值
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,

                //目标y坐标类型；坐标值
                TranslateAnimation.RELATIVE_TO_PARENT, 0.7f
        );

        //设置插值器
        //这里设置的是减速插值器
        //也就是说开始速度很快慢慢变慢
        //可以理解为人刚开始有劲干活
        //后面就没劲了
        //所以速度就慢了
        lineAnimation.setInterpolator(new DecelerateInterpolator());

        //设置动画时间
        lineAnimation.setDuration(2000);

        //重复次数
        //-1:无限重复
        lineAnimation.setRepeatCount(-1);

        //重复模式
        //REVERSE：表示从开始位置到结束位置
        //然后从结束位置到开始位置
        lineAnimation.setRepeatMode(TranslateAnimation.REVERSE);

        //设置动画监听器
        lineAnimation.setAnimationListener(new Animation.AnimationListener() {
            /**
             * 动画开始
             *
             * @param animation
             */
            @Override
            public void onAnimationStart(Animation animation) {
                //显示线
                binding.scanMusicLine.setVisibility(View.VISIBLE);
            }

            /**
             * 动画结束
             *
             * @param animation
             */
            @Override
            public void onAnimationEnd(Animation animation) {
                //隐藏线
                binding.scanMusicLine.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //清除原来的动画
        binding.scanMusicLine.clearAnimation();

        //执行动画
        binding.scanMusicLine.startAnimation(lineAnimation);
        //endregion

        //region 放大镜动画
        //其实下面动画的效果就是
        //让放大镜中心在一个圆周上做运动
        zoomValueAnimator = ValueAnimator.ofFloat(0.0f, 360.0F);

        //设置插值器
        zoomValueAnimator.setInterpolator(new LinearInterpolator());

        //动画时间
        zoomValueAnimator.setDuration(30000);

        //无限重复
        zoomValueAnimator.setRepeatCount(-1);

        //重复模式
        //重新开始
        zoomValueAnimator.setRepeatMode(ValueAnimator.RESTART);

        //动画监听器
        zoomValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            /**
             * 每次动画调用
             *
             * @param animation
             */
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //获取角度
                float angle = (float) animation.getAnimatedValue();

                //由于系统需要的是x,y
                //所以通过三角函数计算出x,y

                //余弦值
                float translateX = (float) (Constant.DEFAULT_RADIUS_SCAN_LOCAL_MUSIC_ZOOM * Math.cos(angle));

                //正弦值
                float translateY = (float) (Constant.DEFAULT_RADIUS_SCAN_LOCAL_MUSIC_ZOOM * Math.sin(angle));

                //设置偏移位置

                //现在是顺时针
                //如果颠倒x,y
                //就变成逆时针了
                //如果y先是正数就是顺时针
                binding.scanMusicZoom.setTranslationX(translateX);
                binding.scanMusicZoom.setTranslationY(translateY);
            }
        });

        //开始动画
        zoomValueAnimator.start();
        //endregion

        //扫描音乐
        startScanMusic();
    }

    private void startScanMusic() {
        //扫描音乐是耗时任务
        //所以放到子线程
        //这里用的是AsyncTask
        scanLocalMusicAsyncTask = new ScanLocalMusicAsyncTask(getApplicationContext()) {
            /**
             * 执行完成后
             * @param songs
             */
            @Override
            protected void onPostExecute(List<Song> songs) {
                super.onPostExecute(songs);

                //清除异步任务
                scanLocalMusicAsyncTask = null;

                //扫描完成
                isScanComplete = true;

                //是否找到了音乐
                hasFoundMusic = songs.size() > 0;

                //停止扫描
                stopScan();

                //显示扫描到的音乐数量
                binding.progress.setText(getResources().getString(R.string.found_music_count, songs.size()));

                binding.primary.setBackgroundTintList(getColorStateList(R.color.primary));

                //设置按钮文本
                binding.primary.setText(R.string.to_my_music);
            }

            /**
             * 进度回调方法
             * 主线程中执行
             * 调用publishProgress就会执行该方法
             *
             * @param values
             */
            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);

                //获取传递的值
                String value = values[0];

                //设置进度到文本
                binding.progress.setText(value);
            }
        };

        //启动异步任务
        scanLocalMusicAsyncTask.execute();
    }

    /**
     * 停止扫描
     */
    private void stopScan() {
        //取消异步任务
        if (scanLocalMusicAsyncTask != null) {
            scanLocalMusicAsyncTask.cancel(true);
            scanLocalMusicAsyncTask = null;
        }

        //清除扫描线动画
        binding.scanMusicLine.clearAnimation();

        //隐藏扫描线
        binding.scanMusicLine.setVisibility(View.GONE);

        //取消扫描线动画
        if (lineAnimation != null) {
            lineAnimation.cancel();
            lineAnimation = null;
        }

        //清除放大镜动画
        binding.scanMusicZoom.clearAnimation();

        //取消放大镜动画
        if (null != zoomValueAnimator) {
            zoomValueAnimator.cancel();
            zoomValueAnimator=null;
        }
    }

    /**
     * 界面销毁时
     */
    @Override
    protected void onDestroy() {
        if (hasFoundMusic) {
            //找到了音乐发送通知
            EventBus.getDefault().post(new ScanLocalMusicCompleteEvent());
        }

        //停止扫描
        stopScan();

        super.onDestroy();
    }
}