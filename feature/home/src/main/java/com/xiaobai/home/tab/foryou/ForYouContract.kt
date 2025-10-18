package com.xiaobai.home.tab.foryou

import com.xiaobai.data.model.VideoModel

data class ViewState(
    val forYouPageFeed: List<VideoModel>? = null,
    val isLoading: Boolean? = null,
    val error: String? = null,
)

sealed class ForYouEvent {
}