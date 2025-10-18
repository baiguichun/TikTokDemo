package com.xiaobai.data.model

import com.xiaobai.core.extension.formattedCount
import com.xiaobai.core.extension.randomUploadDate

/**
 * 视频模型
 * @param videoId: 视频id
 * @param authorDetails: 视频作者信息
 * @param videoStats: 视频统计信息
 * @param videoLink: 视频链接
 * @param description: 视频描述
 * @param currentViewerInteraction: 当前观看者与视频的交互状态
 * @param createdAt: 视频创建时间
 * @param audioModel: 视频音频信息
 * @param hasTag: 视频标签
 */
data class VideoModel(
    val videoId: String,
    val authorDetails: UserModel,
    val videoStats: VideoStats,
    val videoLink: String,
    val description: String,
    val currentViewerInteraction: ViewerInteraction = ViewerInteraction(),
    val createdAt: String = randomUploadDate(),
    val audioModel: AudioModel? = null,
    val hasTag: List<HasTag> = listOf(),
) {
    /**
     * 视频统计信息
     * @param like: 点赞数
     * @param comment: 评论数
     * @param share: 分享数
     * @param favourite: 收藏数
     * @param views: 观看数
     */
    data class VideoStats(
        var like: Long,
        var comment: Long,
        var share: Long,
        var favourite: Long,
        var views: Long = (like.plus(500)..like.plus(100000)).random()
    ) {
        var formattedLikeCount: String = ""
        var formattedCommentCount: String = ""
        var formattedShareCount: String = ""
        var formattedFavouriteCount: String = ""
        var formattedViewsCount: String = ""

        init {
            formattedLikeCount = like.formattedCount()
            formattedCommentCount = comment.formattedCount()
            formattedShareCount = share.formattedCount()
            formattedFavouriteCount = favourite.formattedCount()
            formattedViewsCount = views.formattedCount()
        }
    }

    /**
     * 视频标签
     * @param id: 标签id
     * @param title: 标题
     */
    data class HasTag(
        val id: Long,
        val title: String
    )

    /**
     * 观看者与视频的交互状态
     * @param isLikedByYou: 是否点赞
     * @param isAddedToFavourite: 是否收藏
     */
    data class ViewerInteraction(
        var isLikedByYou: Boolean = false,
        var isAddedToFavourite: Boolean = false
    )
}