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
    
    // ========== 性能统计数据 ==========
    private var totalGetPlayerCalls = 0          // getPlayer 调用总次数
    private var playerCreatedCount = 0           // 创建播放器次数
    private var playerReusedCount = 0            // 复用播放器次数
    private var playerReleasedCount = 0          // 释放播放器次数
    private var maxActivePlayersEver = 0         // 历史最大活跃播放器数
    private var maxIdlePlayersEver = 0           // 历史最大空闲播放器数
    private var sessionStartTime = System.currentTimeMillis() // 会话开始时间

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
            // 统计：调用次数
            totalGetPlayerCalls++
            
            // 记录访问时间
            accessTimeMap[videoId] = System.currentTimeMillis()
            
            // 如果该视频已有活跃播放器，直接返回
            activePlayerMap[videoId]?.let {
                playerReusedCount++  // 统计：复用次数
                Log.d(TAG, "复用已存在的播放器: $videoId")
                return@synchronized it
            }
            
            // 清理超时的空闲播放器
            cleanupExpiredIdlePlayers()
            
            // 从空闲池获取或创建新播放器
            val player = if (idlePlayers.isNotEmpty()) {
                playerReusedCount++  // 统计：复用次数
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
                playerCreatedCount++  // 统计：创建次数
                Log.d(TAG, "创建新播放器: $videoId (活跃数: ${activePlayerMap.size})")
                ExoPlayer.Builder(context).build().apply {
                    videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                    repeatMode = Player.REPEAT_MODE_ONE
                }
            }
            
            // 添加到活跃映射
            activePlayerMap[videoId] = player
            
            // 统计：更新峰值
            maxActivePlayersEver = maxOf(maxActivePlayersEver, activePlayerMap.size)
            
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
                maxIdlePlayersEver = maxOf(maxIdlePlayersEver, idlePlayers.size)  // 统计：更新峰值
                Log.d(TAG, "播放器移入空闲池 (池大小: ${idlePlayers.size}, 总计: ${totalPlayers + 1}/$MAX_POOL_SIZE)")
            } else {
                playerReleasedCount++  // 统计：释放次数
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
    
    /**
     * 获取性能统计数据
     */
    fun getPerformanceStats(): PerformanceStats {
        return synchronized(this) {
            val sessionDurationMin = (System.currentTimeMillis() - sessionStartTime) / 60000.0
            val reuseRate = if (totalGetPlayerCalls > 0) {
                (playerReusedCount.toFloat() / totalGetPlayerCalls * 100)
            } else 0f
            
            PerformanceStats(
                totalGetPlayerCalls = totalGetPlayerCalls,
                playerCreatedCount = playerCreatedCount,
                playerReusedCount = playerReusedCount,
                playerReleasedCount = playerReleasedCount,
                currentActiveCount = activePlayerMap.size,
                currentIdleCount = idlePlayers.size,
                maxActivePlayersEver = maxActivePlayersEver,
                maxIdlePlayersEver = maxIdlePlayersEver,
                reuseRate = reuseRate,
                sessionDurationMinutes = sessionDurationMin
            )
        }
    }
    
    /**
     * 打印性能统计报告
     */
    fun logPerformanceReport() {
        val stats = getPerformanceStats()
        
        // 计算内存优化效果
        val wouldUseMemoryMB = if (stats.totalGetPlayerCalls > 0) stats.totalGetPlayerCalls * 8 else 0
        val actualUsingMemoryMB = (stats.currentActiveCount + stats.currentIdleCount) * 8
        val memorySavedMB = wouldUseMemoryMB - actualUsingMemoryMB
        val memorySavedPercent = if (wouldUseMemoryMB > 0) {
            (memorySavedMB.toFloat() / wouldUseMemoryMB * 100)
        } else 0f
        
        // 计算创建次数减少比例
        val creationReduction = if (stats.totalGetPlayerCalls > 0) {
            (1 - stats.playerCreatedCount.toFloat() / stats.totalGetPlayerCalls) * 100
        } else 0f
        
        Log.i(TAG, """
            ========== ExoPlayerPool 性能报告 ==========
            会话时长: ${"%.1f".format(stats.sessionDurationMinutes)} 分钟
            
            【调用统计】
            getPlayer 调用次数: ${stats.totalGetPlayerCalls}
            创建播放器次数: ${stats.playerCreatedCount}
            复用播放器次数: ${stats.playerReusedCount}
            释放播放器次数: ${stats.playerReleasedCount}
            复用率: ${"%.1f".format(stats.reuseRate)}%
            
            【当前状态】
            活跃播放器: ${stats.currentActiveCount}
            空闲播放器: ${stats.currentIdleCount}
            总计: ${stats.currentActiveCount + stats.currentIdleCount}/$MAX_POOL_SIZE
            
            【历史峰值】
            最大活跃数: ${stats.maxActivePlayersEver}
            最大空闲数: ${stats.maxIdlePlayersEver}
            
            【性能优化效果】
            创建次数减少: ${"%.1f".format(creationReduction)}% (${stats.totalGetPlayerCalls - stats.playerCreatedCount}/${stats.totalGetPlayerCalls})
            无优化内存占用: ${wouldUseMemoryMB}MB (${stats.totalGetPlayerCalls}个播放器)
            实际内存占用: ${actualUsingMemoryMB}MB (${stats.currentActiveCount + stats.currentIdleCount}个播放器)
            节省内存: ${memorySavedMB}MB (${"%.1f".format(memorySavedPercent)}%)
            
            【池效率分析】
            池利用率: ${"%.1f".format((stats.currentActiveCount + stats.currentIdleCount).toFloat() / MAX_POOL_SIZE * 100)}%
            峰值利用率: ${"%.1f".format(maxOf(stats.maxActivePlayersEver, stats.maxIdlePlayersEver).toFloat() / MAX_POOL_SIZE * 100)}%
            ==========================================
        """.trimIndent())
    }
    
    /**
     * 重置性能统计（用于测试）
     */
    fun resetStats() {
        synchronized(this) {
            totalGetPlayerCalls = 0
            playerCreatedCount = 0
            playerReusedCount = 0
            playerReleasedCount = 0
            maxActivePlayersEver = 0
            maxIdlePlayersEver = 0
            sessionStartTime = System.currentTimeMillis()
            Log.i(TAG, "性能统计已重置")
        }
    }
    
    /**
     * 性能统计数据类
     */
    data class PerformanceStats(
        val totalGetPlayerCalls: Int,           // getPlayer 调用总次数
        val playerCreatedCount: Int,            // 创建播放器次数
        val playerReusedCount: Int,             // 复用播放器次数
        val playerReleasedCount: Int,           // 释放播放器次数
        val currentActiveCount: Int,            // 当前活跃播放器数
        val currentIdleCount: Int,              // 当前空闲播放器数
        val maxActivePlayersEver: Int,          // 历史最大活跃数
        val maxIdlePlayersEver: Int,            // 历史最大空闲数
        val reuseRate: Float,                   // 复用率 (%)
        val sessionDurationMinutes: Double      // 会话时长（分钟）
    ) {
        /**
         * 计算内存优化效果
         */
        fun getMemoryOptimization(): MemoryOptimization {
            val wouldUseMemoryMB = if (totalGetPlayerCalls > 0) totalGetPlayerCalls * 8 else 0
            val actualUsingMemoryMB = (currentActiveCount + currentIdleCount) * 8
            val memorySavedMB = wouldUseMemoryMB - actualUsingMemoryMB
            val memorySavedPercent = if (wouldUseMemoryMB > 0) {
                (memorySavedMB.toFloat() / wouldUseMemoryMB * 100)
            } else 0f
            
            return MemoryOptimization(
                wouldUseMemoryMB = wouldUseMemoryMB,
                actualUsingMemoryMB = actualUsingMemoryMB,
                memorySavedMB = memorySavedMB,
                memorySavedPercent = memorySavedPercent
            )
        }
        
        /**
         * 计算创建次数优化效果
         */
        fun getCreationOptimization(): CreationOptimization {
            val reductionCount = totalGetPlayerCalls - playerCreatedCount
            val reductionPercent = if (totalGetPlayerCalls > 0) {
                (1 - playerCreatedCount.toFloat() / totalGetPlayerCalls) * 100
            } else 0f
            
            return CreationOptimization(
                totalCalls = totalGetPlayerCalls,
                actualCreations = playerCreatedCount,
                reductionCount = reductionCount,
                reductionPercent = reductionPercent
            )
        }
        
        /**
         * 计算池利用率
         */
        fun getPoolUtilization(): PoolUtilization {
            val currentUtilization = (currentActiveCount + currentIdleCount).toFloat() / 5 * 100
            val peakUtilization = maxOf(maxActivePlayersEver, maxIdlePlayersEver).toFloat() / 5 * 100
            
            return PoolUtilization(
                currentUtilization = currentUtilization,
                peakUtilization = peakUtilization
            )
        }
    }
    
    /**
     * 内存优化数据
     */
    data class MemoryOptimization(
        val wouldUseMemoryMB: Int,      // 无优化时的内存占用
        val actualUsingMemoryMB: Int,   // 实际内存占用
        val memorySavedMB: Int,         // 节省的内存
        val memorySavedPercent: Float   // 节省比例
    )
    
    /**
     * 创建次数优化数据
     */
    data class CreationOptimization(
        val totalCalls: Int,            // 总调用次数
        val actualCreations: Int,       // 实际创建次数
        val reductionCount: Int,        // 减少的创建次数
        val reductionPercent: Float     // 减少比例
    )
    
    /**
     * 池利用率数据
     */
    data class PoolUtilization(
        val currentUtilization: Float,  // 当前利用率
        val peakUtilization: Float      // 峰值利用率
    )
}