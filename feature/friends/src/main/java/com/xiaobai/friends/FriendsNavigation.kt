package com.xiaobai.friends

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.xiaobai.core.DestinationRoute

/**
 * 朋友页导航
 */
fun NavGraphBuilder.friendsNavGraph(navController: NavController) {
    composable(route = DestinationRoute.FRIENDS_ROUTE) {
        FriendsScreen(navController)
    }
}