package com.xiaobai.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.xiaobai.core.DestinationRoute.HOME_SCREEN_ROUTE

/**
 * 主页导航
 *
 * @param navController
 */
fun NavGraphBuilder.homeNavGraph(navController: NavController) {
    composable(route = HOME_SCREEN_ROUTE) {
        HomeScreen(navController)
    }
}