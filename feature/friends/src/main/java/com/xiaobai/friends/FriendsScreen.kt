package com.xiaobai.friends

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.xiaobai.composable.TopBar
import com.xiaobai.core.DestinationRoute.AUTHENTICATION_ROUTE
import com.xiaobai.theme.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(navController: NavController) {
    Scaffold(topBar = {
        TopBar(
            navIcon = null,
            title = stringResource(id = R.string.friends)
        )
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {

        }
    }

    //在组合完成后执行导航，避免干扰 UI 构建
    LaunchedEffect(key1 = Unit) {//启动一个协程副作用，在组件生命周期内执行一次
        //for now:- (default guest user) redirect to auth
        navController.apply {
            popBackStack()//清除当前导航栈，重定向到登录页面
            navController.navigate(AUTHENTICATION_ROUTE)
        }
    }
}


//组合期间 (Composition Phase)
//定义：Compose 框架分析和构建 UI 树的阶段
//特点：
//执行 @Composable 函数
//创建 UI 组件的层次结构
//确定组件的布局和绘制信息
//此阶段不应执行副作用操作

//渲染期间 (Rendering Phase)
//定义：将组合阶段生成的 UI 树实际绘制到屏幕上的阶段
//特点：
//执行布局计算
//执行绘制操作
//更新屏幕显示内容
//性能敏感阶段，应避免复杂计算


//为什么要区分这两个阶段
//避免副作用干扰：在组合期间直接执行副作用（如导航、网络请求）可能干扰 UI 构建过程
//确保正确时机：使用 LaunchedEffect 等副作用 API 确保操作在合适的时机执行
//性能优化：分离关注点，提高渲染效率



//理解副作用 (Side Effect)
//定义
//副作用是指在函数执行过程中，除了返回值之外对程序状态产生的任何可观察变化。
//Compose 中的副作用特点
//与声明式 UI 的冲突：
//Compose 采用声明式编程范式
//组件应该根据状态渲染 UI，而不应产生副作用
//副作用会破坏 Compose 的可预测性和一致性
//常见副作用类型
//网络请求：获取或发送数据
//数据库操作：读写本地数据
//导航操作：页面跳转
//定时器：延时或周期性任务
//全局状态修改：改变应用级状态
//Compose 的副作用管理机制
//LaunchedEffect：用于执行协程副作用
//DisposableEffect：需要清理的副作用
//rememberCoroutineScope：获取组件生命周期绑定的协程作用域
//当某段逻辑不是“声明 UI”，而是“做事”（如网络请求、打印日志、动画启动、调用系统 API）， 就要考虑使用副作用函数。