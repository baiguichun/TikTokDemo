package com.xiaobai.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaobai.theme.SeparatorColor

/**
 * 自定义图标按钮
 *
 * @param modifier 修饰符
 * @param buttonText 按钮文字
 * @param icon 图标
 * @param iconSize 图标大小
 * @param iconTint 图标颜色
 * @param style 文字样式
 * @param shape 形状
 * @param height 高度
 * @param border 边框
 * @param containerColor 容器颜色
 * @param contentColor 文字颜色
 * @param borderColor 边框颜色
 * @param onClickButton 点击事件
 */
@Composable
fun CustomIconButton(
    modifier: Modifier = Modifier,
    buttonText: String,
    @DrawableRes icon: Int,
    iconSize: Dp = 22.dp,
    iconTint: Color = Color.Unspecified,
    style: TextStyle = MaterialTheme.typography.labelLarge,
    shape: Shape = RoundedCornerShape(2.dp),
    height: Dp = 44.dp,
    border: BorderStroke = BorderStroke(1.dp, SeparatorColor),
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    borderColor: Color = SeparatorColor,
    onClickButton: () -> Unit
) {
    Button(
        modifier = modifier.height(height),
        shape = shape,
        border = border,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        onClick = { onClickButton.invoke() },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
            Text(
                text = buttonText,
                style = style,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }

}