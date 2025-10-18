package com.xiaobai.tiktokdemo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.xiaobai.authentication.authenticationNavGraph
import com.xiaobai.cameramedia.cameraMediaNavGraph
import com.xiaobai.commentlisting.commentListingNavGraph
import com.xiaobai.core.DestinationRoute.HOME_SCREEN_ROUTE
import com.xiaobai.creatorprofile.creatorProfileNavGraph
import com.xiaobai.friends.friendsNavGraph
import com.xiaobai.home.homeNavGraph
import com.xiaobai.inbox.inboxNavGraph
import com.xiaobai.loginwithemailphone.loginWithEmailPhoneNavGraph
import com.xiaobai.myprofile.myProfileNavGraph
import com.xiaobai.setting.settingNavGraph

/**
 * 构建App导航图
 *
 * @param navController 导航控制器
 * @param modifier 修饰符
 * @param startDestination 起始导航目标面
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = HOME_SCREEN_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        homeNavGraph(navController)
        commentListingNavGraph(navController)
        creatorProfileNavGraph(navController)
        inboxNavGraph(navController)
        authenticationNavGraph(navController)
        loginWithEmailPhoneNavGraph(navController)
        friendsNavGraph(navController)
        myProfileNavGraph(navController)
        settingNavGraph(navController)
        cameraMediaNavGraph(navController)
    }
}