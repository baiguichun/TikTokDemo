package com.xiaobai.composable

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.media3.common.Player
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer

object ExoPlayerPool {
    private const val MAX_POOL_SIZE = 5 // 池中最多保留的播放器数量

    // LinkedHashMap 保留插入顺序，用作 LRU
    //MAX_POOL_SIZE：池中最多保留的播放器数量为5个
    //0.75f：负载因子，影响 HashMap 的扩容阈值
    //true：accessOrder 参数，设置为 true 表示按访问顺序排序（LRU 模式）
    //工作原理
    //当向 playerMap 添加新播放器且数量超过5个时
    //removeEldestEntry 方法被触发
    //自动释放最久未使用的 ExoPlayer 实例
    //保持池中播放器数量在限制范围内
    private val playerMap = object : LinkedHashMap<String, ExoPlayer>(MAX_POOL_SIZE, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, ExoPlayer>?): Boolean {
            if (size > MAX_POOL_SIZE) {
                eldest?.value?.release()
                return true
            }
            return false
        }
    }

    /**
     * 获取播放器实例
     * @param context 上下文
     * @param videoLink 视频链接
     * @return ExoPlayer 实例
     */
    @OptIn(ExperimentalFoundationApi::class)
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun getPlayer(context: Context, videoLink: String): ExoPlayer {
        return synchronized(this) {
            playerMap[videoLink] ?: ExoPlayer.Builder(context).build().apply {
                //设置视频缩放模式为适应屏幕
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                //设置循环模式为单个视频重复播放
                repeatMode = Player.REPEAT_MODE_ONE
                // 将新创建的播放器添加到池中
                playerMap[videoLink] = this
            }
        }
    }

    /**
     * 归还播放器到池中
     * @param videoLink 视频链接
     * @param player 播放器实例
     */
    fun releasePlayer(videoLink: String, player: ExoPlayer) {
        synchronized(this) {
            // 停止当前播放
            player.stop()
            //停止当前播放
            player.clearMediaItems()
            // 播放器保留在池中以供复用
        }
    }

    /**
     * 释放所有播放器资源
     */
    fun releaseAll() {
        synchronized(this) {
            playerMap.values.forEach { it.release() }
            playerMap.clear()
        }
    }
}