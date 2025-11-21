# 🎬 ExoPlayerPool 生命周期管理指南

> **版本**: v2.0  
> **最后更新**: 2025-11-21  
> **相关文档**: [ExoPlayerPool 技术文档](./EXOPLAYER_POOL_DOCUMENTATION.md)

## 📍 `releaseAll()` 调用位置说明

### **方案 1：MainActivity.onDestroy() ⭐ 推荐**

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            // 只在 Activity 真正结束时释放
            ExoPlayerPool.releaseAll()
        }
    }
}
```

#### **触发场景：**
- ✅ 用户按返回键退出应用
- ✅ 用户从最近任务列表滑掉应用
- ✅ 系统因内存不足强制关闭应用

#### **为什么要检查 `isFinishing`？**
```kotlin
// ✅ 正确：只在真正退出时释放
if (isFinishing) {
    ExoPlayerPool.releaseAll()
}

// ❌ 错误：屏幕旋转也会触发 onDestroy
// 这会导致播放器被错误释放
ExoPlayerPool.releaseAll()
```

#### **优点：**
- ✅ 确保应用退出时资源被完全释放
- ✅ 避免内存泄漏
- ✅ 下次启动是干净的状态

---

### **方案 2：MyApp.onTrimMemory() ⭐⭐ 强烈推荐**

```kotlin
@HiltAndroidApp
class MyApp : Application() {
    
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        
        when (level) {
            // 应用运行时内存不足
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> {
                Log.w(TAG, "内存不足，释放播放器池")
                ExoPlayerPool.releaseAll()
            }
            
            // 应用在后台且系统内存极度不足
            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND,
            ComponentCallbacks2.TRIM_MEMORY_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                Log.e(TAG, "系统内存极度不足，释放所有播放器")
                ExoPlayerPool.releaseAll()
            }
        }
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        ExoPlayerPool.releaseAll()
    }
}
```

#### **触发场景：**
- ✅ 系统内存不足时自动触发
- ✅ 应用切换到后台时可能触发
- ✅ 系统需要回收内存时触发

#### **内存等级说明：**

| 等级 | 说明 | 建议操作 |
|------|------|---------|
| `TRIM_MEMORY_UI_HIDDEN` | UI 不可见 | 依赖超时清理，不需要立即释放 |
| `TRIM_MEMORY_RUNNING_MODERATE` | 运行中，内存接近不足 | 释放所有播放器 |
| `TRIM_MEMORY_RUNNING_LOW` | 运行中，内存不足 | 释放所有播放器 |
| `TRIM_MEMORY_RUNNING_CRITICAL` | 运行中，内存严重不足 | 释放所有播放器 |
| `TRIM_MEMORY_BACKGROUND` | 后台，内存中等 | 释放所有播放器 |
| `TRIM_MEMORY_MODERATE` | 后台，内存不足 | 释放所有播放器 |
| `TRIM_MEMORY_COMPLETE` | 后台，内存极度不足 | 释放所有播放器 |

#### **优点：**
- ✅ 响应系统内存压力
- ✅ 避免应用被系统杀死
- ✅ 提升用户体验（应用不会因内存不足崩溃）
- ✅ 系统级别的资源管理

---

### **方案 3：ProcessLifecycleOwner（可选）**

如果想在应用完全进入后台时释放资源：

```kotlin
@HiltAndroidApp
class MyApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 监听应用级别的生命周期
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                // 应用完全进入后台（所有 Activity 都不可见）
                Log.i(TAG, "应用进入后台")
                // 可选：延迟释放，避免快速切回时重新创建
                // Handler(Looper.getMainLooper()).postDelayed({
                //     ExoPlayerPool.releaseAll()
                // }, 30_000) // 30秒后释放
            }
            
            override fun onStart(owner: LifecycleOwner) {
                // 应用回到前台
                Log.i(TAG, "应用回到前台")
            }
        })
    }
}
```

#### **触发场景：**
- ✅ 所有 Activity 都不可见时
- ✅ 用户切换到其他应用
- ✅ 用户按 Home 键

#### **优点：**
- ✅ 应用级别的生命周期管理
- ✅ 可以延迟释放，避免频繁创建/销毁

#### **缺点：**
- ⚠️ 如果用户快速切回，需要重新创建播放器
- ⚠️ 增加了复杂度

---

## 🎯 **推荐的完整方案**

### **组合使用：MainActivity.onDestroy() + MyApp.onTrimMemory()**

```kotlin
// MainActivity.kt - 处理应用退出
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            Log.i("MainActivity", "应用退出，释放播放器池")
            ExoPlayerPool.releaseAll()
        }
    }
}

// MyApp.kt - 处理内存压力
@HiltAndroidApp
class MyApp : Application() {
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL,
            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND,
            ComponentCallbacks2.TRIM_MEMORY_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                Log.w("MyApp", "内存压力，释放播放器池 (level: $level)")
                ExoPlayerPool.releaseAll()
            }
        }
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        Log.e("MyApp", "低内存警告，释放播放器池")
        ExoPlayerPool.releaseAll()
    }
}
```

---

## 📊 **执行流程对比**

### **场景 1：正常退出应用**

```
用户按返回键 / 滑掉应用
    ↓
MainActivity.onDestroy() 触发
    ↓
检查 isFinishing = true
    ↓
ExoPlayerPool.releaseAll() ✅
    ↓
应用进程结束
```

### **场景 2：系统内存不足**

```
系统内存不足
    ↓
MyApp.onTrimMemory(TRIM_MEMORY_RUNNING_LOW) 触发
    ↓
ExoPlayerPool.releaseAll() ✅
    ↓
释放 ~40MB 内存
    ↓
应用继续运行（避免被系统杀死）
```

### **场景 3：屏幕旋转（配置变更）**

```
屏幕旋转
    ↓
MainActivity.onDestroy() 触发
    ↓
检查 isFinishing = false ⚠️
    ↓
不释放播放器 ✅
    ↓
新 Activity 创建，复用播放器池
```

### **场景 4：切换到其他应用**

```
用户按 Home 键
    ↓
ExoPlayerPool 的 5 分钟超时机制开始计时
    ↓
5 分钟后自动清理空闲播放器 ✅
    ↓
如果系统内存不足 → onTrimMemory() 触发
    ↓
立即释放所有播放器 ✅
```

---

## 🔍 **调试和监控**

### **查看释放日志**

```bash
# Logcat 过滤
tag:ExoPlayerPool OR tag:MainActivity OR tag:MyApp

# 关键日志：
I/MainActivity: 应用退出，释放播放器池
I/ExoPlayerPool: 释放所有播放器 (活跃: 2, 空闲: 3)
W/MyApp: 内存压力，释放播放器池 (level: 10)
E/MyApp: 低内存警告，释放播放器池
```

### **验证释放是否成功**

```kotlin
// 在调试版本中添加验证
override fun onDestroy() {
    super.onDestroy()
    if (isFinishing) {
        ExoPlayerPool.releaseAll()
        
        // 验证池状态
        Log.d("MainActivity", "池状态: ${ExoPlayerPool.getPoolStatus()}")
        // 应该输出: "活跃播放器: 0, 空闲播放器: 0, 总计: 0/5"
    }
}
```

---

## ⚠️ **注意事项**

### **1. 不要在这些地方调用 `releaseAll()`**

❌ **onPause() / onStop()**
```kotlin
// ❌ 错误：用户只是切换到其他应用，可能马上切回来
override fun onPause() {
    ExoPlayerPool.releaseAll() // 太激进
}
```

❌ **页面切换时**
```kotlin
// ❌ 错误：只是切换页面，不是退出应用
LaunchedEffect(currentRoute) {
    if (currentRoute != "home") {
        ExoPlayerPool.releaseAll() // 不应该释放
    }
}
```

❌ **每次视频播放完成时**
```kotlin
// ❌ 错误：视频播放完成不代表不再需要播放器
DisposableEffect(video) {
    onDispose {
        ExoPlayerPool.releaseAll() // 应该用 softRelease
    }
}
```

### **2. releaseAll() vs softRelease()**

| 方法 | 使用场景 | 效果 |
|------|---------|------|
| `softRelease()` | 单个视频播放完成 | 播放器移入空闲池，可复用 |
| `releaseAll()` | 应用退出或内存不足 | 释放所有播放器，清空池 |

```kotlin
// ✅ 正确：单个视频使用 softRelease
DisposableEffect(video.videoId) {
    onDispose {
        ExoPlayerPool.softRelease(context, exoPlayer) // ✅
    }
}

// ✅ 正确：应用退出使用 releaseAll
override fun onDestroy() {
    if (isFinishing) {
        ExoPlayerPool.releaseAll() // ✅
    }
}
```

---

## 🎉 **总结**

### **最佳实践**

1. ✅ **MainActivity.onDestroy()** - 处理应用正常退出
2. ✅ **MyApp.onTrimMemory()** - 响应系统内存压力
3. ✅ **MyApp.onLowMemory()** - 兼容旧版 API
4. ✅ 依赖 **5分钟超时机制** - 自动清理长期不用的播放器

### **效果**

- 🏆 应用退出时资源完全释放
- 🛡️ 内存不足时自动释放资源
- 🚀 正常使用时保持高性能
- ⚡ 自动平衡性能和内存占用

### **内存节省**

| 场景 | 释放前 | 释放后 | 节省 |
|------|--------|--------|------|
| 应用退出 | 40MB | 0MB | 100% |
| 内存不足 | 40MB | 0MB | 100% |
| 5分钟后台 | 40MB | 0MB | 100% |

---

## 📚 相关文档

### 深入学习
- 📘 [ExoPlayerPool 技术文档](./EXOPLAYER_POOL_DOCUMENTATION.md) - 完整的技术文档和 API 说明
- 📹 [VideoPlayer 优化报告](../videoplayer/VIDEO_PLAYER_OPTIMIZATION.md) - VideoPlayer 组件的优化细节
- 📊 [性能测试指南](./PERFORMANCE_TESTING_GUIDE.md) - 如何测试和验证性能
- 📈 [性能统计使用指南](./PERFORMANCE_STATS_USAGE.md) - 性能数据的使用方法

### 实现文件
- `../../src/main/java/com/xiaobai/composable/ExoPlayerPool.kt` - 对象池实现
- `../../../../app/src/main/java/com/xiaobai/tiktokdemo/MainActivity.kt` - Activity 生命周期管理
- `../../../../app/src/main/java/com/xiaobai/tiktokdemo/MyApp.kt` - Application 内存管理

---

## 🔗 快速导航

| 想了解... | 查看文档 |
|----------|---------|
| ExoPlayerPool 的工作原理？ | [技术文档 - 工作原理](./EXOPLAYER_POOL_DOCUMENTATION.md#⚙️-工作原理) |
| 如何监控性能？ | [性能测试指南](./PERFORMANCE_TESTING_GUIDE.md) |
| 性能指标如何解读？ | [性能统计使用](./PERFORMANCE_STATS_USAGE.md#📊-数据解读指南) |
| 如何优化 VideoPlayer？ | [VideoPlayer 优化](../videoplayer/VIDEO_PLAYER_OPTIMIZATION.md) |

---

**更新时间：** 2025-11-21  
**版本：** v2.0

