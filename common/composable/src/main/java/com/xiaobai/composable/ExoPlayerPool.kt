package com.xiaobai.composable

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.media3.common.Player
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer

/**
 * ExoPlayer 对象池 - 优化内存使用和播放器复用
 * 
 * 采用混合策略：
 * - 为当前正在使用的视频保持专用播放器映射
 * - 维护一个可复用的空闲播放器池
 * - 自动回收超出限制的播放器
 */
object ExoPlayerPool {
    private const val MAX_POOL_SIZE = 5 // 池中最多保留的播放器数量
    private const val IDLE_TIMEOUT_MS = 5 * 60 * 1000L // 空闲播放器超时时间：5分钟
    private const val TAG = "ExoPlayerPool"

    // 当前正在使用的播放器映射 (videoId -> ExoPlayer)
    private val activePlayerMap = mutableMapOf<String, ExoPlayer>()
    
    // 空闲可复用的播放器列表及其加入时间
    private val idlePlayers = mutableListOf<Pair<ExoPlayer, Long>>()
    
    // 访问时间记录，用于 LRU 淘汰
    private val accessTimeMap = mutableMapOf<String, Long>()

    /**
     * 获取播放器实例
     * 
     * 策略：
     * 1. 如果该 videoId 已有活跃播放器，直接返回
     * 2. 否则从空闲池中取一个
     * 3. 如果空闲池为空，创建新播放器
     * 4. 如果总数超限，淘汰最久未访问的播放器
     * 
     * @param context 上下文
     * @param videoId 视频Id
     * @return ExoPlayer 实例
     */
    @OptIn(ExperimentalFoundationApi::class)
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun getPlayer(context: Context, videoId: String): ExoPlayer {
        return synchronized(this) {
            // 记录访问时间
            accessTimeMap[videoId] = System.currentTimeMillis()
            
            // 如果该视频已有活跃播放器，直接返回
            activePlayerMap[videoId]?.let {
                Log.d(TAG, "复用已存在的播放器: $videoId")
                return@synchronized it
            }
            
            // 清理超时的空闲播放器
            cleanupExpiredIdlePlayers()
            
            // 从空闲池获取或创建新播放器
            val player = if (idlePlayers.isNotEmpty()) {
                Log.d(TAG, "从空闲池获取播放器: $videoId (池大小: ${idlePlayers.size})")
                val (idlePlayer, _) = idlePlayers.removeFirst()
                idlePlayer.apply {
                    // 清理旧的媒体项，准备加载新视频
                    if (mediaItemCount > 0) {
                        clearMediaItems()
                        Log.d(TAG, "清理空闲播放器的旧媒体项")
                    }
                }
            } else {
                Log.d(TAG, "创建新播放器: $videoId (活跃数: ${activePlayerMap.size})")
                ExoPlayer.Builder(context).build().apply {
                    videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                    repeatMode = Player.REPEAT_MODE_ONE
                }
            }
            
            // 添加到活跃映射
            activePlayerMap[videoId] = player
            
            // 如果总数超限，淘汰最久未访问的
            evictIfNeeded()
            
            player
        }
    }

    /**
     * 软释放播放器（移回空闲池以供复用）
     * 
     * 策略：
     * 1. 从活跃映射中移除
     * 2. 清理播放器状态（暂停、清除媒体项）
     * 3. 移入空闲池
     * 4. ⚠️ 不调用 clearVideoSurface()，Surface 生命周期交给 PlayerView/AndroidView 管理
     * 
     * @param context 上下文
     * @param player 播放器实例
     */
    fun softRelease(context: Context, player: ExoPlayer) {
        synchronized(this) {
            // 从活跃映射中移除
            val videoId = activePlayerMap.entries.find { it.value == player }?.key
            if (videoId != null) {
                activePlayerMap.remove(videoId)
                accessTimeMap.remove(videoId)
                Log.d(TAG, "软释放播放器: $videoId (活跃数: ${activePlayerMap.size})")
            }
            
            // 清理播放器状态
            player.pause()
            // ⚠️ 不调用 stop()，保留已准备好的状态，加快复用速度
            // player.stop() 会重置为 IDLE，需要重新 prepare()
            
            // 在低内存设备上才清除媒体项和停止播放器
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? android.app.ActivityManager
            if (am?.isLowRamDevice == true) {
                player.stop()
                player.clearMediaItems()
                Log.d(TAG, "低内存设备：完全清理播放器")
            }
            
            // 检查总容量，决定是否移入空闲池
            val totalPlayers = activePlayerMap.size + idlePlayers.size
            if (totalPlayers < MAX_POOL_SIZE) {
                idlePlayers.add(Pair(player, System.currentTimeMillis()))
                Log.d(TAG, "播放器移入空闲池 (池大小: ${idlePlayers.size}, 总计: ${totalPlayers + 1}/$MAX_POOL_SIZE)")
            } else {
                Log.d(TAG, "池已满，释放播放器 (总计: $totalPlayers/$MAX_POOL_SIZE)")
                player.release()
            }
        }
    }
    
    /**
     * 清理超时的空闲播放器
     */
    private fun cleanupExpiredIdlePlayers() {
        val now = System.currentTimeMillis()
        val iterator = idlePlayers.iterator()
        var cleanedCount = 0
        
        while (iterator.hasNext()) {
            val (player, timestamp) = iterator.next()
            if (now - timestamp > IDLE_TIMEOUT_MS) {
                player.release()
                iterator.remove()
                cleanedCount++
            }
        }
        
        if (cleanedCount > 0) {
            Log.i(TAG, "清理超时空闲播放器: $cleanedCount 个 (剩余: ${idlePlayers.size})")
        }
    }

    /**
     * 淘汰最久未访问的播放器（LRU 策略）
     */
    private fun evictIfNeeded() {
        val totalPlayers = activePlayerMap.size + idlePlayers.size
        if (totalPlayers <= MAX_POOL_SIZE) return
        
        // 找出最久未访问的视频
        val lruVideoId = accessTimeMap.minByOrNull { it.value }?.key
        if (lruVideoId != null) {
            activePlayerMap[lruVideoId]?.let { player ->
                Log.w(TAG, "淘汰最久未访问的播放器: $lruVideoId")
                activePlayerMap.remove(lruVideoId)
                accessTimeMap.remove(lruVideoId)
                player.release()
            }
        }
        
        // 如果空闲池也超限，移除最早的
        while (idlePlayers.size > MAX_POOL_SIZE - activePlayerMap.size) {
            val pair = idlePlayers.removeFirstOrNull()
            pair?.first?.release()
            Log.w(TAG, "从空闲池移除过期播放器")
        }
    }

    /**
     * 释放所有播放器资源
     */
    fun releaseAll() {
        synchronized(this) {
            Log.i(TAG, "释放所有播放器 (活跃: ${activePlayerMap.size}, 空闲: ${idlePlayers.size})")
            
            activePlayerMap.values.forEach { it.release() }
            activePlayerMap.clear()
            
            idlePlayers.forEach { (player, _) -> player.release() }
            idlePlayers.clear()
            
            accessTimeMap.clear()
        }
    }
    
    /**
     * 获取池状态（用于调试）
     */
    fun getPoolStatus(): String {
        return synchronized(this) {
            "活跃播放器: ${activePlayerMap.size}, 空闲播放器: ${idlePlayers.size}, " +
            "总计: ${activePlayerMap.size + idlePlayers.size}/$MAX_POOL_SIZE"
        }
    }
}