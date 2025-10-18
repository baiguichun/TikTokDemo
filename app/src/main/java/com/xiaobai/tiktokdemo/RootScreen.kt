package com.xiaobai.tiktokdemo

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.xiaobai.core.DestinationRoute.AUTHENTICATION_ROUTE
import com.xiaobai.core.DestinationRoute.CAMERA_ROUTE
import com.xiaobai.core.DestinationRoute.COMMENT_BOTTOM_SHEET_ROUTE
import com.xiaobai.core.DestinationRoute.FORMATTED_COMPLETE_CREATOR_VIDEO_ROUTE
import com.xiaobai.core.DestinationRoute.FRIENDS_ROUTE
import com.xiaobai.core.DestinationRoute.HOME_SCREEN_ROUTE
import com.xiaobai.core.DestinationRoute.INBOX_ROUTE
import com.xiaobai.core.DestinationRoute.MY_PROFILE_ROUTE
import com.xiaobai.theme.TikTokTheme
import com.xiaobai.tiktokdemo.component.BottomBar
import com.xiaobai.tiktokdemo.navigation.AppNavHost


@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialNavigationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun RootScreen() {
    //创建并记忆底部弹窗导航器实例
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    // 创建导航控制器，并将底部弹窗导航器作为参数传入，使导航控制器能够处理底部弹窗导航
    val navController = rememberNavController(bottomSheetNavigator)
    // 通过导航控制器获取当前回退栈条目的状态，用于监听导航状态变化
    val currentBackStackEntryAsState by navController.currentBackStackEntryAsState()
    //从当前回退栈条目中提取目标页面信息，用于判断当前所在页面
    val currentDestination = currentBackStackEntryAsState?.destination
    //Android 上下文
    val context= LocalContext.current

    //根据当前目标页面路由判断是否显示底部导航栏
    val isShowBottomBar = when (currentDestination?.route) {
        HOME_SCREEN_ROUTE, INBOX_ROUTE, COMMENT_BOTTOM_SHEET_ROUTE,
        FRIENDS_ROUTE, AUTHENTICATION_ROUTE, MY_PROFILE_ROUTE, null -> true
        else -> false
    }
    //根据当前目标页面路由判断是否使用深色主题
    val darkMode = when (currentDestination?.route) {
        HOME_SCREEN_ROUTE, FORMATTED_COMPLETE_CREATOR_VIDEO_ROUTE, CAMERA_ROUTE, null -> true
        else -> false
    }

    // 判断当前页面是否为首页，在首页按返回键退出应用
    if(currentDestination?.route== HOME_SCREEN_ROUTE){
        //注册返回键处理回调
        BackHandler {
            (context as? Activity)?.finish()
        }
    }

    TikTokTheme(darkTheme = darkMode) {
        SetupSystemUi(
            rememberSystemUiController(),
            MaterialTheme.colorScheme.background
        )
        //提供底部弹窗布局容器，用于显示底部弹窗页面
        //传入底部弹窗导航器，用于管理弹窗页面导航
        ModalBottomSheetLayout(bottomSheetNavigator = bottomSheetNavigator) {
            Scaffold(
                topBar = {

                },
                bottomBar = {
                    if (!isShowBottomBar) {
                        return@Scaffold
                    }
                    BottomBar(navController, currentDestination, isDarkTheme = darkMode)
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    AppNavHost(navController = navController)
                }
            }
        }


    }
}


/**
 * 设置系统UI
 * @param systemUiController 系统UI控制器
 * @param systemBarColor 系统栏颜色
 * 主题一致性：MaterialTheme.colorScheme.background 确保系统UI颜色与应用主题背景色保持一致，提供沉浸式体验
 * 动态适配：当应用主题切换时（如深色/浅色模式），系统栏颜色会自动适配新的主题背景色
 * 可维护性：通过 SetupSystemUi 封装系统UI设置逻辑，便于统一管理和复用
 * 副作用处理：使用 SideEffect 确保系统UI设置操作在合适的时机执行，不会干扰UI构建过程
 * 这种设计使应用能够根据当前主题动态调整系统栏颜色，提升用户体验。
 */
@Composable
fun SetupSystemUi(
    systemUiController: SystemUiController,
    systemBarColor: Color
) {
    SideEffect {//SideEffect 用来在 每次成功重组（recomposition）之后用于设置系统UI颜色
        systemUiController.setSystemBarsColor(color = systemBarColor)
    }
}


/**
 * 创建底部弹窗导航器
 * @param animationSpec 动画
 * @param skipHalfExpanded 是否跳过半展开
 */
@ExperimentalMaterialNavigationApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberBottomSheetNavigator(
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    skipHalfExpanded: Boolean = true,
): BottomSheetNavigator {
    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,//初始状态隐藏
        animationSpec,
        skipHalfExpanded,
    )
    return remember(sheetState) {
        BottomSheetNavigator(sheetState = sheetState)
    }
}