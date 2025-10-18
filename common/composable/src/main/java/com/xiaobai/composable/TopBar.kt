package com.xiaobai.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.xiaobai.theme.R

/**
 * 顶部导航栏
 * @param navIcon 导航图标
 * @param title 标题
 * @param backgroundColor 背景颜色
 * @param actions 右侧操作
 * @param onClickNavIcon 点击导航图标
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    @DrawableRes navIcon: Int? = R.drawable.ic_arrow_back,
    title: String? = null,
    backgroundColor: Color = Color.Transparent,
    actions: @Composable RowScope.() -> Unit = {},
    onClickNavIcon: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            title?.let {
                Text(text = title, style = MaterialTheme.typography.headlineMedium)
            }
        },
        navigationIcon = {
            navIcon?.let {
                Icon(
                    painter = painterResource(id = navIcon),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable { onClickNavIcon() }
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = backgroundColor
        ),
        actions = {
            actions()
        }
    )
}