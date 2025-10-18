package com.xiaobai.creatorprofile.screen.creatorvideo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import com.xiaobai.composable.ContentSearchBar
import com.xiaobai.composable.TikTokVerticalVideoPager
import com.xiaobai.data.model.VideoModel
import com.xiaobai.theme.R
import com.xiaobai.theme.SubTextColor


/**
 * 创建者视频列表
 *
 * @param onClickNavIcon 点击返回按钮回调
 * @param onclickComment 点击评论按钮回调
 * @param onClickAudio 点击音频按钮回调
 * @param onClickUser 点击用户按钮回调
 * @param viewModel 视频列表 ViewModel
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorVideoPagerScreen(
    onClickNavIcon: () -> Unit,
    onclickComment: (videoId: String) -> Unit,
    onClickAudio: (VideoModel) -> Unit,
    onClickUser: (userId: Long) -> Unit,
    viewModel: CreatorVideoPagerViewModel = hiltViewModel()
) {
    val viewState by viewModel.viewState.collectAsState()

    Scaffold(//用于构建包含顶部栏、内容区域等标准布局元素的界面
        topBar = {//顶部搜索栏
            ContentSearchBar(
                onClickNav = onClickNavIcon,
                onClickSearch = {},
                placeholder = stringResource(id = R.string.find_related_content)
            )
        }
    ) {
        Column(
            modifier = Modifier
                //在底部添加内边距，it 指代 ScaffoldState，
                //通过 calculateBottomPadding() 计算底部安全区域 padding 值，
                //避免内容被系统 UI（如导航栏）遮挡
                .padding(bottom = it.calculateBottomPadding())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            viewState?.creatorVideosList?.let {
                TikTokVerticalVideoPager(
                    videos = it,
                    showUploadDate = true,
                    onclickComment = onclickComment,
                    onClickLike = { s: String, b: Boolean -> },
                    onclickFavourite = {},
                    onClickAudio = onClickAudio,
                    onClickUser = onClickUser,
                    initialPage = viewModel.videoIndex,
                    modifier = Modifier.weight(1f)
                )
                //分割线
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 0.5.dp,
                    color = Color.White.copy(0.2f)
                )
                //评论区域
                OutlinedTextField(value = "",//初始值为空字符串
                    onValueChange = {},//值变化回调
                    placeholder = {//占位符文本
                        Text(text = stringResource(R.string.add_comment), color = SubTextColor)
                    },
                    //输入框颜色配置
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.Black,//容器背景为黑色
                        unfocusedBorderColor = Color.Transparent//失去焦点时边框为透明
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(46.dp),
                    trailingIcon = {//尾部图标区域
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(18.dp),
                            modifier = Modifier.padding(end = 12.dp, start = 2.dp)
                        ) {
                            //@图标
                            Icon(
                                painter = painterResource(id = R.drawable.ic_mention),
                                contentDescription = null
                            )
                            //表情包图标
                            Icon(
                                painter = painterResource(id = R.drawable.ic_emoji),
                                contentDescription = null
                            )
                        }

                    }

                )

            }

        }

    }
}
