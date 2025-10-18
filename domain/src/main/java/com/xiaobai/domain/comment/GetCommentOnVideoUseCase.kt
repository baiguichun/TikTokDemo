package com.xiaobai.domain.comment

import com.xiaobai.data.model.CommentList
import com.xiaobai.data.repository.comment.CommentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 获取视频评论列表
 */
class GetCommentOnVideoUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    operator fun invoke(videoId: String): Flow<CommentList> {
        return commentRepository.getComment(videoId)
    }
}