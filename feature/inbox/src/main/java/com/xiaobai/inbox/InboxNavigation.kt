package com.xiaobai.inbox

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.xiaobai.core.DestinationRoute.INBOX_ROUTE

fun NavGraphBuilder.inboxNavGraph(navController: NavController) {
    composable(route = INBOX_ROUTE) {
        InboxScreen(navController)
    }
}