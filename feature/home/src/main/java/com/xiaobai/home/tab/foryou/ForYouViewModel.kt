package com.xiaobai.home.tab.foryou

import androidx.lifecycle.viewModelScope
import com.xiaobai.core.base.BaseViewModel
import com.xiaobai.domain.foryou.GetForYouPageFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForYouViewModel @Inject constructor(
    private val getForYouPageFeedUseCase: GetForYouPageFeedUseCase
) : BaseViewModel<ViewState, ForYouEvent>() {
    init {
        getForYouPageFeeds()
    }

    override fun onTriggerEvent(event: ForYouEvent) {
    }

    private fun getForYouPageFeeds() {
        viewModelScope.launch {
            getForYouPageFeedUseCase().collect {
                updateState(ViewState(forYouPageFeed = it))
            }
        }
    }


}

//@HiltViewModel: 这是 Hilt 依赖注入框架提供的注解，用于标记 ForYouViewModel 类作为一个 ViewModel 组件
//使该类能够被 Hilt 自动管理和注入
//提供依赖注入功能，可以方便地注入 Repository 或其他依赖项
//与 @AndroidEntryPoint 配合使用，实现 ViewModel 的自动实例化和生命周期管理
//这是使用 Hilt 进行依赖注入的标准做法，简化了 ViewModel 的创建和管理过程