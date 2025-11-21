# 📦 Composable Module

这是 TikTokDemo 项目的 Composable 组件模块，包含可复用的 UI 组件和核心功能。

---

## 📁 模块结构

```
common/composable/
├── src/main/java/com/xiaobai/composable/
│   ├── ExoPlayerPool.kt              # 🎬 ExoPlayer 对象池（核心优化）
│   ├── VideoPlayer.kt                # 📹 视频播放组件
│   ├── TikTokVerticalVideoPager.kt   # 📱 垂直视频分页器
│   ├── CaptureButton.kt              # 📸 拍摄按钮组件
│   ├── CustomButton.kt               # 🔘 自定义按钮组件
│   ├── CustomIconButton.kt           # 🔘 自定义图标按钮
│   ├── TopBar.kt                     # 🔝 顶部导航栏
│   └── ContentSearchBar.kt           # 🔍 搜索栏组件
│
├── docs/                             # 📚 技术文档
│   ├── README.md                     # 文档中心主页
│   ├── exoplayer-pool/               # ExoPlayerPool 文档
│   └── videoplayer/                  # VideoPlayer 文档
│
└── build.gradle.kts                  # Gradle 配置文件
```

---

## 🎯 核心组件

### 1️⃣ ExoPlayerPool - ExoPlayer 对象池 ⭐

**功能**: 管理和复用 ExoPlayer 实例，显著降低内存占用和创建开销。

**特性**:
- ✅ 播放器复用率 > 80%
- ✅ 内存节省 > 90%
- ✅ 首帧时间减少 67%
- ✅ 完善的性能监控

**文档**: [ExoPlayerPool 技术文档](./docs/exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md)

**代码**: [ExoPlayerPool.kt](./src/main/java/com/xiaobai/composable/ExoPlayerPool.kt)

---

### 2️⃣ VideoPlayer - 视频播放组件

**功能**: Jetpack Compose 视频播放组件，支持预加载、后台恢复、错误处理等功能。

**特性**:
- ✅ 视频预加载（当前页 ±1）
- ✅ 后台切换无缝恢复
- ✅ 自动错误重试（最多 3 次）
- ✅ 性能监控和日志追踪

**文档**: [VideoPlayer 优化报告](./docs/videoplayer/VIDEO_PLAYER_OPTIMIZATION.md)

**代码**: [VideoPlayer.kt](./src/main/java/com/xiaobai/composable/VideoPlayer.kt)

---

### 3️⃣ TikTokVerticalVideoPager - 垂直视频分页器

**功能**: 实现类似 TikTok 的垂直滑动视频列表。

**特性**:
- ✅ 流畅的垂直滑动体验
- ✅ 自动播放当前页视频
- ✅ 支持预加载

**代码**: [TikTokVerticalVideoPager.kt](./src/main/java/com/xiaobai/composable/TikTokVerticalVideoPager.kt)

---

### 4️⃣ 其他 UI 组件

| 组件 | 说明 | 文件 |
|------|------|------|
| CaptureButton | 拍摄按钮，带动画效果 | [CaptureButton.kt](./src/main/java/com/xiaobai/composable/CaptureButton.kt) |
| CustomButton | 自定义按钮组件 | [CustomButton.kt](./src/main/java/com/xiaobai/composable/CustomButton.kt) |
| CustomIconButton | 自定义图标按钮 | [CustomIconButton.kt](./src/main/java/com/xiaobai/composable/CustomIconButton.kt) |
| TopBar | 顶部导航栏 | [TopBar.kt](./src/main/java/com/xiaobai/composable/TopBar.kt) |
| ContentSearchBar | 搜索栏组件 | [ContentSearchBar.kt](./src/main/java/com/xiaobai/composable/ContentSearchBar.kt) |

---

## 📚 技术文档

完整的技术文档位于 `docs/` 目录下：

### 📖 [文档中心主页](./docs/README.md)

### 快速链接

#### ExoPlayerPool 相关
- 📘 [完整技术文档](./docs/exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md) - 架构、API、性能优化
- 🎬 [生命周期管理指南](./docs/exoplayer-pool/EXOPLAYER_POOL_LIFECYCLE.md) - 资源管理和内存优化
- 📊 [性能测试指南](./docs/exoplayer-pool/PERFORMANCE_TESTING_GUIDE.md) - 如何测试和验证性能
- 📈 [性能统计使用指南](./docs/exoplayer-pool/PERFORMANCE_STATS_USAGE.md) - 如何使用性能数据

#### VideoPlayer 相关
- 📹 [VideoPlayer 优化报告](./docs/videoplayer/VIDEO_PLAYER_OPTIMIZATION.md) - 组件优化详解

---

## 🚀 使用示例

### 使用 VideoPlayer

```kotlin
import com.xiaobai.composable.VideoPlayer

@Composable
fun MyScreen(video: VideoModel) {
    VideoPlayer(
        video = video,
        pagerState = pagerState,
        pageIndex = pageIndex,
        lifecycleOwner = LocalLifecycleOwner.current,
        onPlaybackError = { error ->
            Log.e("VideoPlayer", "播放错误: $error")
        }
    )
}
```

### 使用 ExoPlayerPool

```kotlin
import com.xiaobai.composable.ExoPlayerPool

// 获取播放器
val player = ExoPlayerPool.getPlayer(context, videoId)

// 使用播放器
player.setMediaItem(MediaItem.fromUri(videoUri))
player.prepare()
player.play()

// 释放播放器（放回池中）
ExoPlayerPool.softRelease(context, player)
```

### 查看性能统计

```kotlin
// 打印性能报告
ExoPlayerPool.logPerformanceReport()

// 或获取结构化数据
val stats = ExoPlayerPool.getPerformanceStats()
Log.d("Performance", "复用率: ${stats.reuseRate}%")
```

---

## 📊 性能指标

| 指标 | 优化前 | 优化后 | 提升 |
|------|--------|--------|------|
| **内存占用** (50视频) | 400MB | 40MB | ⬇️ 90% |
| **播放器创建次数** | 50 | 9 | ⬇️ 82% |
| **首帧加载时间** | 300ms | 100ms | ⬇️ 67% |
| **复用率** | 0% | 82% | ⬆️ - |

详细数据请参考：[性能测试指南](./docs/exoplayer-pool/PERFORMANCE_TESTING_GUIDE.md)

---

## 🔗 依赖

```kotlin
// build.gradle.kts
dependencies {
    // ExoPlayer (Media3)
    implementation("androidx.media3:media3-exoplayer:1.2.0")
    implementation("androidx.media3:media3-ui:1.2.0")
    
    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    
    // Coil (图片加载)
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
}
```

---

## 🤝 贡献

如果您想为这个模块贡献代码或文档，请：

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

---

## 📝 更新日志

### v2.0 (2025-11-21)
- ✨ 新增完整的性能统计系统
- ✨ 新增结构化数据类
- 🐛 修复后台恢复黑屏问题
- ⚡ 优化状态保留策略
- 📚 完善文档体系

### v1.0 (2025-11-20)
- 🎉 初始版本
- ✨ 实现 ExoPlayerPool 对象池
- ✨ 实现 VideoPlayer 组件
- ✨ 实现其他 UI 组件

---

## 📄 许可证

本项目采用 MIT 许可证。

---

**模块维护**: TikTokDemo Team  
**最后更新**: 2025-11-21  
**版本**: v2.0

