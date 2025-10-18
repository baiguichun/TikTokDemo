package com.xiaobai.data.model
/**
 * 评论列表
 *
 * @param videoId 视频id
 * @param totalComment 评论总数
 * @param comments 评论列表
 * @param isCommentPrivate 是否是隐私评论
 */
data class CommentList(
    val videoId: String?,
    val totalComment: Int,
    val comments: List<Comment>,
    val isCommentPrivate: Boolean
) {
    /**
     * 评论
     *
     * @param commentBy 评论者
     * @param comment 评论内容
     * @param createdAt 创建时间
     * @param totalLike 喜欢总数
     * @param totalDisLike 不喜欢总数
     * @param threadCount 该评论下的回复数量统计
     * @param thread 该评论的所有回复评论列表
     */
    data class Comment(
        val commentBy: UserModel,
        val comment: String?,
        val createdAt: String,
        val totalLike: Long,
        val totalDisLike: Long,
        val threadCount: Int,
        val thread: List<Comment>
    )
}

data class CommentText(
    val comment: String
)