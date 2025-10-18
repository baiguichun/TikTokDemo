package com.xiaobai.domain.following

import com.xiaobai.data.model.ContentCreatorFollowingModel
import com.xiaobai.data.repository.home.FollowingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContentCreatorsUseCase@Inject constructor(private val followingRepository: FollowingRepository) {
    operator fun invoke(): Flow<List<ContentCreatorFollowingModel>> {
        return followingRepository.getContentCreatorForFollowing()
    }
}