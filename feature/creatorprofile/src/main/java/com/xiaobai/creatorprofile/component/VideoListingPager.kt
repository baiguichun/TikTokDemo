package com.xiaobai.creatorprofile.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaobai.creatorprofile.screen.creatorprofile.CreatorProfileViewModel
import com.xiaobai.creatorprofile.screen.creatorprofile.ProfilePagerTabs
import com.xiaobai.creatorprofile.screen.creatorprofile.tabs.LikeVideoTab
import com.xiaobai.creatorprofile.screen.creatorprofile.tabs.PublicVideoTab
import com.xiaobai.data.model.VideoModel
import com.xiaobai.theme.Black
import com.xiaobai.theme.Gray
import com.xiaobai.theme.SeparatorColor
import kotlinx.coroutines.launch

/**
 * 根据标签展示不同的视频列表
 *
 * @param scrollState 父级滚动组件的状态管理器
 * @param height 视频列表高度
 * @param viewModel 视频列表数据模型
 * @param onClickVideo 点击视频回调
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoListingPager(
    scrollState: ScrollState,
    height: Dp,
    viewModel: CreatorProfileViewModel,
    onClickVideo: (video: VideoModel, index: Int) -> Unit
) {
    //页面状态
    val pagerState = rememberPagerState()
    //标签列表
    val tabs = ProfilePagerTabs.values().asList()
    //协程作用域
    val coroutineScope = rememberCoroutineScope()
    //屏幕宽度
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    LazyColumn(modifier = Modifier
        .height(height)
        .nestedScroll(//嵌套滚动
            remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        return if (available.y > 0) Offset.Zero else
                            Offset(x = 0f, y = -scrollState.dispatchRawDelta(-available.y))
                    }
                }
            }
        )) {
        //标签
        item {
            TabRow(
                selectedTabIndex = pagerState.currentPage,//设置当前选中的标签页索引
                indicator = {//自定义选中标签的指示器样式
                    val modifier = Modifier
                        .tabIndicatorOffset(it[pagerState.currentPage])
                        .padding(horizontal = screenWidth.div(5.5f))

                    TabRowDefaults.Indicator(
                        modifier, color = Black
                    )
                },
                divider = {
                    Divider(thickness = 0.5.dp, color = SeparatorColor)
                },
            ) {
                tabs.forEachIndexed { index, item ->
                    val isSelected = pagerState.currentPage == index

                    Tab(
                        selected = isSelected,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        selectedContentColor = Color.Transparent,
                        icon = {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = if (isSelected) Black else Gray
                            )
                        }
                    )
                }
            }
        }
        //标签对应的内容
        item {
            HorizontalPager(
                pageCount = tabs.size, state = pagerState
            ) {
                when (it) {
                    0 -> {
                        PublicVideoTab(viewModel, scrollState, onClickVideo = onClickVideo)
                    }
                    1 -> {
                        LikeVideoTab(viewModel)
                    }
                }
            }
        }

    }
}

//主要功能
//创建可滑动的标签页界面，包含"公开视频"和"喜欢的视频"两个标签页
//实现嵌套滚动处理，与外部滚动组件协调工作
//根据选中的标签页显示对应的内容