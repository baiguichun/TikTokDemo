package com.xiaobai.home.tab.following.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.xiaobai.composable.VideoPlayer
import com.xiaobai.core.extension.Space
import com.xiaobai.data.model.ContentCreatorFollowingModel
import com.xiaobai.theme.R
import com.xiaobai.theme.White
import com.xiaobai.theme.WhiteAlpha95
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun CreatorCard(
    page: Int,
    pagerState: PagerState,
    item: ContentCreatorFollowingModel,
    onClickFollow: (userId: Long) -> Unit,
    onClickUser: (userId: Long) -> Unit
){
    //计算页面偏移
    //pagerState.currentPageOffsetFraction表示从当前页面滑动到下一页的进度比例0～1
    val pageOffset =
        ((pagerState.currentPage - page) + (pagerState.currentPageOffsetFraction)).absoluteValue

    Card(
        modifier = Modifier
            .graphicsLayer {
                lerp(
                    start = 0.9f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                ).also { scale ->
                    scaleX = scale
                    scaleY = scale
                }
            },
        shape = RoundedCornerShape(8.dp)
    ){
        Box(
            //绘制一个动态遮罩
            Modifier
            .drawWithCache {//自定义组件的绘制逻辑，并提供缓存机制以提高性能
                onDrawWithContent {//在内容绘制完成后执行自定义绘制操作
                    drawContent()//首先绘制原始内容
                    val color: Color = lerp(
                        Color.Black.copy(alpha = 0.59f),
                        Color.Transparent,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                    drawRect(color)//绘制一个覆盖整个组件区域的矩形
                }
            }
            .height(340.dp)
        ){
            //播放视频
            VideoPlayer(video = item.coverVideo,
                pagerState = pagerState,
                pageIndex = page,
                onSingleTap = {
                    onClickUser(item.userModel.userId)
                },
                onDoubleTap = { exoPlayer: ExoPlayer, offset: Offset -> },
                onVideoDispose = {},
                onVideoGoBackground = {})

            //取消按钮
            Icon(
                painterResource(id = R.drawable.ic_cancel),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier
                    .align(Alignment.TopEnd)//父容器右上角
                    .padding(12.dp)
            )

            //作者信息
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                //头像
                AsyncImage(
                    model = item.userModel.profilePic,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .size(70.dp)
                        .border(
                            BorderStroke(width = 1.dp, color = White), shape = CircleShape
                        )
                        .clip(shape = CircleShape),
                    contentScale = ContentScale.Crop
                )
                //全名
                Text(
                    text = item.userModel.fullName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                //用户名
                Text(
                    text = "@${item.userModel.uniqueUserName}",
                    style = MaterialTheme.typography.labelMedium,
                    color = WhiteAlpha95
                )
                //关注按钮
                Button(
                    onClick = {
                        onClickFollow(item.userModel.userId)
                    }, modifier = Modifier
                        .padding(top = 2.dp)
                        .padding(horizontal = 36.dp)
                        .fillMaxWidth(), shape = RoundedCornerShape(2.dp)
                ) {
                    Text(text = stringResource(id = R.string.follow))
                }
                12.dp.Space()
            }
            20.dp.Space()
        }
    }
}

// modifier = Modifier
//            .graphicsLayer {
//                lerp(
//                    start = 0.9f,
//                    stop = 1f,
//                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
//                ).also { scale ->
//                    scaleX = scale
//                    scaleY = scale
//                }
//            }
//核心功能
//动态缩放：根据 pageOffset 值在 0.9f 到 1.0f 之间插值计算缩放比例
//视觉层次：当前页面保持正常大小(1.0倍)，相邻页面缩小到 0.9 倍
//代码详解
//graphicsLayer 修饰符：
//用于应用图形变换效果
//允许对组件进行缩放、旋转等变换
//lerp 插值计算：
//start = 0.9f：最小缩放比例
//stop = 1f：最大缩放比例
//fraction = 1f - pageOffset.coerceIn(0f, 1f)：插值系数
//当 pageOffset = 0 时，fraction = 1，scale = 1.0（当前页面）
//当 pageOffset = 1 时，fraction = 0，scale = 0.9（相邻页面）
//coerceIn(0f, 1f)：
//确保 pageOffset 值在 0 到 1 范围内
//防止超出边界导致的异常缩放
//应用缩放：
//将计算出的缩放值同时应用到 scaleX 和 scaleY
//实现等比例缩放效