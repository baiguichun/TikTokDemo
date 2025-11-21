# 📚 TikTokDemo 技术文档中心

> **版本**: v2.0  
> **最后更新**: 2025-11-21

欢迎来到 TikTokDemo 技术文档中心！这里包含了项目的所有核心技术文档。

---

## 📖 文档目录

### 🎬 ExoPlayer 对象池文档 (`exoplayer-pool/`)

ExoPlayerPool 是本项目的核心优化组件，通过对象池技术实现播放器复用，显著降低内存占用和创建开销。

| 文档 | 说明 | 适合人群 |
|------|------|---------|
| [📘 ExoPlayerPool 技术文档](./exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md) | 完整的技术文档，包含架构设计、API 说明、工作原理等 | 所有开发者 ⭐ |
| [🎬 生命周期管理指南](./exoplayer-pool/EXOPLAYER_POOL_LIFECYCLE.md) | 详解何时释放播放器资源，如何管理内存 | 架构师、高级开发 |
| [📊 性能测试指南](./exoplayer-pool/PERFORMANCE_TESTING_GUIDE.md) | 如何测试和验证性能优化效果 | QA、性能工程师 |
| [📈 性能统计使用指南](./exoplayer-pool/PERFORMANCE_STATS_USAGE.md) | 如何使用和解读性能统计数据 | 所有开发者 |

**核心特性：**
- ✅ 播放器复用率 > 80%
- ✅ 内存节省 > 90%
- ✅ 首帧时间减少 67%
- ✅ 完善的性能监控

---

### 📹 VideoPlayer 组件文档 (`videoplayer/`)

VideoPlayer 是视频播放的 Compose 组件，实现了视频预加载、后台恢复、错误处理等功能。

| 文档 | 说明 | 适合人群 |
|------|------|---------|
| [📘 VideoPlayer 技术文档](./videoplayer/VIDEOPLAYER_TECHNICAL_DOCUMENTATION.md) | 完整的技术文档，包含 API、状态管理、生命周期等 | 所有开发者 ⭐ |
| [📹 VideoPlayer 优化报告](./videoplayer/VIDEO_PLAYER_OPTIMIZATION.md) | VideoPlayer 组件的优化细节和实现说明 | 前端开发、UI 工程师 |

**核心特性：**
- ✅ 视频预加载（当前页 ±1）
- ✅ 后台切换无缝恢复
- ✅ 自动错误重试（最多 3 次）
- ✅ 性能监控和日志追踪

---

## 🚀 快速导航

### 我是新手，从哪里开始？

```
1. 先看 [ExoPlayerPool 技术文档 - 概述](./exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md#📋-概述)
   了解 ExoPlayerPool 是什么，能解决什么问题
   
2. 再看 [VideoPlayer 技术文档](./videoplayer/VIDEOPLAYER_TECHNICAL_DOCUMENTATION.md)
   了解如何在 Compose 中使用 VideoPlayer
   
3. 最后看 [生命周期管理指南](./exoplayer-pool/EXOPLAYER_POOL_LIFECYCLE.md)
   了解如何正确管理资源生命周期
```

### 我想优化性能，看哪些文档？

```
1. [性能测试指南](./exoplayer-pool/PERFORMANCE_TESTING_GUIDE.md)
   学习如何测试和获取性能数据
   
2. [性能统计使用指南](./exoplayer-pool/PERFORMANCE_STATS_USAGE.md)
   学习如何解读和使用性能数据
   
3. [ExoPlayerPool 技术文档 - 性能优化](./exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md#🚀-性能优化)
   了解核心优化技术和原理
```

### 我遇到问题了，如何排查？

```
1. [ExoPlayerPool 技术文档 - 故障排查](./exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md#🔧-故障排查)
   查看常见问题和解决方案
   
2. [ExoPlayerPool 技术文档 - FAQ](./exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md#❓-faq)
   查看常见疑问的解答
   
3. [VideoPlayer 优化报告 - 常见问题](./videoplayer/VIDEO_PLAYER_OPTIMIZATION.md)
   查看 VideoPlayer 相关的问题
```

---

## 📊 关键性能指标

| 指标 | 优化前 | 优化后 | 提升 |
|------|--------|--------|------|
| **内存占用** (50视频) | 400MB | 40MB | ⬇️ 90% |
| **播放器创建次数** | 50 | 9 | ⬇️ 82% |
| **首帧加载时间** | 300ms | 100ms | ⬇️ 67% |
| **复用率** | 0% | 82% | ⬆️ - |
| **后台恢复黑屏率** | 15% | 0% | ⬇️ 100% |

详细数据请参考：[性能测试指南](./exoplayer-pool/PERFORMANCE_TESTING_GUIDE.md)

---

## 🏗️ 架构概览

```
┌─────────────────────────────────────────────────────────┐
│                    VideoPlayer                           │
│              (Compose 视频播放组件)                       │
│  • 视频渲染                                               │
│  • 生命周期管理                                           │
│  • 用户交互                                               │
└────────────────────┬────────────────────────────────────┘
                     │ 获取/释放播放器
                     ▼
┌─────────────────────────────────────────────────────────┐
│                  ExoPlayerPool                           │
│              (ExoPlayer 对象池)                          │
│  • 播放器复用                                             │
│  • 内存管理                                               │
│  • 性能监控                                               │
└────────────────────┬────────────────────────────────────┘
                     │ 管理
                     ▼
┌─────────────────────────────────────────────────────────┐
│                   ExoPlayer                              │
│              (Media3 播放器实例)                          │
│  • 视频解码                                               │
│  • 播放控制                                               │
│  • 状态管理                                               │
└─────────────────────────────────────────────────────────┘
```

详细架构请参考：[ExoPlayerPool 技术文档 - 核心设计](./exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md#🏗️-核心设计)

---

## 🔗 相关资源

### 源代码位置

| 组件 | 路径 |
|------|------|
| ExoPlayerPool | `../src/main/java/com/xiaobai/composable/ExoPlayerPool.kt` |
| VideoPlayer | `../src/main/java/com/xiaobai/composable/VideoPlayer.kt` |
| MainActivity | `../../../app/src/main/java/com/xiaobai/tiktokdemo/MainActivity.kt` |
| MyApp | `../../../app/src/main/java/com/xiaobai/tiktokdemo/MyApp.kt` |

**说明**: 本文档位于 `common/composable/docs/` 目录下，与源代码在同一个模块中。

### 外部资源

- [ExoPlayer 官方文档](https://exoplayer.dev/)
- [Jetpack Compose 官方文档](https://developer.android.com/jetpack/compose)
- [Android Performance Patterns](https://www.youtube.com/playlist?list=PLWz5rJ2EKKc9CBxr3BVjPTPoDPLdPIFCE)
- [Object Pool Pattern](https://en.wikipedia.org/wiki/Object_pool_pattern)

---

## 📝 文档版本历史

### v2.0 (2025-11-21)
- ✨ 新增完整的性能统计系统
- ✨ 新增结构化数据类（MemoryOptimization 等）
- 🐛 修复后台恢复黑屏问题
- ⚡ 优化状态保留策略（不调用 stop()）
- 📚 完善文档结构和组织
- 📂 文档按模块分类存放

### v1.0 (2025-11-20)
- 🎉 初始版本
- ✨ 实现基础对象池功能
- ✨ 实现 VideoPlayer 组件
- 📚 基础文档

---

## 🤝 贡献指南

如果您发现文档有任何问题或需要改进，请：

1. 提交 Issue 说明问题
2. 或直接提交 Pull Request

我们欢迎所有形式的贡献！

---

## 📄 许可证

本项目采用 MIT 许可证。

---

**文档维护**: TikTokDemo Team  
**最后更新**: 2025-11-21  
**文档版本**: v2.0

如有任何问题，请查看对应的技术文档或联系开发团队。

