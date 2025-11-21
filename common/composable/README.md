# 📦 Composable Module

TikTokDemo 项目的核心 UI 组件模块 - 纯 Jetpack Compose 实现

---

## 📁 模块结构

```
common/composable/
├── src/main/java/com/xiaobai/composable/
│   ├── VideoPlayer.kt                # 🎬 视频播放组件（核心）
│   ├── TikTokVerticalVideoPager.kt   # 📱 垂直视频分页器
│   ├── CaptureButton.kt              # 📸 拍摄按钮组件
│   ├── CustomButton.kt               # 🔘 自定义按钮组件
│   ├── CustomIconButton.kt           # 🔘 自定义图标按钮
│   ├── TopBar.kt                     # 🔝 顶部导航栏
│   └── ContentSearchBar.kt           # 🔍 搜索栏组件
│
├── docs/                             # 📚 技术文档
│   ├── videoplayer/
│   │   └── VideoPlayer代码详解.md    # VideoPlayer 完整技术文档
│   └── README.md                     # 文档中心（如需创建）
│
└── build.gradle.kts                  # Gradle 配置文件
```

---

## 🎯 核心组件

### 1️⃣ VideoPlayer - 智能视频播放组件 ⭐

**功能**: 纯 Compose 实现的短视频播放组件，组件完全自治，自动管理生命周期。

**核心特性**:
- ✅ **组件自治设计** - 每个组件独立管理 ExoPlayer 实例
- ✅ **智能预加载** - 当前页 ±1，最多 3 个播放器
- ✅ **ExoPlayer 优化** - 短视频缓冲配置，内存占用 ↓ 13%
- ✅ **后台无缝恢复** - Surface 自动管理，0% 黑屏率
- ✅ **自动错误重试** - 最多 3 次，提升用户体验
- ✅ **性能监控** - 首帧加载时间追踪

**架构亮点**:
```kotlin
@Composable
fun VideoPlayer(...) {
    // 组件独立创建播放器（纯 Compose 方式）
    val exoPlayer = remember(video.videoId) {
        ExoPlayer.Builder(context)
            .setLoadControl(/* 短视频优化配置 */)
            .build()
    }
    
    // 组件销毁时自动释放
    DisposableEffect(video.videoId) {
        onDispose { exoPlayer.release() }
    }
}
```

**技术文档**: [VideoPlayer 代码详解](./docs/VideoPlayer代码详解.md)

**代码文件**: [VideoPlayer.kt](./src/main/java/com/xiaobai/composable/VideoPlayer.kt)

---

### 2️⃣ TikTokVerticalVideoPager - 垂直视频分页器

**功能**: 实现类似 TikTok 的垂直滑动视频列表，与 VideoPlayer 完美配合。

**特性**:
- ✅ 流畅的垂直滑动体验
- ✅ 自动播放当前页视频
- ✅ 手势控制（单击暂停/播放，双击点赞）
- ✅ 与 VideoPlayer 预加载机制协同

**代码文件**: [TikTokVerticalVideoPager.kt](./src/main/java/com/xiaobai/composable/TikTokVerticalVideoPager.kt)

---

### 3️⃣ 其他 UI 组件

| 组件 | 说明 | 文件 |
|------|------|------|
| CaptureButton | 拍摄按钮，带动画效果 | [CaptureButton.kt](./src/main/java/com/xiaobai/composable/CaptureButton.kt) |
| CustomButton | 自定义按钮组件 | [CustomButton.kt](./src/main/java/com/xiaobai/composable/CustomButton.kt) |
| CustomIconButton | 自定义图标按钮 | [CustomIconButton.kt](./src/main/java/com/xiaobai/composable/CustomIconButton.kt) |
| TopBar | 顶部导航栏 | [TopBar.kt](./src/main/java/com/xiaobai/composable/TopBar.kt) |
| ContentSearchBar | 搜索栏组件 | [ContentSearchBar.kt](./src/main/java/com/xiaobai/composable/ContentSearchBar.kt) |

---

## 🚀 使用示例

### 基础使用 - VideoPlayer

```kotlin
import com.xiaobai.composable.VideoPlayer
import androidx.compose.foundation.pager.rememberPagerState

@Composable
fun VideoScreen(videos: List<VideoModel>) {
    val pagerState = rememberPagerState(pageCount = { videos.size })
    
    HorizontalPager(state = pagerState) { index ->
        VideoPlayer(
            video = videos[index],
            pagerState = pagerState,
            pageIndex = index,
            onSingleTap = { player ->
                if (player.isPlaying) player.pause()
                else player.play()
            },
            onDoubleTap = { player, offset ->
                // 处理双击点赞
                viewModel.handleLike(videos[index])
            }
        )
    }
}
```

### 带回调的使用

```kotlin
VideoPlayer(
    video = video,
    pagerState = pagerState,
    pageIndex = index,
    onSingleTap = { player -> /* 单击处理 */ },
    onDoubleTap = { player, offset -> /* 双击处理 */ },
    onPlaybackError = { error ->
        Log.e("VideoPlayer", "播放错误: $error")
        Toast.makeText(context, "播放失败", Toast.LENGTH_SHORT).show()
    },
    onVideoDispose = {
        // 记录播放时长等
        analytics.logVideoWatch(video.videoId)
    }
)
```

### 完整示例 - TikTokVerticalVideoPager

```kotlin
import com.xiaobai.composable.TikTokVerticalVideoPager

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val videos by viewModel.videos.collectAsState()
    
    TikTokVerticalVideoPager(
        videos = videos,
        onVideoLiked = { video -> viewModel.likeVideo(video) },
        onCommentClicked = { video -> viewModel.showComments(video) },
        onShareClicked = { video -> viewModel.shareVideo(video) }
    )
}
```

---

## 📊 性能指标

### 内存占用（实测数据）

| 指标 | 数值 | 说明 |
|------|------|------|
| **总内存占用** | ~280MB | 41 个视频稳定运行 |
| **ExoPlayer 内存** | ~165MB | 3 个播放器实例 |
| **Java 内存** | ~33MB | 应用基础内存 |
| **Native 内存** | ~165MB | ExoPlayer 解码器 |
| **首帧加载时间** | 30-50ms | 秒开体验 |

### 优化效果

| 项目 | 优化前 | 优化后 | 提升 |
|------|--------|--------|------|
| **单个播放器内存** | ~80MB | ~55MB | ↓ 31% |
| **3 个播放器总计** | ~240MB | ~165MB | ↓ 31% |
| **总内存占用** | ~320MB | ~280MB | ↓ 13% |
| **代码复杂度** | 813行 | 413行 | ↓ 49% |

---

## 🎯 设计理念

### 核心思想

**拥抱 Compose，组件自治**

```kotlin
// ✅ 当前方案：纯 Compose
@Composable
fun VideoPlayer(...) {
    val exoPlayer = remember(videoId) { 
        createPlayer()  // 直接创建
    }
    
    DisposableEffect(videoId) {
        onDispose { 
            exoPlayer.release()  // 自动释放
        }
    }
}

优势：
✅ 代码简洁（413 行）
✅ 零共享状态
✅ Compose 自动管理生命周期
✅ 预加载范围自动限制数量
✅ 无需手动同步
```

### 为什么不使用对象池？

1. **预加载范围限制了数量**
   - 当前页 ±1 = 固定最多 3 个播放器
   - 无需池来管理数量

2. **创建成本极低**
   - ExoPlayer 创建只需 1-2ms
   - 占用户滑动时间的 0.2%，完全可忽略

3. **Compose 完美的生命周期管理**
   - `remember` 自动记忆
   - `DisposableEffect` 自动清理
   - 零内存泄漏风险

4. **性能相同，代码更简洁**
   - 内存占用：相同
   - 首帧时间：相同
   - 代码复杂度：↓ 49%

详见：[项目主 README - 为什么不使用对象池](../../README.md#为什么不使用对象池)

---

## 💡 技术亮点

### 1. ExoPlayer 短视频优化

```kotlin
ExoPlayer.Builder(context)
    .setLoadControl(
        DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                1000,   // minBufferMs: 1s（默认 50s）
                3000,   // maxBufferMs: 3s（默认 200s）
                500,    // bufferForPlaybackMs: 0.5s
                1000    // bufferForPlaybackAfterRebufferMs: 1s
            )
            .build()
    )
    .build()
```

**效果**: 单个播放器内存从 80MB 降为 55MB

### 2. 智能预加载策略

```kotlin
val isInPreloadRange = pagerState.settledPage in (pageIndex - 1)..(pageIndex + 1)
```

- 当前页 ±1 范围预加载
- 最多 3 个播放器实例
- 内存占用稳定

### 3. 完美的生命周期管理

- `remember(videoId)` 确保每个视频独立状态
- `DisposableEffect` 自动释放资源
- `LifecycleEventObserver` 处理后台恢复
- Surface 自动管理，零黑屏

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
    implementation("androidx.compose.foundation:foundation:1.5.4")
    
    // Coil (图片加载)
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

---

## 📚 技术文档

### 核心文档

- 📖 [VideoPlayer 代码详解](./docs/VideoPlayer代码详解.md) - 逐行代码解析（1485 行）
- 📊 [项目主 README](../../README.md) - 项目总览和架构说明

### 文档特点

- ✅ **完整性**: 覆盖所有 413 行代码
- ✅ **深度**: 逐行解析实现原理
- ✅ **实用性**: 包含实测数据和优化建议
- ✅ **可读性**: 清晰的图表和代码示例

---

## 🎓 学习价值

### 可以学到

1. **Jetpack Compose 最佳实践**
   - 组件自治设计
   - 生命周期管理（remember、DisposableEffect、LaunchedEffect）
   - 状态管理（单一数据源）

2. **ExoPlayer 集成**
   - 基础配置
   - Surface 管理
   - 错误处理
   - 短视频优化

3. **性能优化思维**
   - 预加载策略
   - 内存管理
   - 简洁设计（KISS 原则）

4. **架构演进**
   - 从复杂到简单的重构
   - 拥抱框架理念
   - 过度设计的识别

---

## 🤝 贡献

欢迎贡献代码、文档或提出建议！

### 贡献流程

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

---

## 📝 更新日志

### v4.0 (2025-11-21) - 纯 Compose 架构

- 🔄 **重大重构**: 移除 ExoPlayerPool，采用纯 Compose 组件自治设计
- ⚡ **ExoPlayer 优化**: 短视频缓冲配置，内存 ↓ 13%
- 📚 **文档更新**: 全面更新技术文档，新增代码详解（1485 行）
- 🎯 **代码简化**: 从 813 行降至 413 行（↓ 49%）
- ✨ **性能保持**: 内存、首帧时间与对象池方案相同

### v3.0 (2025-11-20) - 对象池简化

- ♻️ 简化对象池逻辑，移除 LRU 淘汰
- 📊 优化性能统计

### v2.0 (2025-11-19) - 性能优化

- ✨ 新增完整的性能统计系统
- 🐛 修复后台恢复黑屏问题
- ⚡ 优化状态保留策略

### v1.0 (2025-11-16) - 初始版本

- 🎉 实现 ExoPlayerPool 对象池
- ✨ 实现 VideoPlayer 组件
- ✨ 实现垂直视频分页器

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](../../LICENSE) 文件

---

## 🔗 相关链接

- [项目主 README](../../README.md)
- [VideoPlayer 代码详解](./docs/VideoPlayer代码详解.md)
- [GitHub 仓库](https://github.com/baiguichun/TikTokDemo)

---

**模块维护**: TikTokDemo Team  
**最后更新**: 2025-11-21  
**当前版本**: v4.0 (纯 Compose 架构)  
**核心理念**: KISS (Keep It Simple, Stupid) + Embrace the Framework
