package com.xiaobai.data.repository.home

import com.xiaobai.data.model.ContentCreatorFollowingModel
import com.xiaobai.data.source.ContentCreatorForFollowingDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FollowingRepository @Inject constructor() {
    fun getContentCreatorForFollowing(): Flow<List<ContentCreatorFollowingModel>> {
        return ContentCreatorForFollowingDataSource.fetchContentCreatorForFollowing()
    }
}