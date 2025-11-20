package com.xiaobai.tiktokdemo

import android.app.Application
import android.content.ComponentCallbacks2
import android.util.Log
import com.xiaobai.composable.ExoPlayerPool
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    
    companion object {
        private const val TAG = "MyApp"
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "应用启动")
    }

    /**
     * 系统内存紧张时的回调
     * 
     * 根据不同的内存等级采取不同的清理策略：
     * - TRIM_MEMORY_UI_HIDDEN: UI不可见，清理空闲播放器
     * - TRIM_MEMORY_RUNNING_LOW: 运行时内存不足
     * - TRIM_MEMORY_COMPLETE: 内存极度不足，释放所有资源
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> {
                // UI 不再可见（用户切换到其他应用）
                // 这是清理资源的好时机
                Log.i(TAG, "UI 不可见，清理资源")
                // ExoPlayerPool 会自动通过超时机制清理，这里不需要手动释放
            }
            
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> {
                // 应用运行时内存不足
                Log.w(TAG, "内存不足 (level: $level)，释放播放器池")
                ExoPlayerPool.releaseAll()
            }
            
            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND,
            ComponentCallbacks2.TRIM_MEMORY_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                // 应用在后台且系统内存极度不足
                Log.e(TAG, "系统内存极度不足 (level: $level)，释放所有播放器")
                ExoPlayerPool.releaseAll()
            }
        }
    }

    /**
     * 低内存警告回调（旧版 API，保留兼容）
     */
    override fun onLowMemory() {
        super.onLowMemory()
        Log.e(TAG, "低内存警告，释放所有播放器")
        ExoPlayerPool.releaseAll()
    }
}