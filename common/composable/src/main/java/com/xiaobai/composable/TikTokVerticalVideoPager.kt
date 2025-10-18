package com.xiaobai.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.xiaobai.core.extension.Space
import com.xiaobai.core.utils.IntentUtils.share
import com.xiaobai.data.model.VideoModel
import com.xiaobai.theme.Gray20
import com.xiaobai.theme.GrayLight
import com.xiaobai.theme.R
import com.xiaobai.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 垂直视频列表
 *
 * @param modifier 修饰符
 * @param videos 视频列表
 * @param initialPage 初始页面索引
 * @param showUploadDate 是否显示上传时间
 * @param onclickComment 点击评论按钮的回调
 * @param onClickLike 点击喜欢按钮的回调
 * @param onclickFavourite 点击收藏按钮的回调
 * @param onClickAudio 点击音频按钮的回调
 * @param onClickUser 点击用户按钮的回调
 * @param onClickFavourite 点击收藏按钮的回调
 *
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun TikTokVerticalVideoPager(
    modifier: Modifier = Modifier,
    videos: List<VideoModel>,
    initialPage: Int? = 0,
    showUploadDate: Boolean = false,
    onclickComment: (videoId: String) -> Unit,
    onClickLike: (videoId: String, likeStatus: Boolean) -> Unit,
    onclickFavourite: (videoId: String) -> Unit,
    onClickAudio: (VideoModel) -> Unit,
    onClickUser: (userId: Long) -> Unit,
    onClickFavourite: (isFav: Boolean) -> Unit = {},
    onClickShare: (() -> Unit)? = null
) {
    //创建分页器状态，用于跟踪当前页面位置和管理页面切换
    val pagerState = rememberPagerState(initialPage = initialPage ?: 0)
    //创建协程作用域，用于执行异步操作
    val coroutineScope = rememberCoroutineScope()
    //获取当前屏幕密度信息，用于进行 dp 和 px 之间的转换
    val localDensity = LocalDensity.current
    //创建一个默认的 FlingBehavior 对象，用于处理页面滑动
    //lowVelocityAnimationSpec: 配置低速滑动时的动画规范
    val fling = PagerDefaults.flingBehavior(
        state = pagerState, lowVelocityAnimationSpec = tween(
            easing = LinearEasing, durationMillis = 300
        )

    )

    VerticalPager(
        modifier = modifier,
        pageCount = videos.size,//设置页面总数
        state = pagerState,//页面状态，包括当前页面位置和滑动状态
        flingBehavior = fling,//页面滑动行为
        beyondBoundsPageCount = 1,//预加载相邻页面内容，提升滑动体验
    ) {
        //控制暂停按钮的显示/隐藏状态
        var pauseButtonVisibility by remember { mutableStateOf(false) }
        //双击状态
        var doubleTapState by remember {
            mutableStateOf(
                Triple(
                    Offset.Unspecified, //双击触点位置坐标
                    false, //双击动画是否开始
                    0f //旋转角度
                )
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {//填充其父容器的全部可用空间
            //视频播放器
            VideoPlayer(
                videos[it],
                pagerState,
                it,
                onSingleTap = {
                    // 更新暂停按钮的可见性状态
                    pauseButtonVisibility = it.isPlaying
                    //更新播放器的播放状态
                    it.playWhenReady = !it.isPlaying
                },
                onDoubleTap = { exoPlayer, offset ->
                    coroutineScope.launch {
                        //双击设置为喜欢
                        videos[it].currentViewerInteraction.isLikedByYou = true
                        //生成随机旋转角度
                        val rotationAngle = (-10..10).random()
                        //更新 doubleTapState 状态触发点赞动画效果
                        doubleTapState = Triple(offset, true, rotationAngle.toFloat())
                        delay(400)
                        //延迟400ms后重置双击状态，防止重复触发，确保动画完整播放完毕
                        doubleTapState = Triple(offset, false, rotationAngle.toFloat())
                    }
                },
                onVideoDispose = {
                    pauseButtonVisibility = false
                },
                onVideoGoBackground = {
                    pauseButtonVisibility = false
                }
            )

            //底部栏 + 侧边栏
            Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    //底部栏
                    FooterUi(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),//占据剩余空间
                        item = videos[it],
                        showUploadDate = showUploadDate,
                        onClickAudio = onClickAudio,
                        onClickUser = onClickUser,
                    )
                    //侧边栏
                    SideItems(
                        modifier = Modifier,
                        videos[it],
                        doubleTabState = doubleTapState,
                        onclickComment = onclickComment,
                        onClickUser = onClickUser,
                        onClickFavourite = onClickFavourite,
                        onClickShare = onClickShare
                    )
                }
                12.dp.Space()
            }

            //暂停按钮
            AnimatedVisibility(
                visible = pauseButtonVisibility,//可见性控制
                enter = scaleIn(
                    spring(Spring.DampingRatioMediumBouncy),
                    initialScale = 1.5f
                ),//DampingRatioMediumBouncy弹性动画效果，缩放比例为1.5倍
                exit = scaleOut(tween(150)),// 退出动画，使用150ms的补间动画缩小消失
                modifier = Modifier.align(Alignment.Center)//钮居中对齐
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_play),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(36.dp)
                )
            }

            //双击点赞动画
            val iconSize = 110.dp
            AnimatedVisibility(
                visible = doubleTapState.second,
                //进入动画：弹性动画，初始缩放比例1.3倍
                enter = scaleIn(spring(Spring.DampingRatioMediumBouncy), initialScale = 1.3f),
                exit = scaleOut(tween(600), targetScale = 1.58f)
                        + fadeOut(tween(600))
                        + slideOutVertically(
                    tween(600)
                ),
                modifier = Modifier.run {
                    //Offset.Unspecified未指定或无效的坐标偏移量
                    //是否是有效的点击位置
                    if (doubleTapState.first != Offset.Unspecified) {//是有效位置计算动画的中心点
                        this.offset(x = localDensity.run {
                            doubleTapState.first.x.toInt().toDp().plus(-iconSize.div(2))
                        }, y = localDensity.run {
                            doubleTapState.first.y.toInt().toDp().plus(-iconSize.div(2))
                        })
                    } else this
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_like),
                    contentDescription = null,
                    tint = if (doubleTapState.second) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.8f
                    ),
                    modifier = Modifier
                        .size(iconSize)
                        .rotate(doubleTapState.third)
                )
            }
        }
    }
}

/**
 * 侧边按钮
 * @param modifier 修饰符
 * @param item 视频信息
 * @param doubleTabState 双击状态
 * @param onclickComment 点击评论按钮
 * @param onClickUser 点击作者头像
 * @param onClickShare 点击分享按钮
 * @param onClickFavourite 点击收藏按钮
 *
 */
@Composable
fun SideItems(
    modifier: Modifier,
    item: VideoModel,
    doubleTabState: Triple<Offset, Boolean, Float>,
    onclickComment: (videoId: String) -> Unit,
    onClickUser: (userId: Long) -> Unit,
    onClickShare: (() -> Unit)? = null,
    onClickFavourite: (isFav: Boolean) -> Unit
) {
    val context = LocalContext.current
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        //作者头像
        AsyncImage(
            model = item.authorDetails.profilePic,
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .border(
                    BorderStroke(width = 1.dp, color = White), shape = CircleShape
                )
                .clip(shape = CircleShape)
                .clickable {
                    onClickUser.invoke(item.authorDetails.userId)
                },
            contentScale = ContentScale.Crop
        )
        //关注按钮
        Image(
            painter = painterResource(id = R.drawable.ic_plus),
            contentDescription = null,
            modifier = Modifier
                .offset(y = (-10).dp)//在垂直方向上向上偏移10dp
                .size(20.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(5.5.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )
        //插入间隔
        12.dp.Space()
        //是否喜欢
        var isLiked by remember {
            mutableStateOf(item.currentViewerInteraction.isLikedByYou)
        }
        //使用 LaunchedEffect 来监听双击状态变化并更新点赞状态
        LaunchedEffect(key1 = doubleTabState) {
            if (doubleTabState.first != Offset.Unspecified && doubleTabState.second) {
                isLiked = true
            }
        }
        //喜欢按钮
        LikeIconButton(
            isLiked = isLiked,
            likeCount = item.videoStats.formattedLikeCount,
            onLikedClicked = {
                isLiked = it
                item.currentViewerInteraction.isLikedByYou = it
            })
        //评论按钮
        Icon(
            painter = painterResource(id = R.drawable.ic_comment),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(33.dp)
                .clickable {
                    onclickComment(item.videoId)
                })
        Text(
            text = item.videoStats.formattedCommentCount,
            style = MaterialTheme.typography.labelMedium
        )
        //插入间隔
        16.dp.Space()
        //收藏按钮
        Icon(
            painter = painterResource(id = R.drawable.ic_bookmark),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(33.dp)
        )
        Text(
            text = item.videoStats.formattedFavouriteCount,
            style = MaterialTheme.typography.labelMedium
        )
        //插入间隔
        14.dp.Space()
        //分享按钮
        Icon(
            painter = painterResource(id = R.drawable.ic_share),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(32.dp)
                .clickable {
                    onClickShare?.let { onClickShare.invoke() } ?: run {
                        context.share(
                            text = "https://github.com/baiguichun"
                        )
                    }
                }
        )
        Text(
            text = item.videoStats.formattedShareCount, style = MaterialTheme.typography.labelMedium
        )
        //插入间隔
        20.dp.Space()
        //背景音乐
        RotatingAudioView(item.authorDetails.profilePic)
    }


}


/**
 * 点赞按钮
 * @param isLiked 是否点赞
 * @param likeCount 点赞数
 * @param onLikedClicked 点赞点击事件
 */
@Composable
fun LikeIconButton(
    isLiked: Boolean,
    likeCount: String,
    onLikedClicked: (Boolean) -> Unit
) {
    val maxSize = 38.dp
    //点赞动画（属性动画）
    val iconSize by animateDpAsState(
        targetValue = if (isLiked) 33.dp else 32.dp,
        animationSpec = keyframes {//使用关键帧动画
            durationMillis = 400
            24.dp.at(50)//50毫秒时尺寸为24dp
            maxSize.at(190)//190毫秒时尺寸为最大值
            26.dp.at(330)//330毫秒时尺寸为26dp
            32.dp.at(400).with(FastOutLinearInEasing)//400毫秒时尺寸为32dp，使用特定的缓动函数
        }
    )

    Box(
        modifier = Modifier
            .size(maxSize)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) {//禁用默认的点击波纹效果
                onLikedClicked(!isLiked)
            }, contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_heart),
            contentDescription = null,
            tint = if (isLiked) MaterialTheme.colorScheme.primary else Color.White,//根据点赞状态设置图标颜色
            modifier = Modifier.size(iconSize)
        )
    }
    //点赞数
    Text(text = likeCount, style = MaterialTheme.typography.labelMedium)
    16.dp.Space()
}


/**
 * 底部信息
 * @param item 视频信息
 * @param showUploadDate 是否显示上传时间
 * @param onClickAudio 点击音频
 * @param onClickUser 点击用户
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FooterUi(
    modifier: Modifier,
    item: VideoModel,
    showUploadDate: Boolean,
    onClickAudio: (VideoModel) -> Unit,
    onClickUser: (userId: Long) -> Unit,
) {
    //底部对齐排列
    Column(modifier = modifier, verticalArrangement = Arrangement.Bottom) {
        //作者名字
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
            onClickUser(item.authorDetails.userId)
        }) {
            Text(
                text = item.authorDetails.fullName, style = MaterialTheme.typography.bodyMedium
            )
            if (showUploadDate) {
                Text(
                    text = " . ${item.createdAt} ago",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
        //插入间隔
        5.dp.Space()
        //视频描述
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(0.85f)
        )
        //插入间隔
        10.dp.Space()
        //背景音乐信息
        val audioInfo: String = item.audioModel?.run {
            "Original sound - ${audioAuthor.uniqueUserName} - ${audioAuthor.fullName}"
        }
            ?: item.run { "Original sound - ${item.authorDetails.uniqueUserName} - ${item.authorDetails.fullName}" }
        //音频信息
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable {
                onClickAudio(item)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_music_note),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = audioInfo,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .basicMarquee()//启用跑马灯效果 (basicMarquee())，当文本过长时自动滚动
            )
        }

    }

}


/**
 * 旋转音频组件
 * @param img 音频图片
 */
@Composable
fun RotatingAudioView(img: String) {
    //创建了一个无限循环的动画过渡效果
    val infiniteTransition = rememberInfiniteTransition()
    //创建一个旋转动画（属性动画），旋转角度从0度到360度（动画时间7s），并无限重复
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 7000 })
    )
    //创建一个持续旋转的容器
    Box(modifier = Modifier.rotate(angle)) {
        //创建了一个带有渐变背景的圆形头像组件
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            Gray20, Gray20, GrayLight, Gray20, Gray20,
                        )
                    ), shape = CircleShape
                )
                .size(46.dp), contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = img,
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }

}

//remember 的工作机制：
//保持引用：确保在重组时返回同一个对象实例
//不阻止变更：对象内部的值仍然可以发生变化
//状态保持：remember 用于在 Compose 重组时保持对象实例不变，避免每次重组都创建新的状态对象
//性能优化：防止不必要的对象重新创建，提高应用性能
//状态一致性：确保 mutableStateOf 创建的状态对象在多次重组中保持同一实例，维持状态的连续性
//Compose 生命周期管理：与 Compose 的状态管理机制配合工作，确保状态变更能正确触发 UI 更新


//AnimatedVisibility 是 Jetpack Compose 中的一个可组合项，用于控制子组件的显示和隐藏，并提供平滑的进入和退出动画效果。
//主要特性：
//可见性控制：根据 visible 参数的布尔值控制子组件的显示/隐藏
//进入动画：通过 enter 参数定义组件显示时的动画效果
//退出动画：通过 exit 参数定义组件隐藏时的动画效果
//动画状态管理：自动处理组件在显示和隐藏状态之间的过渡动画


//exit = scaleOut(tween(600), targetScale = 1.58f)
//+ fadeOut(tween(600))
//+ slideOutVertically(tween(600)
//scaleOut(tween(600), targetScale = 1.58f): 缩放退出动画
//使用 600ms 的补间动画时间
//将组件缩放到 1.58 倍大小后消失

//fadeOut(tween(600)): 淡出动画
//使用 600ms 的补间动画时间
//逐渐降低组件的透明度直至完全消失

//slideOutVertically(tween(600)): 垂直滑出动画
//使用 600ms 的补间动画时间
//将组件垂直滑出屏幕
//这三种动画同时执行，创造出丰富而生动的退出效果


//modifier = Modifier.run {
//    //Offset.Unspecified未指定或无效的坐标偏移量
//    //是否是有效的点击位置
//    if (doubleTapState.first != Offset.Unspecified) {//是有效位置计算动画的中心点
//        this.offset(x = localDensity.run {
//            doubleTapState.first.x.toInt().toDp().plus(-iconSize.div(2))
//        }, y = localDensity.run {
//            doubleTapState.first.y.toInt().toDp().plus(-iconSize.div(2))
//        })
//    } else this
//}
//这段代码用于根据双击位置动态设置修饰符偏移量：
//使用 Modifier.run 扩展函数来条件性地应用修饰符
//检查 doubleTapState.first 是否为有效的点击位置（不等于 Offset.Unspecified）
//如果有有效的双击位置，则应用 offset 修饰符来定位元素：
//x 坐标：将双击点的 x 坐标转换为 dp，并减去图标大小的一半，使图标中心对齐点击位置
//y 坐标：将双击点的 y 坐标转换为 dp，并减去图标大小的一半
//使用 localDensity 进行像素到 dp 的转换
//如果没有有效的双击位置，则返回原始修饰符
//这是实现双击点赞动画效果的关键逻辑，确保爱心图标出现在用户双击的位置。


//localDensity.run 是一个用于执行与屏幕密度相关转换的扩展函数。
//主要功能：
//提供屏幕密度上下文，便于进行 dp、sp 等单位与像素之间的转换
//在 run 作用域内可以直接调用密度转换相关的方法
//简化密度转换代码的书写
//常用于场景：
//将像素值转换为 dp 值：pxValue.toDp()
//将 dp 值转换为像素值：dpValue.toPx()
//坐标位置的密度适配计算