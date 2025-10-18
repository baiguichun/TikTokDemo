package com.xiaobai.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import com.xiaobai.core.AppContract.Type.INSTAGRAM
import com.xiaobai.core.AppContract.Type.YOUTUBE

object IntentUtils {
    /**
     * 分享
     * @param type 分享类型
     * @param title 标题
     * @param text 内容
     */
    fun Context.share(
        type: String = "text/plain",
        title: String = "",
        text: String = ""
    ) {
        //使用 Intent 的 ACTION_SEND 动作创建分享意图
        val intent = Intent(Intent.ACTION_SEND).apply {
            //设置分享内容类型
            setType(type)
            //添加要分享的文本内容
            putExtra(Intent.EXTRA_TEXT, text)
        }
        //创建系统分享选择器
        val chooserIntent = Intent.createChooser(intent, null)
        //启动分享界面
        startActivity(chooserIntent)
    }

    fun Context.redirectToApp(link: String, type: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        try {
            when (type) {
                YOUTUBE -> intent.setPackage("com.google.android.youtube")
                INSTAGRAM -> intent.setPackage("com.instagram.android")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("share", "redirect fail: ${e.message}")
        }
    }
}

fun Context.openAppSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts("package", packageName, null)
        }
    startActivity(intent)
}