package com.ixuea.courses.mymusic.component.download.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter
import com.ixuea.courses.mymusic.component.download.listener.MyDownloadListener
import com.ixuea.courses.mymusic.component.download.model.event.DownloadChangedEvent
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.databinding.ItemDownloadingBinding
import com.ixuea.courses.mymusic.util.FileUtil
import com.ixuea.courses.mymusic.util.LiteORMUtil
import com.ixuea.superui.dialog.SuperDialog
import org.greenrobot.eventbus.EventBus
import java.lang.ref.SoftReference

/**
 * 下载中适配器
 */
class DownloadingAdapter(
    context: Context,
    private val orm: LiteORMUtil,
    private val fragmentManager: FragmentManager
) : BaseRecyclerViewAdapter<DownloadInfo, DownloadingAdapter.ViewHolder>(context) {
    private var listener: DownloadingAdapterListener? = null

    fun setListener(listener: DownloadingAdapterListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDownloadingBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val data = getData(position)
        val song = orm.querySong(data.id)
        holder.bindBase(song)
        holder.bind(data)
    }

    private fun publishDownloadStatusChangedEvent(isDownloadManagerNotify: Boolean) {
        if (isDownloadManagerNotify) {
            EventBus.getDefault().post(DownloadChangedEvent())
        }
    }

    /**
     * 下载ViewHolder
     */
    inner class ViewHolder(
        private val binding: ItemDownloadingBinding
    ) : BaseRecyclerViewAdapter.ViewHolder<DownloadInfo>(binding.root) {
        private var data: DownloadInfo? = null

        /**
         * 显示基础数据
         */
        fun bindBase(data: Song?) {
            binding.title.text = data?.title.orEmpty()
        }

        /**
         * 显示下载信息
         */
        override fun bind(data: DownloadInfo) {
            this.data = data

            data.downloadListener = object : MyDownloadListener(SoftReference<Any>(this)) {
                override fun onRefresh() {
                    val holder = getUserTag()?.get() as? ViewHolder ?: return
                    holder.refresh(true)
                }
            }

            refresh(false)

            binding.delete.setOnClickListener {
                SuperDialog.newInstance(fragmentManager)
                    .setTitleRes(R.string.confirm_delete)
                    .setOnClickListener(View.OnClickListener {
                        val position = bindingAdapterPosition
                        if (position == RecyclerView.NO_POSITION) {
                            return@OnClickListener
                        }

                        listener?.onDeleteClick(position, data)
                    })
                    .show()
            }
        }

        /**
         * 显示下载信息
         */
        private fun refresh(isDownloadManagerNotify: Boolean) {
            val data = data ?: return

            when (data.status) {
                DownloadInfo.STATUS_PAUSED -> {
                    binding.info.setText(R.string.click_download)
                    binding.progress.visibility = View.GONE
                }

                DownloadInfo.STATUS_ERROR -> {
                    binding.info.setText(R.string.download_failed)
                    binding.progress.visibility = View.GONE
                }

                DownloadInfo.STATUS_DOWNLOADING,
                DownloadInfo.STATUS_PREPARE_DOWNLOAD -> {
                    binding.info.visibility = View.VISIBLE
                    binding.progress.visibility = View.VISIBLE

                    if (data.size > 0) {
                        val start = FileUtil.formatFileSize(data.progress)
                        val size = FileUtil.formatFileSize(data.size)

                        binding.info.text = context.resources.getString(
                            R.string.download_progress,
                            start,
                            size
                        )

                        binding.progress.max = data.size.toInt()
                        binding.progress.progress = data.progress.toInt()
                    }
                }

                DownloadInfo.STATUS_WAIT -> {
                    binding.info.setText(R.string.wait_download)
                    binding.progress.visibility = View.GONE
                }

                else -> {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        removeData(position)
                    }

                    publishDownloadStatusChangedEvent(isDownloadManagerNotify)
                }
            }
        }
    }

    interface DownloadingAdapterListener {
        fun onDeleteClick(position: Int, data: DownloadInfo)
    }
}
