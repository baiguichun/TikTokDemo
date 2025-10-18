package com.xiaobai.creatorprofile.screen.creatorprofile.tabs

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.xiaobai.creatorprofile.component.VideoGrid
import com.xiaobai.creatorprofile.screen.creatorprofile.CreatorProfileViewModel
import com.xiaobai.data.model.VideoModel

@Composable
fun PublicVideoTab(
    viewModel: CreatorProfileViewModel,
    scrollState: ScrollState,
    onClickVideo: (video: VideoModel, index: Int) -> Unit
) {
    val creatorPublicVideos by viewModel.publicVideosList.collectAsState()
    VideoGrid(
        scrollState = scrollState,
        videoList = creatorPublicVideos,
        onClickVideo = onClickVideo
    )
}