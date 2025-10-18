package com.xiaobai.creatorprofile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.xiaobai.core.DestinationRoute.COMMENT_BOTTOM_SHEET_ROUTE
import com.xiaobai.core.DestinationRoute.CREATOR_PROFILE_ROUTE
import com.xiaobai.core.DestinationRoute.FORMATTED_COMPLETE_CREATOR_VIDEO_ROUTE
import com.xiaobai.core.DestinationRoute.PassedKey.USER_ID
import com.xiaobai.core.DestinationRoute.PassedKey.VIDEO_INDEX
import com.xiaobai.creatorprofile.screen.creatorprofile.CreatorProfileScreen
import com.xiaobai.creatorprofile.screen.creatorvideo.CreatorVideoPagerScreen

fun NavGraphBuilder.creatorProfileNavGraph(navController: NavController) {
    //注册创作者个人页面路由
    composable(route = "$CREATOR_PROFILE_ROUTE/{$USER_ID}",
        arguments = listOf(
            navArgument(USER_ID) { type = NavType.LongType }
        )
    ) {
        CreatorProfileScreen(
            onClickNavIcon = { navController.navigateUp() },
            navController = navController
        )
    }

    //注册创作者视频页面路由
    composable(route = FORMATTED_COMPLETE_CREATOR_VIDEO_ROUTE,
        arguments = listOf(
            navArgument(USER_ID) { type = NavType.LongType },
            navArgument(VIDEO_INDEX) { type = NavType.IntType }
        )
    ) {
        CreatorVideoPagerScreen(
            onClickNavIcon = { navController.navigateUp() },
            onclickComment = { navController.navigate(COMMENT_BOTTOM_SHEET_ROUTE) },
            onClickAudio = {},
            onClickUser = { navController.navigateUp() }
        )
    }
}