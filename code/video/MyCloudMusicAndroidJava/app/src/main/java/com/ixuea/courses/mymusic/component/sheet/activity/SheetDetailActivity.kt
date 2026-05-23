package com.ixuea.courses.mymusic.component.sheet.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseTitleActivity
import com.ixuea.courses.mymusic.component.comment.activity.CommentActivity
import com.ixuea.courses.mymusic.component.login.activity.LoginHomeActivity
import com.ixuea.courses.mymusic.component.sheet.adapter.SongAdapter
import com.ixuea.courses.mymusic.component.sheet.domain.NotifySheetChangedUseCase
import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import com.ixuea.courses.mymusic.component.sheet.ui.SheetCollectOperation
import com.ixuea.courses.mymusic.component.sheet.ui.SheetDetailUiState
import com.ixuea.courses.mymusic.component.sheet.ui.SheetDetailViewModel
import com.ixuea.courses.mymusic.component.user.activity.UserDetailActivity
import com.ixuea.courses.mymusic.databinding.ActivitySheetDetailBinding
import com.ixuea.courses.mymusic.databinding.HeaderSheetDetailBinding
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.ImageUtil
import com.ixuea.courses.mymusic.util.ResourceUtil
import com.ixuea.superui.toast.SuperToast
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import timber.log.Timber

/**
 * 歌单详情界面
 */
class SheetDetailActivity :
    BaseTitleActivity<ActivitySheetDetailBinding>(),
    View.OnClickListener {
    private var id: String? = null
    private var data: Sheet? = null
    private lateinit var adapter: SongAdapter
    private lateinit var headerBinding: HeaderSheetDetailBinding
    private lateinit var viewModel: SheetDetailViewModel
    private var deleteMenuItem: MenuItem? = null
    private var handledDataVersion = 0L
    private var handledErrorVersion = 0L
    private var handledCollectEventVersion = 0L
    private var loadingVisible = false
    private val notifySheetChangedUseCase = NotifySheetChangedUseCase()

    override fun initViews() {
        super.initViews()
        QMUIStatusBarHelper.setStatusBarDarkMode(this)
    }

    override fun initDatum() {
        super.initDatum()
        id = if (Intent.ACTION_VIEW == intent.action) {
            intent.data?.getQueryParameter(Constant.ID)
        } else {
            extraId()
        }

        viewModel = ViewModelProvider(this)[SheetDetailViewModel::class.java]
        adapter = SongAdapter(R.layout.item_song)
        adapter.addHeaderView(createHeaderView())
        binding.list.adapter = adapter

        observeSheetState()
        loadData()
    }

    override fun initListeners() {
        super.initListeners()
        headerBinding.collect.setOnClickListener(this)
        adapter.setOnItemClickListener { _, _, position ->
            play(position)
        }
    }

    private fun createHeaderView(): View {
        headerBinding = HeaderSheetDetailBinding.inflate(LayoutInflater.from(hostActivity))

        headerBinding.controlContainer.setOnClickListener {
            play(0)
        }

        headerBinding.userContainer.setOnClickListener {
            val userId = data?.user?.id ?: return@setOnClickListener
            startActivityExtraId(UserDetailActivity::class.java, userId)
        }

        headerBinding.commentContainer.setOnClickListener {
            val sheetId = data?.id ?: return@setOnClickListener
            CommentActivity.startWithSheetId(hostActivity, sheetId)
        }

        return headerBinding.root
    }

    private fun play(position: Int) {
        if (position !in 0 until adapter.itemCount) {
            return
        }

        val song = adapter.getItem(position)
        musicListManager.datum = adapter.data
        musicListManager.play(song)
        startMusicPlayerActivity()
    }

    override fun loadData(isPlaceholder: Boolean) {
        super.loadData(isPlaceholder)
        viewModel.load(id.orEmpty())
    }

    private fun observeSheetState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: SheetDetailUiState) {
        renderLoading(state.isLoading || state.isCollecting)

        if (state.dataVersion != handledDataVersion) {
            handledDataVersion = state.dataVersion
            state.sheet?.let(::showData)
        }

        if (state.collectEventVersion != handledCollectEventVersion) {
            handledCollectEventVersion = state.collectEventVersion
            handleCollectEvent(state.collectOperation)
        }

        if (state.errorVersion != handledErrorVersion) {
            handledErrorVersion = state.errorVersion
            if (state.error != null) {
                Timber.e(state.error, "sheet detail error %s", state.errorMessage)
            } else {
                Timber.e("sheet detail error %s", state.errorMessage)
            }
            state.errorMessage?.takeIf { it.isNotBlank() }?.let(SuperToast::show)
        }
    }

    private fun renderLoading(show: Boolean) {
        if (show == loadingVisible) {
            return
        }

        loadingVisible = show
        if (show) {
            showLoading()
        } else {
            hideLoading()
        }
    }

    private fun showData(data: Sheet) {
        this.data = data
        adapter.setNewInstance(data.songs ?: arrayListOf())

        val icon = data.icon
        if (StringUtils.isBlank(icon)) {
            headerBinding.icon.setImageResource(R.drawable.placeholder)
            setDefaultColor()
        } else {
            loadHeaderIcon(icon.orEmpty())
        }

        headerBinding.title.text = data.title
        ImageUtil.showAvatar(hostActivity, headerBinding.avatar, data.user?.icon)
        headerBinding.nickname.text = data.user?.nickname.orEmpty()
        headerBinding.commentCount.text = data.commentsCount.toString()
        headerBinding.count.text = resources.getString(R.string.music_count, data.songs?.size ?: 0)

        showCollectStatus()
        updateDeleteMenuVisibility()
    }

    private fun loadHeaderIcon(icon: String) {
        val uri = ResourceUtil.resourceUri(icon)
        val glidePalette = GlidePalette
            .with(uri)
            .use(BitmapPalette.Profile.VIBRANT)
            .intoCallBack(
                object : BitmapPalette.CallBack {
                    override fun onPaletteLoaded(palette: Palette?) {
                        val swatch = palette?.vibrantSwatch
                        if (swatch != null) {
                            setHeaderBackground(swatch.rgb)
                        } else {
                            setDefaultColor()
                        }
                    }
                },
            )
            .crossfade(true)

        Glide.with(hostActivity)
            .load(uri)
            .listener(glidePalette)
            .into(headerBinding.icon)
    }

    private fun showCollectStatus() {
        val data = data ?: return
        if (data.isCollect) {
            headerBinding.collect.text = resources.getString(
                R.string.cancel_collect,
                data.collectsCount,
            )
            headerBinding.collect.background = null
            headerBinding.collect.setTextColor(ContextCompat.getColor(this, R.color.black80))
        } else {
            headerBinding.collect.text = resources.getString(
                R.string.collect,
                data.collectsCount,
            )
            headerBinding.collect.setBackgroundColor(ContextCompat.getColor(this, R.color.primary))
            headerBinding.collect.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_sheet_detail, menu)
        deleteMenuItem = menu.findItem(R.id.delete)
        updateDeleteMenuVisibility()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search,
            R.id.sort,
            R.id.report -> true

            R.id.delete -> {
                deleteSheet()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteSheet() {
    }

    private fun setDefaultColor() {
        setHeaderBackground(getColor(R.color.primary))
    }

    private fun setHeaderBackground(color: Int) {
        setStatusBarColor(color)
        toolbar.setBackgroundColor(color)
        headerBinding.header.setBackgroundColor(color)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.collect) {
            processCollectClick()
        }
    }

    private fun processCollectClick() {
        if (!sp.isLogin) {
            startActivity(LoginHomeActivity::class.java)
            return
        }

        val sheet = data ?: return
        viewModel.toggleCollect(sheet)
    }

    private fun handleCollectEvent(operation: SheetCollectOperation) {
        when (operation) {
            SheetCollectOperation.COLLECTED -> SuperToast.success(R.string.collection_success)
            SheetCollectOperation.UNCOLLECTED -> SuperToast.success(R.string.cancel_success)
            SheetCollectOperation.NONE -> return
        }

        showCollectStatus()
        notifySheetChanged()
    }

    private fun updateDeleteMenuVisibility() {
        val ownerId = data?.user?.id
        deleteMenuItem?.isVisible = ownerId != null && ownerId == sp.userId
    }

    private fun notifySheetChanged() {
        notifySheetChangedUseCase()
    }
}
