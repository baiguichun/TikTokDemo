package com.xiaobai.data.model

/**
 * 推荐视频页数据模型
 * @param user: 用户信息
 * @param videoStats: 视频统计信息
 * @param music: 背景音乐信息
 * @param video: 视频内容信息
 */
data class ForYouFeedModel(
    val user: User,
    val videoStats: VideoStats,
    val music: Audio,
    val video: Video,
) {
    /**
     * 用户信息
     * @param id: 用户id
     * @param userName: 用户名（登录或唯一标识）
     * @param fullName: 用户全名（显示昵称）
     * @param profilePic: 头像
     */
    data class User(
        val id: Long,
        val userName: String,
        val fullName: String,
        val profilePic: String,
    )

    /**
     * 视频统计信息
     * @param like: 点赞数
     * @param comment: 评论数
     * @param share: 分享数
     */
    data class VideoStats(
        val like: Int,
        val comment: Int,
        val share: Int
    )

    /**
     * 视频内容信息
     * @param url: 视频地址
     * @param videoCaption: 视频标题
     * @param hasTag: 视频标签
     */
    data class Video(
        val url: String,
        val videoCaption: String,
        val hasTag: List<HasTag>
    ) {
        /**
         * 视频标签
         * @param hasTagId: 标签id
         * @param title: 标题
         */
        data class HasTag(
            val hasTagId: Long,
            val title: String
        )
    }

    data class Audio(
        val url: String
    )
}
