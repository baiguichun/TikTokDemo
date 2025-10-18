package com.xiaobai.cameramedia

import androidx.lifecycle.viewModelScope
import com.xiaobai.core.base.BaseViewModel
import com.xiaobai.domain.cameramedia.GetTemplateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraMediaViewModel @Inject constructor(
    private val getTemplateUseCase: GetTemplateUseCase
) : BaseViewModel<ViewState, CameraMediaEvent>() {

    init {
        getTemplates()
    }

    override fun onTriggerEvent(event: CameraMediaEvent) {
        when (event) {
            CameraMediaEvent.EventFetchTemplate -> getTemplates()
        }
    }

    private fun getTemplates() {
        viewModelScope.launch {
            getTemplateUseCase().collect {
                updateState((viewState.value ?: ViewState()).copy(templates = it))
            }
        }
    }


}