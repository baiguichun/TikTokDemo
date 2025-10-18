package com.xiaobai.loginwithemailphone

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.xiaobai.composable.TopBar
import com.xiaobai.loginwithemailphone.tabs.EmailUsernameTabScreen
import com.xiaobai.loginwithemailphone.tabs.PhoneTabScreen
import com.xiaobai.theme.Black
import com.xiaobai.theme.R
import com.xiaobai.theme.SeparatorColor
import com.xiaobai.theme.SubTextColor
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginWithEmailPhoneScreen(
    navController: NavController,
    viewModel: LoginWithEmailPhoneViewModel = hiltViewModel()
) {
    Scaffold(topBar = {//顶部导航栏
        TopBar(
            title = stringResource(id = R.string.login_or_sign_up),
            actions = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_question_circle),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        ) {
            navController.navigateUp()//返回上一页
        }
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)//提供安全区域的内边距，避免内容被系统UI（如状态栏、导航栏）遮挡
        ) {
            LoginPager(viewModel)
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LoginPager(viewModel: LoginWithEmailPhoneViewModel) {
    //页面状态
    val pagerState = rememberPagerState()
    //登录方式
    val pages = LoginPages.values().asList()
    //协程作用域
    val coroutineScope = rememberCoroutineScope()

    //当 pagerState 变化时启动副作用
    LaunchedEffect(key1 = pagerState) {
        //使用 snapshotFlow 监听 pagerState.settledPage 的变化
        //当页面切换完成时，触发 LoginEmailPhoneEvent.EventPageChange 事件通知 ViewModel 更新状态
        snapshotFlow { pagerState.settledPage }.collect {
            viewModel.onTriggerEvent(LoginWithEmailPhoneEvent.EventPageChange(it))
        }
    }
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = {
            val modifier = Modifier
                .tabIndicatorOffset(it[pagerState.currentPage])
                .padding(horizontal = 26.dp)
            TabRowDefaults.Indicator(modifier, color = Black)
        },
        divider = {
            Divider(thickness = 0.5.dp, color = SeparatorColor)
        },
    ) {
        pages.forEachIndexed { index, item ->
            val isSelected = pagerState.currentPage == index
            Tab(
                selected = isSelected,
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                },
                selectedContentColor = Color.Black,
                unselectedContentColor = SubTextColor,
                text = {
                    Text(
                        text = stringResource(id = item.title),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            )
        }
    }

    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerState,
        pageCount = pages.size,
        key = { it }
    ) { page ->
        when (page) {
            0 -> PhoneTabScreen(viewModel)
            1 -> EmailUsernameTabScreen(viewModel)
        }
    }
}

//snapshotFlow 解释
//功能: snapshotFlow 是 Compose 中用于将状态转换为 Flow 的函数
//用途: 监听 Compose 状态变化并将其作为数据流进行处理
//工作原理:
//将 Compose 的快照机制与协程 Flow 结合
//当被观察的状态发生变化时，会发射新的值
//在此代码中的作用:
//监听 pagerState.settledPage 的变化
//当页面切换完成时收集到新的页面索引值
//触发 LoginEmailPhoneEvent.EventPageChange 事件更新 ViewModel 状态
//这是 Compose 中状态监听和响应的标准实现方式。



//pagerState 修改机制
//自动状态管理: pagerState 是通过 rememberPagerState() 创建的，由 Compose 系统自动管理
//用户交互触发: 当用户滑动页面或点击标签页时，pagerState 会自动更新
//程序化控制: 通过 coroutineScope.launch { pagerState.animateScrollToPage(index) } 实现页面切换动画
//状态监听: 使用 snapshotFlow { pagerState.settledPage } 监听页面切换完成事件
//ViewModel 同步: 页面切换完成后通过 viewModel.onTriggerEvent(LoginEmailPhoneEvent.EventPageChange(it)) 通知 ViewModel 更新状态
//这是 Compose 中声明式 UI 的典型状态管理模式，状态变化会自动触发 UI 重组。