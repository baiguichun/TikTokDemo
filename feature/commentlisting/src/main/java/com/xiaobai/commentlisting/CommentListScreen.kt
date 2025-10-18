package com.xiaobai.commentlisting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xiaobai.core.extension.Space
import com.xiaobai.theme.R

/**
 * 评论列表
 * @param onClickCancel 取消按钮点击事件
 * @param viewModel CommentListViewModel
 *
 */
@Composable
fun CommentListScreen(
    viewModel: CommentListViewModel = hiltViewModel(),
    onClickCancel: () -> Unit
){
    val viewState by viewModel.viewState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxHeight(0.75f)
    ){
        12.dp.Space()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // 评论数
            Text(
                text = "${viewState?.comments?.totalComment ?: ""} ${stringResource(id = R.string.comments)}",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.Center)
            )
            //取消按钮
            Icon(
                painter = painterResource(id = R.drawable.ic_cancel),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable {
                        onClickCancel()
                    }
            )
        }
    }










}