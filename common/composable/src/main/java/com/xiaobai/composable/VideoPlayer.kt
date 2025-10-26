package com.xiaobai.composable

import android.graphics.Bitmap
import android.net.Uri
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
import androidx.media3.common.C
import androidx.media3.common.MediaItem
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
 * 视频播放器
 *
 * @param video 视频信息
 * @param pagerState PagerState 对象，用于获取当前页面状态
 * @param pageIndex 当前视频组件所在的页面索引
 * @param onSingleTap 单击事件回调，接收 ExoPlayer 对象作为参数
 * @param onDoubleTap 双击事件回调，接收 ExoPlayer 对象和点击位置偏移量 Offset 作为参数
 * @param onVideoDispose 视频组件销毁时回调，用于释放资源
 * @param onVideoGoBackground 视频组件进入后台时回调，用于暂停播放
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
    onVideoGoBackground: () -> Unit = {}
) {
    //获取当前的 Context 对象
    val context = LocalContext.current
    //缩略图信息
    var thumbnail by remember {
        mutableStateOf<Pair<Bitmap?, Boolean>>(Pair(null, true))  //bitmap, isShow
    }
    //是否已加载首帧
    var isFirstFrameLoad = remember { false }

    //异步截取缩略图
    LaunchedEffect(key1 = true) {
        //IO线程获取视频缩略图
        withContext(Dispatchers.IO) {
            val bitmap = FileUtils.extractThumbnail(
                context.assets.openFd("videos/${video.videoLink}"), 1
            )
            //切回主线程更新缩略图
            withContext(Dispatchers.Main) {
                thumbnail = thumbnail.copy(first = bitmap, second = thumbnail.second)
            }
        }
    }


    //预加载的页面范围：当前页面 - 1 到当前页面 + 1
    if (pagerState.settledPage >= pageIndex - 1 && pagerState.settledPage <= pageIndex + 1) {
        val exoPlayer = remember(video.videoLink) {
            ExoPlayerPool.getPlayer(context, video.videoLink).apply {
                //设置要播放的媒体资源
                setMediaItem(MediaItem.fromUri(Uri.parse("asset:///videos/${video.videoLink}")))
                //设置播放器在当前页面自动开始播放
                playWhenReady = pagerState.settledPage == pageIndex
                //预加载
                prepare()
                addListener(object : Player.Listener {
                    //首帧渲染回调
                    override fun onRenderedFirstFrame() {
                        super.onRenderedFirstFrame()
                        isFirstFrameLoad = true
                        thumbnail = thumbnail.copy(second = false)
                    }
                })
            }
        }

        //获取当前组件的生命周期所有者
        val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
        //监听当前组件的生命周期，确保视频播放器能够正确响应组件的生命周期变化，自动暂停和恢复播放，同时避免资源泄漏。
        DisposableEffect(lifecycleOwner) {
            val lifeCycleObserver = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_STOP -> {
                        exoPlayer.pause()
                        onVideoGoBackground()
                    }

                    Lifecycle.Event.ON_START -> {
                        if (pagerState.settledPage == pageIndex) {
                            exoPlayer.play()
                        }
                    }

                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(lifeCycleObserver)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(lifeCycleObserver)
            }
        }

        //创建播放器视图
        val playerView = remember {
            PlayerView(context).apply {
                //设置播放器
                player = exoPlayer
                //禁用默认的播放控制器（如播放/暂停按钮）
                useController = false
                //设置缩放模式为填充并裁剪
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                //设置布局参数为全屏显
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }

        //在 Compose 中嵌入 PlayerView 并处理用户交互手势
        //管理视频播放器的生命周期，在适当时候释放资源
        //处理单击和双击事件回调
        //确保组件销毁时正确清理所有资源，避免内存泄漏
        DisposableEffect(AndroidView(factory = {
            playerView
        }, modifier = Modifier.pointerInput(Unit) {//启用手势输入处理
            //检测点击手势
            detectTapGestures(onTap = {
                //单击
                onSingleTap(exoPlayer)
            }, onDoubleTap = { offset ->
                //双击
                onDoubleTap(exoPlayer, offset)
            })
        }), effect = {
            onDispose {
                thumbnail = thumbnail.copy(second = true)
                ExoPlayerPool.releasePlayer(video.videoLink, exoPlayer)
                onVideoDispose()
            }
        })


        // 在 ExoPlayer 初始化后添加页面状态监听
        LaunchedEffect(pagerState.settledPage) {
            if (pagerState.settledPage == pageIndex) {
                exoPlayer.playWhenReady = true
                exoPlayer.play()
            } else {
                exoPlayer.playWhenReady = false
                exoPlayer.pause()
            }
        }

    }

    //加载缩略图
    if (thumbnail.second) {
        AsyncImage(
            model = thumbnail.first,
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