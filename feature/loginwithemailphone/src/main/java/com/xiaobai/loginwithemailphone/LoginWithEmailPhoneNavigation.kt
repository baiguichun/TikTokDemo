package com.xiaobai.loginwithemailphone

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.xiaobai.core.DestinationRoute

/**
 * 登录邮箱手机号导航
 * @param navController 导航控制器
 */
fun NavGraphBuilder.loginWithEmailPhoneNavGraph(navController: NavController) {
    composable(route = DestinationRoute.LOGIN_OR_SIGNUP_WITH_PHONE_EMAIL_ROUTE) {
        LoginWithEmailPhoneScreen(navController)
    }
}