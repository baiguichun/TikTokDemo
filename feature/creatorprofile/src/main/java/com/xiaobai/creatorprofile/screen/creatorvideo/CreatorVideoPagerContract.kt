package com.xiaobai.creatorprofile.screen.creatorvideo

import com.xiaobai.data.model.VideoModel

data class ViewState(
    val isLoading: Boolean? = null,
    val error: String? = null,
    val creatorVideosList: List<VideoModel>? = null
)

sealed class CreatorVideoEvent {
}

