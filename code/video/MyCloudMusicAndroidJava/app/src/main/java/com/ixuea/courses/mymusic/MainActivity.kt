package com.ixuea.courses.mymusic

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.chat.activity.ChatActivity
import com.ixuea.courses.mymusic.component.ad.model.Ad
import com.ixuea.courses.mymusic.component.conversation.activity.ConversationActivity
import com.ixuea.courses.mymusic.component.discovery.fragment.DiscoveryFragment
import com.ixuea.courses.mymusic.component.download.activity.DownloadActivity
import com.ixuea.courses.mymusic.component.feed.activity.PublishFeedActivity
import com.ixuea.courses.mymusic.component.feed.fragment.FeedFragment
import com.ixuea.courses.mymusic.component.login.activity.LoginHomeActivity
import com.ixuea.courses.mymusic.component.music.activity.LocalMusicActivity
import com.ixuea.courses.mymusic.component.music.activity.ScanLocalMusicActivity
import com.ixuea.courses.mymusic.component.player.fragment.SmallAudioControlPageFragment
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme
import com.ixuea.courses.mymusic.util.Constant

/**
 * Main launcher for the modernized app shell.
 */
class MainActivity : BaseLogicActivity() {
    private var selectedTab by mutableStateOf(MainTab.Discover)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MuseFlowTheme {
                MainHome(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onOpenLocalMusic = { startActivity(LocalMusicActivity::class.java) },
                    onScanLocalMusic = { startActivity(ScanLocalMusicActivity::class.java) },
                    onOpenDownloads = { startActivity(DownloadActivity::class.java) },
                    onOpenMessages = { startActivity(ConversationActivity::class.java) },
                    onPublishFeed = { startActivity(PublishFeedActivity::class.java) },
                    onOpenPlayer = { startMusicPlayerActivity() },
                )
            }
        }
    }

    override fun initDatum() {
        super.initDatum()
        processIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        processIntent(intent)
    }

    private fun processIntent(intent: Intent?) {
        when (intent?.action) {
            Constant.ACTION_LOGIN -> startActivity(LoginHomeActivity::class.java)
            Constant.ACTION_MUSIC_PLAYER_PAGE,
            Constant.ACTION_LYRIC,
            -> startMusicPlayerActivity()

            Constant.ACTION_SCAN -> startActivity(ScanLocalMusicActivity::class.java)
            Constant.ACTION_CHAT -> {
                val id = intent.getStringExtra(Constant.ID)
                if (id.isNullOrBlank()) {
                    startActivity(ConversationActivity::class.java)
                } else {
                    startActivityExtraId(ChatActivity::class.java, id)
                }
            }

            Constant.ACTION_PUSH -> {
                val id = intent.getStringExtra(Constant.PUSH)
                if (id.isNullOrBlank()) {
                    startActivity(ConversationActivity::class.java)
                } else {
                    startActivityExtraId(ChatActivity::class.java, id)
                }
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun processAdClick(_data: Ad?) {
        // Ads are not part of the modernized public feature set.
    }
}

@Composable
private fun MainHome(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    onOpenLocalMusic: () -> Unit,
    onScanLocalMusic: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenMessages: () -> Unit,
    onPublishFeed: () -> Unit,
    onOpenPlayer: () -> Unit,
) {
    Scaffold(
        topBar = {
            MainHeader(
                selectedTab = selectedTab,
                onPublishFeed = onPublishFeed,
                onOpenPlayer = onOpenPlayer,
            )
        },
        bottomBar = {
            MainBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                MainFragmentContent(
                    selectedTab = selectedTab,
                    modifier = Modifier.fillMaxSize(),
                )

                if (selectedTab == MainTab.Library) {
                    ShortcutContent(
                        title = "本地音乐",
                        description = "打开本地曲库、扫描设备音频并继续播放。",
                        primaryText = "打开本地音乐",
                        onPrimaryClick = onOpenLocalMusic,
                        secondaryText = "扫描本地音乐",
                        onSecondaryClick = onScanLocalMusic,
                    )
                }

                if (selectedTab == MainTab.Messages) {
                    ShortcutContent(
                        title = "消息",
                        description = "进入会话列表，继续聊天和查看未读消息。",
                        primaryText = "打开消息",
                        onPrimaryClick = onOpenMessages,
                    )
                }

                if (selectedTab == MainTab.Downloads) {
                    ShortcutContent(
                        title = "下载",
                        description = "查看已下载歌曲和下载中的任务。",
                        primaryText = "打开下载管理",
                        onPrimaryClick = onOpenDownloads,
                    )
                }
            }

            FragmentHost(
                tag = "main_small_audio_control",
                factory = { SmallAudioControlPageFragment() },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp),
            )
        }
    }
}

@Composable
private fun MainBottomNavigation(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MainTab.entries.forEach { tab ->
            MainBottomNavigationItem(
                tab = tab,
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MainBottomNavigationItem(
    tab: MainTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(50.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(14.dp)
                .background(
                    color = if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0f)
                    },
                    shape = RoundedCornerShape(999.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            TabDot(selected = selected)
        }
        Text(
            text = tab.title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
private fun MainHeader(
    selectedTab: MainTab,
    onPublishFeed: () -> Unit,
    onOpenPlayer: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "MuseFlow",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = selectedTab.title,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        if (selectedTab == MainTab.Feed) {
            TextButton(onClick = onPublishFeed) {
                Text("发布")
            }
        }

        TextButton(onClick = onOpenPlayer) {
            Text("播放")
        }
    }
}

@Composable
private fun MainFragmentContent(
    selectedTab: MainTab,
    modifier: Modifier = Modifier,
) {
    val fragmentFactory: (() -> Fragment)? = when (selectedTab) {
        MainTab.Discover -> {
            { DiscoveryFragment.newInstance() }
        }

        MainTab.Feed -> {
            { FeedFragment.newInstance() }
        }

        else -> null
    }

    FragmentHost(
        tag = "main_content_${selectedTab.name}",
        factory = fragmentFactory,
        modifier = modifier,
    )
}

@Composable
private fun FragmentHost(
    tag: String,
    factory: (() -> Fragment)?,
    modifier: Modifier = Modifier,
) {
    val activity = LocalContext.current as FragmentActivity
    val containerId = remember { View.generateViewId() }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            FragmentContainerView(context).apply {
                id = containerId
            }
        },
    )

    LaunchedEffect(tag, containerId) {
        val newFragment = factory?.invoke() ?: return@LaunchedEffect
        if (activity.supportFragmentManager.isStateSaved) {
            return@LaunchedEffect
        }

        activity.supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(containerId, newFragment, tag)
            .commit()
    }
}

@Composable
private fun ShortcutContent(
    title: String,
    description: String,
    primaryText: String,
    onPrimaryClick: () -> Unit,
    secondaryText: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = description,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(
            onClick = onPrimaryClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 14.dp),
        ) {
            Text(primaryText)
        }

        if (secondaryText != null && onSecondaryClick != null) {
            TextButton(
                onClick = onSecondaryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                Text(secondaryText)
            }
        }
    }
}

@Composable
private fun TabDot(
    selected: Boolean,
    size: Dp = 8.dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                },
                shape = CircleShape,
            ),
    )
}

private enum class MainTab(val title: String) {
    Discover("发现"),
    Library("音乐"),
    Feed("动态"),
    Messages("消息"),
    Downloads("下载"),
}
