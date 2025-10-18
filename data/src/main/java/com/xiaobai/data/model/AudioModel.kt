package com.xiaobai.data.model

/**
 * 音频信息
 */
data class AudioModel(
    //音频封面图片
    val audioCoverImage:String,
    //是否原创
    val isOriginal:Boolean,
    //音频作者信息
    val audioAuthor:UserModel,
    //音频使用量
    val numberOfPost:Long,
    //原始视频地址
    val originalVideoUrl:String,
)
