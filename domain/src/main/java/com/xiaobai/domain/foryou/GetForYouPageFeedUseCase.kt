package com.xiaobai.domain.foryou

import com.xiaobai.data.model.VideoModel
import com.xiaobai.data.repository.home.ForYouRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 获取首页推荐视频列表
 *
 */
class GetForYouPageFeedUseCase @Inject constructor(private val forYouRepository: ForYouRepository) {
    operator fun invoke(): Flow<List<VideoModel>> {
        return forYouRepository.getForYouPageFeeds()
    }
}