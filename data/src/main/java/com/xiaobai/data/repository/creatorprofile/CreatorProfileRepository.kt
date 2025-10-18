package com.xiaobai.data.repository.creatorprofile

import com.xiaobai.data.model.UserModel
import com.xiaobai.data.model.VideoModel
import com.xiaobai.data.source.UsersDataSource.fetchSpecificUser
import com.xiaobai.data.source.VideoDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreatorProfileRepository @Inject constructor() {
    fun getCreatorDetails(id: Long): Flow<UserModel?> {
        return fetchSpecificUser(id)
    }

    fun getCreatorPublicVideo(id: Long): Flow<List<VideoModel>> {
        return VideoDataSource.fetchVideosOfParticularUser(id)
    }
}