package com.xiaobai.data.repository.home

import com.xiaobai.data.model.VideoModel
import com.xiaobai.data.source.VideoDataSource.fetchVideos
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ForYouRepository @Inject constructor() {
    fun getForYouPageFeeds(): Flow<List<VideoModel>> {
        return fetchVideos()
    }
}