package com.xiaobai.home.tab.foryou

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.xiaobai.composable.TikTokVerticalVideoPager
import com.xiaobai.core.DestinationRoute.COMMENT_BOTTOM_SHEET_ROUTE
import com.xiaobai.core.DestinationRoute.CREATOR_PROFILE_ROUTE

/**
 * 推荐页面的 UI 界面
 * @param navController 导航控制器，用于页面间导航跳转
 * @param viewModel 视图模型，用于数据处理
 *
 */
@Composable
fun ForYouTabScreen(
    navController: NavController,
    viewModel: ForYouViewModel = hiltViewModel()
) {
    //从 viewModel 中收集状态并将其转换为 Compose 可观察的状态
    val viewState by viewModel.viewState.collectAsState()

    viewState?.forYouPageFeed?.let {
        TikTokVerticalVideoPager(
            videos = it,
            onclickComment = { navController.navigate(COMMENT_BOTTOM_SHEET_ROUTE) },
            onClickLike = { s: String, b: Boolean -> },
            onclickFavourite = {},
            onClickAudio = {},
            onClickUser = { userId -> navController.navigate("$CREATOR_PROFILE_ROUTE/$userId") }
        )
    }
}