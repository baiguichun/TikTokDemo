package com.xiaobai.composable

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.xiaobai.core.utils.FileUtils
import com.xiaobai.data.model.VideoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 视频播放器组件 - 支持预加载、错误重试和后台恢复播放
 *
 * @param video 视频信息
 * @param pagerState PagerState 对象，用于获取当前页面状态
 * @param pageIndex 当前视频组件所在的页面索引
 * @param onSingleTap 单击事件回调
 * @param onDoubleTap 双击事件回调
 * @param onVideoDispose 视频销毁回调
 * @param onVideoGoBackground 视频进入后台回调
 * @param onPlaybackError 播放错误回调，参数为错误信息
 */
@OptIn(ExperimentalFoundationApi::class)
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoPlayer(
    video: VideoModel,
    pagerState: PagerState,
    pageIndex: Int,
    onSingleTap: (exoPlayer: ExoPlayer) -> Unit,
    onDoubleTap: (exoPlayer: ExoPlayer, offset: Offset) -> Unit,
    onVideoDispose: () -> Unit = {},
    onVideoGoBackground: () -> Unit = {},
    onPlaybackError: (error: String) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // 缩略图状态 - 使用 videoId 作为 key 确保每个视频独立状态
    var showThumbnail by remember(video.videoId) { mutableStateOf(true) }
    var thumbnailBitmap by remember(video.videoId) { mutableStateOf<Bitmap?>(null) }
    
    // 错误重试状态
    var retryCount by remember(video.videoId) { mutableStateOf(0) }
    var hasError by remember(video.videoId) { mutableStateOf(false) }
    
    // 性能监控：首帧加载时间
    var loadStartTime by remember(video.videoId) { mutableStateOf(0L) }

    // 预加载范围：当前页 ±1
    val isInPreloadRange = pagerState.settledPage in (pageIndex - 1)..(pageIndex + 1)
    
    // 不在预加载范围内，只显示缩略图
    if (!isInPreloadRange) {
        if (showThumbnail && thumbnailBitmap != null) {
            AsyncImage(
                model = thumbnailBitmap,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        return
    }

    // 异步加载缩略图
    LaunchedEffect(video.videoId) {
        showThumbnail = true
        thumbnailBitmap = null
        try {
            val bitmap = withContext(Dispatchers.IO) {
                FileUtils.extractThumbnail(
                    context.assets.openFd("videos/${video.videoLink}"),
                    1
                )
            }
            if (bitmap != null) {
                thumbnailBitmap = bitmap
            } else {
                // 缩略图加载失败，尽快隐藏以显示视频
                showThumbnail = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // 加载失败时隐藏缩略图，让视频尽快显示
            showThumbnail = false
        }
    }

    // 获取或创建 ExoPlayer - 使用 videoId 作为 key
    val exoPlayer = remember(video.videoId) {
        loadStartTime = System.currentTimeMillis()
        ExoPlayerPool.getPlayer(context, video.videoId).apply {
            val currentUri = currentMediaItem?.localConfiguration?.uri?.toString()
            val expectedUri = "asset:///videos/${video.videoLink}"
            
            // 只有当媒体项不匹配时才重新设置
            if (currentUri != expectedUri) {
                setMediaItem(MediaItem.fromUri(Uri.parse(expectedUri)))
                prepare()
            }
            
            // 根据当前页面决定是否播放
            playWhenReady = pagerState.settledPage == pageIndex
        }
    }
    
    // 保持错误回调的最新引用
    val onPlaybackErrorUpdated = rememberUpdatedState(onPlaybackError)

    // 播放器事件监听 - 首帧渲染、错误处理、性能监控
    DisposableEffect(video.videoId, exoPlayer) {
        val listener = object : Player.Listener {
            override fun onRenderedFirstFrame() {
                // 隐藏缩略图
                showThumbnail = false
                hasError = false
                
                // 性能监控：记录首帧加载时间
                if (loadStartTime > 0) {
                    val loadTime = System.currentTimeMillis() - loadStartTime
                    Log.d("VideoPlayer", "视频 ${video.videoId} 首帧加载耗时: ${loadTime}ms")
                    loadStartTime = 0L
                }
            }
            
            override fun onPlayerError(error: PlaybackException) {
                val errorMsg = "视频播放错误: ${error.errorCodeName} - ${error.message}"
                Log.e("VideoPlayer", errorMsg, error)
                
                hasError = true
                
                // 尝试自动重试（最多3次）
                if (retryCount < 3) {
                    retryCount++
                    Log.w("VideoPlayer", "尝试重试播放 (${retryCount}/3): ${video.videoId}")
                    
                    // 延迟重试，避免立即失败
                    exoPlayer.prepare()
                } else {
                    // 超过重试次数，回调错误
                    Log.e("VideoPlayer", "播放失败，已达最大重试次数: ${video.videoId}")
                    onPlaybackErrorUpdated.value(errorMsg)
                    showThumbnail = true // 显示缩略图作为兜底
                }
            }
            
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        Log.d("VideoPlayer", "视频缓冲中: ${video.videoId}")
                    }
                    Player.STATE_READY -> {
                        if (hasError) {
                            // 错误恢复成功
                            Log.i("VideoPlayer", "视频恢复播放成功: ${video.videoId}")
                            hasError = false
                            retryCount = 0
                        }
                    }
                    Player.STATE_ENDED -> {
                        Log.d("VideoPlayer", "视频播放结束: ${video.videoId}")
                    }
                    else -> Unit
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            // 确保移除监听器，避免内存泄漏
            exoPlayer.removeListener(listener)
        }
    }

    // 创建 PlayerView - 使用 videoId 作为 key 确保重新创建时获得新的 Surface
    val playerView = remember(video.videoId) {
        PlayerView(context).apply {
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            player = exoPlayer
        }
    }

    // 保持回调的最新引用
    val onVideoGoBackgroundUpdated = rememberUpdatedState(onVideoGoBackground)
    val onVideoDisposeUpdated = rememberUpdatedState(onVideoDispose)

    // 控制播放/暂停的状态
    var shouldPlay by remember(video.videoId) { mutableStateOf(false) }
    
    // 生命周期监听 - 处理后台切换和 Surface 管理
    DisposableEffect(lifecycleOwner, playerView, exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    // 进入后台时暂停播放
                    Log.d("VideoPlayer", "ON_PAUSE - 暂停播放: ${video.videoId}")
                    if (exoPlayer.isPlaying) {
                        exoPlayer.pause()
                    }
                    shouldPlay = false
                }
                Lifecycle.Event.ON_STOP -> {
                    // 完全停止时断开 Surface 连接，释放资源
                    Log.d("VideoPlayer", "ON_STOP - 断开 Surface: ${video.videoId}")
                    playerView.player = null
                    onVideoGoBackgroundUpdated.value()
                }
                Lifecycle.Event.ON_START -> {
                    // 从后台恢复时重新连接 Surface ⭐ 关键步骤
                    Log.d("VideoPlayer", "ON_START - 重新连接 Surface: ${video.videoId}")
                    if (playerView.player != exoPlayer) {
                        playerView.player = exoPlayer
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    // 恢复前台时，如果是当前页则恢复播放
                    Log.d("VideoPlayer", "ON_RESUME - 恢复前台: ${video.videoId}, 当前页: ${pagerState.settledPage == pageIndex}")
                    if (pagerState.settledPage == pageIndex) {
                        shouldPlay = true
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 页面切换监听 - 控制播放/暂停
    LaunchedEffect(pagerState.settledPage) {
        val isCurrentPage = pagerState.settledPage == pageIndex
        Log.d("VideoPlayer", "页面切换: ${video.videoId}, 是否当前页: $isCurrentPage")
        
        if (isCurrentPage) {
            // 切换到当前页
            val isResumed = lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            if (isResumed) {
                shouldPlay = true
            }
        } else {
            // 切换到其他页，暂停播放
            shouldPlay = false
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
            }
            onVideoGoBackgroundUpdated.value()
        }
    }
    
    // 播放控制逻辑 - 根据 shouldPlay 状态控制播放
    LaunchedEffect(shouldPlay) {
        if (shouldPlay) {
            Log.d("VideoPlayer", "准备播放: ${video.videoId}, 状态: ${exoPlayer.playbackState}")
            when (exoPlayer.playbackState) {
                Player.STATE_READY -> {
                    if (!exoPlayer.isPlaying) {
                        Log.d("VideoPlayer", "开始播放: ${video.videoId}")
                        exoPlayer.play()
                    }
                }
                Player.STATE_IDLE -> {
                    Log.d("VideoPlayer", "准备播放器: ${video.videoId}")
                    exoPlayer.prepare()
                }
                Player.STATE_ENDED -> {
                    Log.d("VideoPlayer", "重新播放: ${video.videoId}")
                    exoPlayer.seekTo(0)
                    exoPlayer.play()
                }
                Player.STATE_BUFFERING -> {
                    Log.d("VideoPlayer", "缓冲中: ${video.videoId}")
                    // 缓冲完成后会自动播放
                }
            }
        } else {
            if (exoPlayer.isPlaying) {
                Log.d("VideoPlayer", "暂停播放: ${video.videoId}")
                exoPlayer.pause()
            }
        }
    }

    // 渲染 PlayerView
    AndroidView(
        factory = { playerView },
        update = { view ->
            // 确保 player 始终正确绑定到 view，避免 Surface 丢失 ⭐ 关键步骤
            // 只在不匹配时才重新绑定，避免不必要的 Surface 重建
            if (view.player != exoPlayer) {
                view.player = exoPlayer
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(video.videoId) {
                detectTapGestures(
                    onTap = { onSingleTap(exoPlayer) },
                    onDoubleTap = { offset -> onDoubleTap(exoPlayer, offset) }
                )
            }
    )

    // 组件销毁时释放资源
    DisposableEffect(video.videoId) {
        onDispose {
            showThumbnail = true
            thumbnailBitmap = null
            // 软释放播放器，保留在池中以供复用
            ExoPlayerPool.softRelease(context, exoPlayer)
            onVideoDisposeUpdated.value()
        }
    }

    // 缩略图遮罩层
    if (showThumbnail && thumbnailBitmap != null) {
        AsyncImage(
            model = thumbnailBitmap,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

//by 委托确保当状态改变时能正确触发 Composable 重组
//直接使用 State 对象无法自动触发重组


//LaunchedEffect(key1=true)
//LaunchedEffect: 是 Jetpack Compose 提供的副作用函数，用于在协程中执行副作用操作
//一次性执行: 由于键值固定为 true，该效果只会在组件首次加载时执行一次
//生命周期管理: 当组件被移除时，协程会自动取消
//适合场景: 适用于只需要执行一次的初始化操作，如数据加载、资源初始化等


//remember(context) 与 remember 的区别
//1. 键值依赖
//remember: 无参数，仅在首次组合时创建并记忆对象
//remember(context): 以 context 作为键值，当 context 发生变化时会重新创建对象
//2. 重新创建条件
//remember: 只有在组件首次加载或作用域重建时才会重新创建
//remember(context): 当传入的 context 引用发生变化时也会重新创建
//3. 使用场景
//remember: 适用于不依赖外部变化的稳定对象
//remember(context): 适用于需要响应 context 变化的对象，如需要 context 的播放器实例


//rememberUpdatedState 函数来获取并记忆当前的生命周期所有者：
//LocalLifecycleOwner.current: 获取当前 Composable 的 LifecycleOwner 实例
//rememberUpdatedState: 记住并自动更新状态值，当 LocalLifecycleOwner.current 变化时会自动更新
//by 委托: 使用属性委托语法，使得 lifecycleOwner 变量始终指向最新的 LifecycleOwner 实例
//主要作用：
//生命周期感知: 获取当前组件的生命周期所有者，用于监听组件的生命周期变化
//自动更新: 当 LifecycleOwner 发生变化时，lifecycleOwner 变量会自动更新为最新值
//避免过时引用: 防止持有过时的 LifecycleOwner 引用，确保始终使用最新的生命周期信息
//这种模式常用于需要在 Composable 中监听生命周期变化的场景，比如暂停/恢复视频播放等操作。

//DisposableEffect 是 Jetpack Compose 提供的副作用管理函数
//专门用于处理需要清理资源的副作用操作
//当键值发生变化或组件被移除时，会自动执行清理操作