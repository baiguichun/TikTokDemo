package com.xiaobai.loginwithemailphone.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.xiaobai.composable.CustomButton
import com.xiaobai.core.AppContract
import com.xiaobai.core.extension.Space
import com.xiaobai.loginwithemailphone.LoginWithEmailPhoneEvent
import com.xiaobai.loginwithemailphone.LoginWithEmailPhoneViewModel
import com.xiaobai.theme.R
import com.xiaobai.theme.SeparatorColor
import com.xiaobai.theme.SubTextColor
import com.xiaobai.theme.fontFamily


/**
 * 手机号登录页面
 * @param viewModel 登录邮箱手机号ViewModel
 */
@Composable
fun PhoneTabScreen(viewModel: LoginWithEmailPhoneViewModel) {
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val dialCode by viewModel.dialCode.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp)
            .padding(top = 52.dp)
    ) {
        PhoneNumberField(viewModel, phoneNumber, dialCode)
        8.dp.Space()
        InfoAnnotatedText {
        }
        16.dp.Space()
        CustomButton(
            buttonText = stringResource(id = R.string.send_code),
            modifier = Modifier.fillMaxWidth(),
            isEnabled = phoneNumber.first.isNotEmpty()
        ) {

        }
    }
}


/**
 * 手机号输入框
 * @param viewModel 登录邮箱手机号ViewModel
 * @param phoneNumber 电话号码
 * @param dialCode 国家代码
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhoneNumberField(
    viewModel: LoginWithEmailPhoneViewModel,
    phoneNumber: Pair<String, String?>,
    dialCode: Pair<String, String?>
) {
    //当前页面状态
    val currentPage by viewModel.settledPage.collectAsState()
    //焦点
    val focusRequester = remember { FocusRequester() }
    TextField(//文本输入框
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),//让该文本字段能够接收和管理焦点状态
        value = phoneNumber.first,//电话号码值
        onValueChange = {//输入变化时触发
            viewModel.onTriggerEvent(LoginWithEmailPhoneEvent.OnChangePhoneNumber(it))
        },
        textStyle = MaterialTheme.typography.labelLarge,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),//设置键盘类型为电话号码类型
        singleLine = true,//单行
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,//设置文本框容器背景色为透明
            focusedIndicatorColor = SubTextColor,//设置获得焦点时输入框底部指示线的颜色为
            unfocusedIndicatorColor = SubTextColor,//设置失去焦点时输入框底部指示线的颜色为
        ),
        placeholder = {//占位符文本
            Text(
                text = stringResource(id = R.string.phone_number),
                style = MaterialTheme.typography.labelLarge
            )
        },
        leadingIcon = {//显示国家代码和下拉图标
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = dialCode.first)  //static for now
                Icon(
                    painter = painterResource(id = R.drawable.ic_down_more),
                    contentDescription = null,
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(12.dp)
                        .background(color = SeparatorColor)
                )

            }
        },
        trailingIcon = {//显示清空按钮（当有输入内容时）
            IconButton(onClick = {
                viewModel.onTriggerEvent(
                    LoginWithEmailPhoneEvent.OnChangePhoneNumber(
                        ""
                    )
                )
            }) {
                if (phoneNumber.first.isNotEmpty()) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cancel),
                        contentDescription = null
                    )
                }

            }
        }
    )
    //当页面切换到第0页时自动请求焦点
    LaunchedEffect(key1 = currentPage) {
        if (currentPage == 0) {
            focusRequester.requestFocus()
        }
    }
}




/**
 * 手机号登录说明信息
 * @param onClickLearnMore 点击了解更多
 */
@Composable
fun InfoAnnotatedText(onClickLearnMore: () -> Unit) {
    val annotatedString = buildAnnotatedString {
        append(stringResource(id = R.string.phone_number_info))
        pushStringAnnotation(
            tag = AppContract.Annotate.ANNOTATED_TAG,
            annotation = AppContract.Annotate.ANNOTATED_LEARN_MORE
        )
        withStyle(
            style = SpanStyle(
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
        ) { append(" ${stringResource(id = R.string.learn_more)}") }
        pop()
    }

    ClickableText(
        text = annotatedString, onClick = { offset ->
            annotatedString.getStringAnnotations(
                tag = AppContract.Annotate.ANNOTATED_TAG, start = offset, end = offset
            ).firstOrNull()?.let { annotation ->
                if (annotation.item == AppContract.Annotate.ANNOTATED_LEARN_MORE) {
                    onClickLearnMore()
                }
            }
        }, style = TextStyle(
            color = SubTextColor,
            fontFamily = fontFamily
        )
    )
}