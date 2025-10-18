package com.xiaobai.creatorprofile.component

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.xiaobai.core.utils.FileUtils
import com.xiaobai.data.model.VideoModel
import com.xiaobai.theme.GrayMainColor
import com.xiaobai.theme.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 视频网格，使用外部拦截法处理滑动冲突
 *
 * @param scrollState 父级滚动组件的状态管理器
 * @param videoList 视频列表
 * @param onClickVideo 点击视频回调
 */
@Composable
fun VideoGrid(
    scrollState: ScrollState,
    videoList: List<VideoModel>,
    onClickVideo: (video: VideoModel, index: Int) -> Unit
) {
    //当前上下文
    val context = LocalContext.current
    LazyVerticalGrid(//使用垂直网格布局组件
        columns = GridCells.Fixed(3),//设置固定3列网格布局
        modifier = Modifier
            .height(1000.dp)//设置网格高
            .nestedScroll(//添加嵌套滚动处理，与外部滚动组件协调工作
                remember {//记忆 NestedScrollConnection 对象实例，避免重组时重复创建
                    object : NestedScrollConnection {
                        override fun onPreScroll(//在滚动发生前进行拦截和处理
                            available: Offset,//可用的滚动偏移量
                            source: NestedScrollSource//滚动来源
                        ): Offset {
                            //当 available.y > 0（向下滚动）时，返回 Offset.Zero 不消费滚动事件
                            //否则计算并返回需要消费的滚动偏移量
                            return if (available.y > 0) Offset.Zero else
                                Offset(
                                    x = 0f,
                                    y = -scrollState.dispatchRawDelta(-available.y)
                                )
                        }
                    }
                }
            ),
        horizontalArrangement = Arrangement.spacedBy(1.dp),//上下网格项之间的间距为1dp
        verticalArrangement = Arrangement.spacedBy(1.dp),//左右网格项之间的间距为1dp
        content = {//显示网格项
            itemsIndexed(videoList) { index, item ->
                context.VideoGridItem(item, index, onClickVideo = onClickVideo)
            }
        })
}



/**
 * 视频网格项
 *
 * @param item 视频模型
 * @param index 当前索引
 * @param onClickVideo 点击视频回调
 */
@Composable
fun Context.VideoGridItem(
    item: VideoModel,
    index: Int,
    onClickVideo: (VideoModel, Int) -> Unit) {
    Box(
        modifier = Modifier
            .height(160.dp)
            .clickable {
                onClickVideo(item, index)
            }
    ) {
        //缩略图
        var thumbnail: Bitmap? by remember {
            mutableStateOf(null)
        }
        AsyncImage(
            model = thumbnail,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .background(GrayMainColor)//设置背景色为灰色主色调，作为图像加载前的占位色
                .drawWithContent {//自定义绘制内容
                    drawContent()//绘制原始图像内容
                    drawRect(
                        brush = Brush.verticalGradient(//使用垂直渐变画笔
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.5f)//渐变颜色从透明到半透明黑色
                            ), startY = 300f//渐变开始的Y坐标位置
                        )
                    )
                },
            contentScale = ContentScale.Crop//图像缩放模式设置为裁剪填充，保持宽高比的同时填满整个显示区域
        )
        LaunchedEffect(key1 = true) {
            //IO线程截取缩略图
            withContext(Dispatchers.IO) {
                val bitmap = FileUtils.extractThumbnail(
                    assets.openFd("videos/${item.videoLink}"), 1
                )
                //主线程更新
                withContext(Dispatchers.Main) {
                    thumbnail = bitmap
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                painterResource(id = R.drawable.ic_play_outline),
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = Color.Unspecified
            )
            Text(
                text = item.videoStats.formattedViewsCount,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
        }

    }
}

//Android系统限制: 在 Compose 环境中访问 Android 系统资源（如 assets）需要通过 Context 对象
//Context 提供了访问应用资源和系统服务的入口。


//y = -scrollState.dispatchRawDelta(-available.y)
//这段代码是嵌套滚动机制中处理垂直方向滚动偏移量的核心逻辑：
//available.y: 表示当前可滚动的垂直偏移量
//-available.y: 对偏移量取反，因为滚动方向与坐标系方向相反
//scrollState.dispatchRawDelta(-available.y): 将处理后的滚动偏移量分发给父级滚动状态管理器，虽然调用了父级的 dispatchRawDelta，但这是为了让父级知道内部组件需要向上滚动，从而实现内部内容的滚动效果。
//y = -scrollState.dispatchRawDelta(-available.y): 将返回值再次取反，确保正确的滚动方向
//这种双重取反的操作是为了：
//正确处理滚动方向的转换
//让父级滚动组件能够消费相应的滚动距离
//实现内部网格和外部滚动容器的协调滚动效果
//这是解决嵌套滚动冲突的标准实现方式。


//滚动策略逻辑
//向下滚动时 (available.y > 0)：
//返回 Offset.Zero，不消费滚动事件
//让父级滚动容器优先处理向下滚动
//确保用户可以先滚动到页面顶部
//向上滚动时 (available.y <= 0)：
//计算并消费滚动偏移量：y = -scrollState.dispatchRawDelta(-available.y)
//主动将滚动事件分发给 scrollState 处理
//实现内部组件的向上滚动行为
//设计目的
//这种策略实现了父级优先的嵌套滚动机制：
//优先级控制：父级容器优先处理向下滚动，内部组件优先处理向上滚动
//滚动协调：避免内部 LazyVerticalGrid 和外部可滚动容器之间的滚动冲突
//用户体验：确保用户在浏览内容时能够获得流畅自然的滚动体验