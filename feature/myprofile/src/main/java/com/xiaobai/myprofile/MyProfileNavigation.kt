package com.xiaobai.myprofile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.xiaobai.core.DestinationRoute

/**
 * 我的页面导航
 * @param navController 导航控制器
 */
fun NavGraphBuilder.myProfileNavGraph(navController: NavController) {
    composable(route = DestinationRoute.MY_PROFILE_ROUTE) {
        MyProfileScreen(navController)
    }
}