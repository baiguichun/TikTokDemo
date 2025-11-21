# 📁 文档结构说明

本文档说明 TikTokDemo 项目的文档组织结构。

---

## 📂 目录结构

```
common/composable/
├── src/                                         # 源代码目录
│   └── main/java/com/xiaobai/composable/
│       ├── ExoPlayerPool.kt                     # ExoPlayer 对象池
│       ├── VideoPlayer.kt                       # VideoPlayer 组件
│       └── ...其他 Compose 组件
│
└── docs/                                        # 📚 文档目录
    ├── README.md                                # 📚 文档中心主页（总入口）
    ├── STRUCTURE.md                             # 📁 本文件 - 结构说明
    │
    ├── exoplayer-pool/                          # 🎬 ExoPlayerPool 相关文档
    │   ├── EXOPLAYER_POOL_DOCUMENTATION.md     # 📘 完整技术文档（核心）
    │   ├── EXOPLAYER_POOL_LIFECYCLE.md         # 🎬 生命周期管理指南
    │   ├── PERFORMANCE_TESTING_GUIDE.md        # 📊 性能测试指南
    │   └── PERFORMANCE_STATS_USAGE.md          # 📈 性能统计使用指南
    │
    └── videoplayer/                             # 📹 VideoPlayer 相关文档
        ├── VIDEOPLAYER_TECHNICAL_DOCUMENTATION.md  # 📘 VideoPlayer 技术文档
        └── VIDEO_PLAYER_OPTIMIZATION.md        # 📹 VideoPlayer 优化报告
```

**说明**: 文档与源代码位于同一个模块 (`common/composable`) 下，便于维护和查找。

---

## 📖 文档分类

### 🎬 ExoPlayerPool 文档 (`exoplayer-pool/`)

**主题**: ExoPlayer 对象池的设计、实现和使用

| 文档 | 内容概要 | 阅读时长 |
|------|---------|---------|
| [EXOPLAYER_POOL_DOCUMENTATION.md](./exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md) | • 完整的 API 文档<br>• 架构设计和工作原理<br>• 性能优化技术<br>• 故障排查和 FAQ | 30 分钟 |
| [EXOPLAYER_POOL_LIFECYCLE.md](./exoplayer-pool/EXOPLAYER_POOL_LIFECYCLE.md) | • `releaseAll()` 调用时机<br>• Activity/Application 生命周期管理<br>• 内存管理策略 | 15 分钟 |
| [PERFORMANCE_TESTING_GUIDE.md](./exoplayer-pool/PERFORMANCE_TESTING_GUIDE.md) | • 如何获取性能数据<br>• 测试方法和工具<br>• 数据验证 | 20 分钟 |
| [PERFORMANCE_STATS_USAGE.md](./exoplayer-pool/PERFORMANCE_STATS_USAGE.md) | • 性能统计 API 使用<br>• 数据解读指南<br>• 实际应用场景 | 15 分钟 |

**核心文件**: `EXOPLAYER_POOL_DOCUMENTATION.md` ⭐

---

### 📹 VideoPlayer 文档 (`videoplayer/`)

**主题**: VideoPlayer Compose 组件的设计、实现和使用

| 文档 | 内容概要 | 阅读时长 |
|------|---------|---------|
| [VIDEOPLAYER_TECHNICAL_DOCUMENTATION.md](./videoplayer/VIDEOPLAYER_TECHNICAL_DOCUMENTATION.md) | • 完整的 API 文档<br>• 状态管理和生命周期<br>• 性能优化技术<br>• 故障排查和 FAQ | 40 分钟 |
| [VIDEO_PLAYER_OPTIMIZATION.md](./videoplayer/VIDEO_PLAYER_OPTIMIZATION.md) | • VideoPlayer 组件优化细节<br>• 视频预加载实现<br>• 后台恢复处理<br>• 错误处理机制 | 20 分钟 |

---

## 🔗 文档间关系

```
┌─────────────────────────────────────────────────────────┐
│              docs/README.md (总入口)                     │
│         • 文档导航                                        │
│         • 快速开始指南                                    │
│         • 架构概览                                        │
└────────────────┬────────────────────────────────────────┘
                 │
        ┌────────┴────────┐
        │                 │
        ▼                 ▼
┌──────────────┐  ┌──────────────┐
│exoplayer-pool│  │ videoplayer  │
│    (4个文档)  │  │   (1个文档)   │
└──────┬───────┘  └──────┬───────┘
       │                 │
       │ 相互引用         │
       └────────┬────────┘
                │
                ▼
        跨目录链接已配置
```

---

## 🎯 文档使用指南

### 场景 1: 我是新手

**推荐阅读顺序**:
```
1. docs/README.md
   └─ 了解项目整体情况

2. exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md (概述章节)
   └─ 了解 ExoPlayerPool 是什么

3. videoplayer/VIDEO_PLAYER_OPTIMIZATION.md
   └─ 了解如何使用 VideoPlayer

4. exoplayer-pool/EXOPLAYER_POOL_LIFECYCLE.md
   └─ 了解生命周期管理
```

### 场景 2: 我要集成到自己的项目

**推荐阅读顺序**:
```
1. exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md (使用指南章节)
   └─ 学习 API 使用方法

2. videoplayer/VIDEO_PLAYER_OPTIMIZATION.md
   └─ 学习 VideoPlayer 集成

3. exoplayer-pool/EXOPLAYER_POOL_LIFECYCLE.md
   └─ 配置生命周期管理

4. exoplayer-pool/PERFORMANCE_TESTING_GUIDE.md
   └─ 验证集成效果
```

### 场景 3: 我要优化性能

**推荐阅读顺序**:
```
1. exoplayer-pool/PERFORMANCE_TESTING_GUIDE.md
   └─ 获取性能基线数据

2. exoplayer-pool/PERFORMANCE_STATS_USAGE.md
   └─ 解读性能数据

3. exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md (性能优化章节)
   └─ 学习优化技术

4. exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md (故障排查章节)
   └─ 解决具体问题
```

### 场景 4: 我遇到问题了

**推荐查找顺序**:
```
1. exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md (故障排查 + FAQ)
   └─ 查看常见问题

2. exoplayer-pool/EXOPLAYER_POOL_LIFECYCLE.md
   └─ 检查生命周期配置

3. videoplayer/VIDEO_PLAYER_OPTIMIZATION.md
   └─ 检查 VideoPlayer 使用

4. docs/README.md (快速导航)
   └─ 查找相关章节
```

---

## 📝 文档更新规范

### 更新某个文档时需要检查:

1. **更新版本信息**
   ```markdown
   > **版本**: v2.x
   > **最后更新**: YYYY-MM-DD
   ```

2. **检查相互引用链接**
   - 确保跨目录链接正确（`../` 相对路径）
   - 确保锚点链接有效

3. **更新 docs/README.md**
   - 如果是重大更新，需要在文档中心主页体现

4. **更新项目根 README.md**
   - 如果是重要功能更新，需要在项目主页体现

---

## 🔍 文档搜索

### 按主题查找

| 主题 | 文档位置 |
|------|---------|
| **对象池设计** | exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md → 核心设计 |
| **API 使用** | exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md → API 文档 |
| **生命周期** | exoplayer-pool/EXOPLAYER_POOL_LIFECYCLE.md |
| **性能测试** | exoplayer-pool/PERFORMANCE_TESTING_GUIDE.md |
| **性能数据** | exoplayer-pool/PERFORMANCE_STATS_USAGE.md |
| **VideoPlayer** | videoplayer/VIDEO_PLAYER_OPTIMIZATION.md |
| **预加载** | videoplayer/VIDEO_PLAYER_OPTIMIZATION.md → 预加载实现 |
| **后台恢复** | videoplayer/VIDEO_PLAYER_OPTIMIZATION.md → 后台切换 |
| **错误处理** | videoplayer/VIDEO_PLAYER_OPTIMIZATION.md → 错误处理 |
| **内存优化** | exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md → 性能优化 |
| **故障排查** | exoplayer-pool/EXOPLAYER_POOL_DOCUMENTATION.md → 故障排查 |

### 按文件类型查找

| 文件类型 | 文档位置 |
|---------|---------|
| **源代码文件** | 所有文档都在"相关文档"章节列出源代码位置 |
| **测试代码** | exoplayer-pool/PERFORMANCE_TESTING_GUIDE.md |
| **配置文件** | exoplayer-pool/EXOPLAYER_POOL_LIFECYCLE.md |

---

## 📊 文档统计

- **总文档数**: 7 个
- **总字数**: ~45,000 字
- **预计总阅读时长**: ~140 分钟
- **最后更新日期**: 2025-11-21
- **文档版本**: v2.0

---

## 🤝 贡献指南

### 添加新文档

1. **确定文档分类**
   - ExoPlayerPool 相关 → `exoplayer-pool/`
   - VideoPlayer 相关 → `videoplayer/`
   - 其他通用文档 → `docs/` 根目录

2. **创建文档**
   ```markdown
   # 📋 文档标题
   
   > **版本**: v1.0
   > **最后更新**: YYYY-MM-DD
   > **相关文档**: [链接](./相对路径.md)
   ```

3. **更新索引**
   - 在 `docs/README.md` 添加文档链接
   - 在 `docs/STRUCTURE.md` 更新结构说明
   - 在相关文档中添加交叉引用

4. **检查链接**
   - 确保所有相对路径正确
   - 确保锚点链接有效

---

**维护者**: TikTokDemo Team  
**最后更新**: 2025-11-21  
**版本**: v2.0

