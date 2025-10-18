package com.xiaobai.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.OutlinedBorderContainerBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaobai.theme.OverlayWhiteColor
import com.xiaobai.theme.R
/**
 * 搜索栏
 *
 * @param onClickNav 点击返回图标回调
 * @param onClickSearch 点击搜索图标回调
 * @param placeholder 占位符
 * @param readOnly 是否只读
 * @param marginHorizontal 水平间距
 * @param marginTop 垂直间距
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentSearchBar(
    onClickNav: () -> Unit,
    onClickSearch: () -> Unit,
    placeholder: String = "",
    readOnly: Boolean = true,
    marginHorizontal: Dp = 16.dp,
    marginTop: Dp = 8.dp
) {
    //配置文本输入框的颜色样式
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        //设置未获得焦点时的边框颜色
        unfocusedBorderColor = OverlayWhiteColor,
        //设置容器背景为透明
        containerColor = Color.Transparent,
        //文本颜色
        textColor = Color.White,
        //占位符文本颜色
        placeholderColor = Color.White
    )
    //圆角矩形形状
    val shape = RoundedCornerShape(8.dp)
    //跟踪和管理用户的点击、触摸等交互行为
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Row (
        modifier = Modifier
            .padding(horizontal = marginHorizontal)
            .padding(top = marginTop),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ){

        //返回图标
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp).clickable {
                onClickNav()
            }
        )

        //文本输入框
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .clickable { onClickSearch() },
            //空字符串
            value = "",
            //输入框内容改变时回调
            onValueChange = {},
            //单行输入
            singleLine = true,
            //是否只读
            readOnly = readOnly,
            decorationBox = {
                TextFieldDefaults.TextFieldDecorationBox(
                    value = "",
                    innerTextField = { },
                    enabled = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = textFieldColors,
                    shape = shape,
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Divider(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(16.dp), color = OverlayWhiteColor
                            )
                            Text(
                                text = stringResource(id = R.string.search),
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )
                        }
                    },
                    placeholder = {
                        Text(text = placeholder, style = MaterialTheme.typography.labelLarge)
                    },
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    contentPadding = PaddingValues(4.dp),
                    container = {
                        OutlinedBorderContainerBox(
                            interactionSource = interactionSource,
                            colors = textFieldColors,
                            shape = shape,
                            focusedBorderThickness = 1.dp,
                            unfocusedBorderThickness = 1.dp,
                            enabled = true,
                            isError = false
                        )
                    }
                )
            }
        )
    }
}