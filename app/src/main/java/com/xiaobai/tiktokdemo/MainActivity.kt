package com.xiaobai.tiktokdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
    
    // 注意：播放器由 Compose 组件自动管理，无需手动释放
}
