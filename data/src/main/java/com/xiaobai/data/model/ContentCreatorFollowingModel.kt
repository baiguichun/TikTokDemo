package com.xiaobai.data.model

import android.net.Uri
import androidx.core.net.toUri

/**
 * 用户关注的内容创作者信息
 * @param userModel: 内容创作者信息
 * @param coverVideo: 封面视频信息
 *
 */
data class ContentCreatorFollowingModel (
    val userModel: UserModel,
    val coverVideo: VideoModel
) {
    fun parseVideo(): Uri = "asset:///videos/${coverVideo.videoLink}".toUri()
}