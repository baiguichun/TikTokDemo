package com.xiaobai.data.repository.comment

import com.xiaobai.data.model.CommentList
import com.xiaobai.data.source.CommentDataSource.fetchComment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CommentRepository @Inject constructor() {
    fun getComment(videoId: String): Flow<CommentList> {
        return fetchComment(videoId)
    }
}