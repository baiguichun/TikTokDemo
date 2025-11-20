package com.xiaobai.tiktokdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.xiaobai.composable.ExoPlayerPool
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //为了确保启动画面能够尽早显示
        //提前调用可以减少应用启动时的白屏时间
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            RootScreen()
        }
    }

    /**
     * Activity 完全销毁时释放所有播放器资源
     * 
     * 触发场景：
     * - 用户按返回键退出应用
     * - 系统因内存不足杀死应用
     * - 应用崩溃
     */
    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            // 只在 Activity 真正结束时释放（不是配置变更导致的重建）
            ExoPlayerPool.releaseAll()
        }
    }
}
