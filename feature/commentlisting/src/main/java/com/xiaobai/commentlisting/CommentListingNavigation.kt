package com.xiaobai.commentlisting

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.xiaobai.core.DestinationRoute.COMMENT_BOTTOM_SHEET_ROUTE

/**
 * 在导航图中添加评论列表的底部弹窗界面
 *
 */
@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.commentListingNavGraph(navController: NavController) {
    bottomSheet(route = COMMENT_BOTTOM_SHEET_ROUTE) {
        CommentListScreen(onClickCancel = { navController.navigateUp() })
    }
}