package com.xiaobai.core.utils

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * 用于禁用点击时的波纹反馈效果
 */
class DisableRippleInteractionSource: MutableInteractionSource {
    override val interactions: Flow<Interaction>
        get() = emptyFlow()

    override suspend fun emit(interaction: Interaction) {
    }

    override fun tryEmit(interaction: Interaction): Boolean = true
}