package com.xiaobai.core.extension

import android.content.Context
import android.provider.Settings
import java.text.DecimalFormat

val decimalFormat = DecimalFormat("#.#")
/**
 * 格式化数字
 */
fun Long.formattedCount(): String {
    return if (this < 10000) {
        this.toString()
    } else if (this < 1000000) {
        "${decimalFormat.format(this.div(1000))}K"
    } else if (this < 1000000000) {
        "${decimalFormat.format(this.div(1000000))}M"
    } else {
        "${decimalFormat.format(this.div(1000000000))}B"
    }
}

/**
 * 随机生成上传时间
 */
fun randomUploadDate(): String = "${(1..24).random()}h"

/**
 * 获取国际电话号码，格式：区号-号码
 */
fun Pair<String, String>.getFormattedInternationalNumber() = "${this.first}-${this.second}".trim()

/**
 * 获取当前屏幕亮度
 */
fun Context.getCurrentBrightness():Float= Settings.System.getInt(this.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
    .toFloat().div(255)