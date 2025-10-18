package com.xiaobai.commentlisting

import androidx.lifecycle.viewModelScope
import com.xiaobai.core.base.BaseViewModel
import com.xiaobai.domain.comment.GetCommentOnVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentListViewModel @Inject constructor(
    private val getCommentOnVideoUseCase: GetCommentOnVideoUseCase
) : BaseViewModel<ViewState, CommentEvent>() {

    init {
        getContentCreator()
    }

    private fun getContentCreator() {
        viewModelScope.launch {
            getCommentOnVideoUseCase("vid").collect {
                updateState(ViewState(comments = it))
            }
        }
    }

    override fun onTriggerEvent(event: CommentEvent) {
    }


}