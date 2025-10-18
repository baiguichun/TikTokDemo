package com.xiaobai.domain.creatorprofile

import com.xiaobai.data.model.UserModel
import com.xiaobai.data.repository.creatorprofile.CreatorProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 获取创作者详情
 *
 */
class GetCreatorProfileUseCase @Inject constructor(
    private val creatorProfileRepository: CreatorProfileRepository
) {
    operator fun invoke(id: Long): Flow<UserModel?> {
        return creatorProfileRepository.getCreatorDetails(id)
    }
}