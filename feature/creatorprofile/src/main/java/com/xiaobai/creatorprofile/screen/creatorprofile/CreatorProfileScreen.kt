package com.xiaobai.creatorprofile.screen.creatorprofile

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.xiaobai.composable.TopBar
import com.xiaobai.core.AppContract.Type.INSTAGRAM
import com.xiaobai.core.AppContract.Type.YOUTUBE
import com.xiaobai.core.DestinationRoute.CREATOR_VIDEO_ROUTE
import com.xiaobai.core.utils.IntentUtils.redirectToApp
import com.xiaobai.creatorprofile.component.VideoListingPager
import com.xiaobai.data.model.SocialMediaType
import com.xiaobai.data.model.UserModel
import com.xiaobai.theme.Gray
import com.xiaobai.theme.R
import com.xiaobai.theme.SubTextColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorProfileScreen(
    onClickNavIcon: () -> Unit,
    navController: NavController,
    viewModel: CreatorProfileViewModel = hiltViewModel()
) {
    val viewState by viewModel.viewState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(//用于构建包含顶部栏、内容区域等标准布局元素的界面
        topBar = {
            TopBar(//简介栏
                onClickNavIcon = onClickNavIcon,
                title = viewState?.creatorProfile?.fullName ?: "",
                actions = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more_vert),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { })
                })
        }
    ) {
        BoxWithConstraints {//提供约束布局容器，它会自动测量并提供可用空间的约束信息
            val height = this.maxHeight//通过 BoxWithConstraintsScope 自动获取当前容器的最大可用高度
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                //个人资料
                ProfileDetails(viewState?.creatorProfile)
                //标签视频列表
                VideoListingPager(scrollState = scrollState, height = height, viewModel,
                    onClickVideo = { video, index ->
                        navController.navigate("$CREATOR_VIDEO_ROUTE/${viewModel.userId}/$index")
                    }
                )
            }
        }

    }
}

/**
 * 个人资料
 */
@Composable
fun ColumnScope.ProfileDetails(creatorProfile: UserModel?) {
    val context = LocalContext.current
    //头像
    AsyncImage(
        model = creatorProfile?.profilePic,
        contentDescription = null,
        modifier = Modifier
            .size(94.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        //用户名
        Text(
            text = "@${creatorProfile?.uniqueUserName}",
            style = MaterialTheme.typography.bodyMedium
        )
        //是否是认证用户
        if (creatorProfile?.isVerified == true) {
            Icon(
                painter = painterResource(id = R.drawable.ic_verified),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }

    }
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        //创建多个约束引用
        //每个组件通过 Modifier.constrainAs() 与对应的引用关联
        val (
            followingCount, tvFollowing, separator1,
            followersCount, tvFollowers, separator2, likeCount, tvLike,
        ) = createRefs()

        //关注数
        Text(
            text = creatorProfile?.formattedFollowingCount ?: "-",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.constrainAs(followingCount) {
                top.linkTo(parent.top)
                centerHorizontallyTo(tvFollowing)
            }
        )
        Text(
            text = stringResource(id = R.string.following),
            color = SubTextColor,
            modifier = Modifier.constrainAs(tvFollowing) {
                top.linkTo(followingCount.bottom, margin = 3.dp)
                start.linkTo(parent.start, margin = 36.dp)
                end.linkTo(separator1.start)
            }
        )

        Divider(modifier = Modifier
            .width(1.dp)
            .height(16.dp)
            .constrainAs(separator1) {
                start.linkTo(tvFollowing.end)
                end.linkTo(tvFollowers.start)
                bottom.linkTo(tvFollowing.bottom)
                top.linkTo(followingCount.top)
            })

        //粉丝数
        Text(
            text = creatorProfile?.formattedFollowersCount ?: "-",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.constrainAs(followersCount) {
                top.linkTo(followingCount.top)
                centerHorizontallyTo(tvFollowers)
            }
        )

        Text(
            text = stringResource(id = R.string.followers),
            color = SubTextColor,
            modifier = Modifier.constrainAs(tvFollowers) {
                start.linkTo(separator1.end)
                end.linkTo(separator2.start)
                top.linkTo(followersCount.bottom, margin = 3.dp)
            }
        )


        Divider(modifier = Modifier
            .width(1.dp)
            .height(16.dp)
            .constrainAs(separator2) {
                start.linkTo(tvFollowers.end)
                end.linkTo(tvLike.start)
                top.linkTo(followingCount.top)
                bottom.linkTo(tvFollowers.bottom)
            })
        //点赞数
        Text(
            text = creatorProfile?.formattedLikeCount ?: "-",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.constrainAs(likeCount) {
                top.linkTo(followingCount.top)
                centerHorizontallyTo(tvLike)
            }
        )

        Text(
            text = stringResource(id = R.string.likes),
            color = SubTextColor,
            modifier = Modifier.constrainAs(tvLike) {
                top.linkTo(likeCount.bottom, margin = 3.dp)
                start.linkTo(separator2.end)
                end.linkTo(parent.end, margin = 36.dp)
            }
        )
    }


    Row(
        modifier = Modifier.height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //关注按钮
        Button(
            onClick = { }, modifier = Modifier
                .width(158.dp)
                .height(42.dp), shape = RoundedCornerShape(2.dp)
        ) {
            Text(text = stringResource(id = R.string.follow))
        }

        //社交平台
        creatorProfile?.pinSocialMedia?.let {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(40.dp)
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(2.dp),
                        color = Gray.copy(alpha = 0.2f)
                    )
                    .clickable {
                        context.redirectToApp(
                            link = it.link,
                            type = when (it.type) {
                                SocialMediaType.INSTAGRAM -> INSTAGRAM
                                SocialMediaType.YOUTUBE -> YOUTUBE
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                val icon = when (it.type) {
                    SocialMediaType.INSTAGRAM -> R.drawable.ic_instagram
                    SocialMediaType.YOUTUBE -> R.drawable.ic_youtube
                }
                Icon(painter = painterResource(id = icon), contentDescription = null)
            }
        }

        //更多
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(40.dp)
                .border(
                    width = 1.dp, shape = RoundedCornerShape(2.dp), color = Gray.copy(alpha = 0.2f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_down_more), contentDescription = null)
        }
    }

    //个人简介
    Text(
        text = creatorProfile?.bio ?: stringResource(id = R.string.no_bio_yet),
    )


}