package com.xiaobai.domain.creatorprofile

import com.xiaobai.data.model.VideoModel
import com.xiaobai.data.repository.creatorprofile.CreatorProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 获取创作者公开视频列表
 *
 */
class GetCreatorPublicVideoUseCase @Inject constructor(
    private val creatorProfileRepository: CreatorProfileRepository
) {
    operator fun invoke(id: Long): Flow<List<VideoModel>> {
        return creatorProfileRepository.getCreatorPublicVideo(id)
    }
}