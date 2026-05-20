package com.ixuea.courses.mymusic.component.sheet.adapter

import androidx.fragment.app.FragmentManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.courses.mymusic.AppContext
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.download.domain.DownloadActionsUseCase
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.superui.dialog.SuperDialog

/**
 * 歌单详情-歌曲适配器
 */
class SongAdapter @JvmOverloads constructor(
    layoutResId: Int,
    private val offset: Int = 0,
    private val fragmentManager: FragmentManager? = null,
) : BaseQuickAdapter<Song, BaseViewHolder>(layoutResId) {
    private var selectedIndexes = IntArray(0)
    private val downloadActionsUseCase = DownloadActionsUseCase()

    private var editing = false

    override fun convert(holder: BaseViewHolder, item: Song) {
        holder.setText(R.id.index, (holder.layoutPosition + offset).toString())
        holder.setText(R.id.title, item.title)
        holder.setText(R.id.info, item.singer?.nickname.orEmpty())

        if (offset != 0) {
            bindLocalSongMore(holder, item)
        } else {
            bindDownloadStatus(holder, item)
        }

        bindEditingState(holder)
    }

    private fun bindLocalSongMore(holder: BaseViewHolder, data: Song) {
        holder.setImageResource(R.id.more, R.drawable.close)
        holder.getView<android.view.View>(R.id.more).setOnClickListener {
            val manager = fragmentManager ?: return@setOnClickListener
            SuperDialog.newInstance(manager)
                .setTitleRes(R.string.confirm_delete)
                .setOnClickListener {
                    val downloadInfo = data.id?.let { songId ->
                        downloadActionsUseCase.getDownloadById(songId)
                    }
                    if (downloadInfo != null) {
                        downloadActionsUseCase.remove(downloadInfo)
                    } else {
                        AppContext.getInstance().orm.deleteSong(data)
                    }

                    val position = holder.bindingAdapterPosition
                    if (position >= 0) {
                        removeAt(position)
                    }
                }
                .show()
        }
    }

    private fun bindDownloadStatus(holder: BaseViewHolder, data: Song) {
        val downloadInfo = data.id?.let { songId -> downloadActionsUseCase.getDownloadById(songId) }
        holder.setGone(
            R.id.download,
            downloadInfo == null || downloadInfo.status != DownloadInfo.STATUS_COMPLETED,
        )
    }

    private fun bindEditingState(holder: BaseViewHolder) {
        if (isEditing()) {
            holder.setVisible(R.id.index, false)
            holder.setVisible(R.id.check, true)
            holder.setVisible(R.id.more, false)

            val checkIcon = if (isSelected(holder.layoutPosition)) {
                R.drawable.ic_checkbox_selected
            } else {
                R.drawable.ic_checkbox
            }
            holder.setImageResource(R.id.check, checkIcon)
        } else {
            holder.setVisible(R.id.index, true)
            holder.setVisible(R.id.check, false)
            holder.setVisible(R.id.more, true)
        }
    }

    fun isEditing(): Boolean {
        return editing
    }

    fun setEditing(editing: Boolean) {
        this.editing = editing

        if (!editing) {
            selectedIndexes.fill(0)
        }

        notifyDataSetChanged()
    }

    override fun setNewInstance(list: MutableList<Song>?) {
        super.setNewInstance(list)
        selectedIndexes = IntArray(list?.size ?: 0)
    }

    fun isSelected(position: Int): Boolean {
        return position in selectedIndexes.indices && selectedIndexes[position] == 1
    }

    fun setSelected(position: Int, isSelected: Boolean) {
        if (position !in selectedIndexes.indices) {
            return
        }

        selectedIndexes[position] = if (isSelected) 1 else 0
        notifyItemChanged(position)
    }

    fun getSelectedIndexes(): List<Int> {
        val indexes = mutableListOf<Int>()
        selectedIndexes.forEachIndexed { index, selected ->
            if (selected == 1) {
                indexes += index
            }
        }
        return indexes
    }
}
