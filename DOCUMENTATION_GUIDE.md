# 📚 TikTokDemo 文档导航指南

> 快速找到您需要的文档

---

## 🎯 文档位置

所有技术文档现在位于 **`common/composable/docs/`** 目录下，与核心组件的源代码在同一模块中。

```
common/composable/
├── src/                    # 源代码
│   └── main/java/com/xiaobai/composable/
│       ├── ExoPlayerPool.kt
│       ├── VideoPlayer.kt
│       └── ...
│
├── docs/                   # 📚 技术文档（所有文档都在这里）
│   ├── README.md          # 文档中心主页
│   ├── exoplayer-pool/    # ExoPlayerPool 文档
│   └── videoplayer/       # VideoPlayer 文档
│
└── README.md              # 模块说明
```

---

## 📖 快速访问

### 🏠 主入口

| 文档 | 路径 | 说明 |
|------|------|------|
| **项目主页** | [README.md](./README.md) | 项目概览、下载链接 |
| **模块说明** | [common/composable/README.md](./common/composable/README.md) | Composable 模块介绍 |
| **文档中心** | [common/composable/docs/README.md](./common/composable/docs/README.md) | 所有技术文档的入口 ⭐ |

---

### 📘 ExoPlayerPool 文档

所有 ExoPlayerPool 相关文档位于：`common/composable/docs/exoplayer-pool/`

| 文档 | 说明 | 何时查看 |
|------|------|---------|
| [EXOPLAYER_POOL_DOCUMENTATION.md](./common/composable/docs/exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md) | 完整技术文档（架构、API、原理） | 需要全面了解 ⭐ |
| [EXOPLAYER_POOL_LIFECYCLE.md](./common/composable/docs/exoplayer-pool/EXOPLAYER_POOL_LIFECYCLE.md) | 生命周期管理和资源释放 | 处理内存问题 |
| [PERFORMANCE_TESTING_GUIDE.md](./common/composable/docs/exoplayer-pool/PERFORMANCE_TESTING_GUIDE.md) | 性能测试方法 | 需要验证性能 |
| [PERFORMANCE_STATS_USAGE.md](./common/composable/docs/exoplayer-pool/PERFORMANCE_STATS_USAGE.md) | 性能数据使用和解读 | 分析性能数据 |

---

### 📹 VideoPlayer 文档

VideoPlayer 相关文档位于：`common/composable/docs/videoplayer/`

| 文档 | 说明 | 何时查看 |
|------|------|---------|
| [VIDEOPLAYER_TECHNICAL_DOCUMENTATION.md](./common/composable/docs/videoplayer/VIDEOPLAYER_TECHNICAL_DOCUMENTATION.md) | VideoPlayer 完整技术文档 | 深入了解 VideoPlayer ⭐ |
| [VIDEO_PLAYER_OPTIMIZATION.md](./common/composable/docs/videoplayer/VIDEO_PLAYER_OPTIMIZATION.md) | VideoPlayer 组件优化详解 | 查看优化总结 |

---

## 🚀 常见场景

### 场景 1: 我是新手，刚接触项目

**推荐路径**:
```
1. README.md (项目主页)
   ↓
2. common/composable/README.md (模块说明)
   ↓
3. common/composable/docs/README.md (文档中心)
   ↓
4. common/composable/docs/exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md
```

---

### 场景 2: 我要集成 ExoPlayerPool

**推荐路径**:
```
1. common/composable/docs/exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md
   (查看 "使用指南" 章节)
   ↓
2. common/composable/docs/exoplayer-pool/EXOPLAYER_POOL_LIFECYCLE.md
   (配置生命周期管理)
   ↓
3. common/composable/docs/exoplayer-pool/PERFORMANCE_TESTING_GUIDE.md
   (验证集成效果)
```

---

### 场景 3: 我要优化视频播放性能

**推荐路径**:
```
1. common/composable/docs/exoplayer-pool/PERFORMANCE_TESTING_GUIDE.md
   (获取性能数据)
   ↓
2. common/composable/docs/exoplayer-pool/PERFORMANCE_STATS_USAGE.md
   (分析性能数据)
   ↓
3. common/composable/docs/exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md
   (查看 "性能优化" 章节)
```

---

### 场景 4: 我遇到播放问题

**推荐路径**:
```
1. common/composable/docs/exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md
   (查看 "故障排查" 和 "FAQ" 章节)
   ↓
2. common/composable/docs/videoplayer/VIDEO_PLAYER_OPTIMIZATION.md
   (检查 VideoPlayer 配置)
   ↓
3. common/composable/docs/exoplayer-pool/EXOPLAYER_POOL_LIFECYCLE.md
   (检查生命周期管理)
```

---

### 场景 5: 我要查看源代码

**代码位置**:
```
common/composable/src/main/java/com/xiaobai/composable/
├── ExoPlayerPool.kt              # 对象池实现
├── VideoPlayer.kt                # VideoPlayer 组件
└── TikTokVerticalVideoPager.kt   # 垂直分页器
```

**对应文档**:
- ExoPlayerPool.kt → [EXOPLAYER_POOL_DOCUMENTATION.md](./common/composable/docs/exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md)
- VideoPlayer.kt → [VIDEO_PLAYER_OPTIMIZATION.md](./common/composable/docs/videoplayer/VIDEO_PLAYER_OPTIMIZATION.md)

---

## 🔍 搜索技巧

### 按关键词查找

| 关键词 | 文档位置 |
|--------|---------|
| **对象池** / **Object Pool** | [EXOPLAYER_POOL_DOCUMENTATION.md](./common/composable/docs/exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md) |
| **生命周期** / **Lifecycle** | [EXOPLAYER_POOL_LIFECYCLE.md](./common/composable/docs/exoplayer-pool/EXOPLAYER_POOL_LIFECYCLE.md) |
| **性能** / **Performance** | [PERFORMANCE_TESTING_GUIDE.md](./common/composable/docs/exoplayer-pool/PERFORMANCE_TESTING_GUIDE.md) |
| **统计** / **Stats** | [PERFORMANCE_STATS_USAGE.md](./common/composable/docs/exoplayer-pool/PERFORMANCE_STATS_USAGE.md) |
| **预加载** / **Preload** | [VIDEOPLAYER_TECHNICAL_DOCUMENTATION.md](./common/composable/docs/videoplayer/VIDEOPLAYER_TECHNICAL_DOCUMENTATION.md) |
| **后台恢复** / **Resume** | [VIDEOPLAYER_TECHNICAL_DOCUMENTATION.md](./common/composable/docs/videoplayer/VIDEOPLAYER_TECHNICAL_DOCUMENTATION.md) |
| **内存优化** / **Memory** | [EXOPLAYER_POOL_DOCUMENTATION.md](./common/composable/docs/exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md) |
| **错误处理** / **Error** | [VIDEOPLAYER_TECHNICAL_DOCUMENTATION.md](./common/composable/docs/videoplayer/VIDEOPLAYER_TECHNICAL_DOCUMENTATION.md) |
| **状态管理** / **State** | [VIDEOPLAYER_TECHNICAL_DOCUMENTATION.md](./common/composable/docs/videoplayer/VIDEOPLAYER_TECHNICAL_DOCUMENTATION.md) |
| **API 使用** / **API** | [VIDEOPLAYER_TECHNICAL_DOCUMENTATION.md](./common/composable/docs/videoplayer/VIDEOPLAYER_TECHNICAL_DOCUMENTATION.md) |

---

## 📊 文档统计

| 统计项 | 数值 |
|--------|------|
| 总文档数 | 8 个 |
| ExoPlayerPool 文档 | 4 个 |
| VideoPlayer 文档 | 2 个 |
| 总字数 | ~45,000 字 |
| 预计阅读时长 | ~160 分钟 |

---

## 🎓 学习路径推荐

### 初级（1-2天）
```
1. README.md - 了解项目
2. common/composable/README.md - 了解模块
3. common/composable/docs/README.md - 浏览文档概览
4. EXOPLAYER_POOL_DOCUMENTATION.md (概述章节) - 理解对象池概念
```

### 中级（3-5天）
```
1. EXOPLAYER_POOL_DOCUMENTATION.md (完整阅读) - 深入理解实现
2. VIDEO_PLAYER_OPTIMIZATION.md - 学习组件使用
3. EXOPLAYER_POOL_LIFECYCLE.md - 掌握资源管理
4. 动手实践：运行项目，查看日志
```

### 高级（1-2周）
```
1. PERFORMANCE_TESTING_GUIDE.md - 学习性能测试
2. PERFORMANCE_STATS_USAGE.md - 分析性能数据
3. 阅读源代码：ExoPlayerPool.kt、VideoPlayer.kt
4. 优化实践：调整参数，验证效果
5. 贡献代码：提交改进建议或 PR
```

---

## 🔗 外部资源

### 官方文档
- [ExoPlayer 官方文档](https://exoplayer.dev/)
- [Jetpack Compose 官方文档](https://developer.android.com/jetpack/compose)
- [Android Performance](https://developer.android.com/topic/performance)

### 设计模式
- [Object Pool Pattern](https://en.wikipedia.org/wiki/Object_pool_pattern)
- [Android Performance Patterns](https://www.youtube.com/playlist?list=PLWz5rJ2EKKc9CBxr3BVjPTPoDPLdPIFCE)

---

## 💡 提示

1. **书签收藏**: 建议将 [common/composable/docs/README.md](./common/composable/docs/README.md) 加入浏览器书签
2. **IDE 集成**: 在 Android Studio 中可以直接通过 Markdown 预览查看文档
3. **搜索功能**: 使用 `Cmd/Ctrl + F` 在文档中搜索关键词
4. **打印阅读**: 长文档可以导出 PDF 打印阅读

---

## 🤝 反馈

如果您：
- 找不到需要的信息
- 发现文档有误
- 有改进建议

请提交 Issue 或 PR，我们会及时处理！

---

**文档维护**: TikTokDemo Team  
**最后更新**: 2025-11-21  
**版本**: v2.0

祝您使用愉快！🎉

