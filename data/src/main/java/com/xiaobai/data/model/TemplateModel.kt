package com.xiaobai.data.model

import android.net.Uri
import androidx.core.net.toUri

/**
 * 模板模型
 * @param id 模板id
 * @param name 模板名称
 * @param hint 模板提示信息
 * @param mediaUrl 模板图片
 *
 */
data class TemplateModel(
    val id: Long,
    val name: String,
    val hint: String,
    val mediaUrl: String
){
    fun parseMediaLink(): Uri = "file:///android_asset/templateimages/${mediaUrl}".toUri()
}