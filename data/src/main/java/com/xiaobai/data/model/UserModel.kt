package com.xiaobai.data.model

import com.xiaobai.core.extension.formattedCount

/**
 * 用户信息
 *
 */
data class UserModel(
    //用户ID
    val userId: Long,
    //用户名（用户自定义的唯一标识符）
    val uniqueUserName: String,
    //全名
    val fullName: String,
    //关注数
    val following: Long,
    //粉丝数
    val followers: Long,
    //获赞数
    val likes: Long,
    //个人简介
    val bio: String,
    //头像图片链接
    val profilePic: String,
    //是否认证用户
    val isVerified: Boolean,
    //是否将点赞视频设为私密
    val isLikedVideoPrivate: Boolean = true,
    //置顶社交媒体信息
    val pinSocialMedia: SocialMedia? = null
) {
    var formattedFollowingCount: String = ""
    var formattedFollowersCount: String = ""
    var formattedLikeCount: String = ""

    init {
        formattedLikeCount = likes.formattedCount()
        formattedFollowingCount = following.formattedCount()
        formattedFollowersCount = followers.formattedCount()
    }

    data class SocialMedia(
        val type: SocialMediaType,
        val link: String
    )
}

enum class SocialMediaType {
    INSTAGRAM,
    YOUTUBE
}