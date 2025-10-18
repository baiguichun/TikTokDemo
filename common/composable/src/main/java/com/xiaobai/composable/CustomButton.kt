package com.xiaobai.composable

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 自定义按钮
 * @param modifier 修饰符
 * @param buttonText 按钮文字
 * @param style 文字样式
 * @param shape 形状
 * @param height 高度
 * @param containerColor 背景颜色
 * @param isEnabled 是否可用
 * @param onClickButton 点击事件
 */
@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    buttonText: String,
    style: TextStyle = MaterialTheme.typography.labelLarge,
    shape: Shape = RoundedCornerShape(2.dp),
    height: Dp = 44.dp,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    isEnabled: Boolean = true,
    onClickButton: () -> Unit
) {
    Button(
        modifier = modifier.height(height),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
        ),
        enabled = isEnabled,
        onClick = {
            onClickButton()
        }
    ) { Text(text = buttonText, style = style) }

}