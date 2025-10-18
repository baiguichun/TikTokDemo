package com.xiaobai.authentication

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.xiaobai.core.DestinationRoute.AUTHENTICATION_ROUTE

/**
 * 登录页底部弹窗导航
 *
 **/
@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.authenticationNavGraph(navController: NavController) {
    bottomSheet(route = AUTHENTICATION_ROUTE) {
        AuthenticationScreen(navController)
    }
}