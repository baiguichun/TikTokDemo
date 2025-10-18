package com.xiaobai.core.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<ViewState, Event> : ViewModel() {

    private val _viewState = MutableStateFlow<ViewState?>(null)
    val viewState = _viewState.asStateFlow()

    fun updateState(viewState: ViewState) {
        _viewState.value = viewState
    }

    abstract fun onTriggerEvent(event: Event)

}

//设计模式：
//采用 单向数据流 架构模式：
//UI 触发事件 → onTriggerEvent 处理事件 → updateState 更新状态 → UI 响应新状态
//实现了状态驱动的响应式编程模型




