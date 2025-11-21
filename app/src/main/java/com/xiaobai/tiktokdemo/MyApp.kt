package com.xiaobai.tiktokdemo

import android.app.Application
import android.content.ComponentCallbacks2
import android.util.Log
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
     * 注意：由于采用 Compose 组件自治管理播放器，
     * 当组件离开预加载范围时会自动释放播放器，
     * 无需手动管理内存
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> {
                Log.i(TAG, "UI 不可见 (播放器由 Compose 自动管理)")
            }
            
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> {
                Log.w(TAG, "内存不足 (level: $level)")
            }
            
            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND,
            ComponentCallbacks2.TRIM_MEMORY_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                Log.e(TAG, "系统内存极度不足 (level: $level)")
            }
        }
    }

    /**
     * 低内存警告回调（旧版 API，保留兼容）
     */
    override fun onLowMemory() {
        super.onLowMemory()
        Log.e(TAG, "低内存警告")
    }
}