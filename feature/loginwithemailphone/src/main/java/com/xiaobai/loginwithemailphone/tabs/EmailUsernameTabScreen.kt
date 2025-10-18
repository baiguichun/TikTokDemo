package com.xiaobai.loginwithemailphone.tabs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
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
import com.xiaobai.loginwithemailphone.suggestedDomainList
import com.xiaobai.theme.R
import com.xiaobai.theme.SeparatorColor
import com.xiaobai.theme.SubTextColor
import com.xiaobai.theme.fontFamily

/**
 * 邮箱登录页面
 * @param viewModel 登录邮箱手机号ViewModel
 */
@Composable
fun EmailUsernameTabScreen(viewModel: LoginWithEmailPhoneViewModel) {
    val email by viewModel.email.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(top = 42.dp, start = 28.dp, end = 28.dp)
                .weight(1f),
        ) {
            EmailField(email, viewModel)
            8.dp.Space()
            PrivacyPolicyText {}
            16.dp.Space()
            CustomButton(
                buttonText = stringResource(id = R.string.next),
                modifier = Modifier.fillMaxWidth(),
                isEnabled = email.first.isNotEmpty()
            ) {

            }
        }

        DomainSuggestion {
            viewModel.onTriggerEvent(
                LoginWithEmailPhoneEvent.OnChangeEmailEntry(
                    "${email.first.substringBefore("@")}$it"
                )
            )
        }
        16.dp.Space()

    }
}




/**
 * 邮箱输入框
 * @param email 邮箱
 * @param viewModel 登录邮箱手机号ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailField(email: Pair<String, String?>, viewModel: LoginWithEmailPhoneViewModel) {
    val currentPage by viewModel.settledPage.collectAsState()
    val focusRequester = remember { FocusRequester() }
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = email.first,
        textStyle = MaterialTheme.typography.labelLarge,
        onValueChange = { viewModel.onTriggerEvent(LoginWithEmailPhoneEvent.OnChangeEmailEntry(it)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            focusedIndicatorColor = SubTextColor,
            unfocusedIndicatorColor = SubTextColor,
        ),
        placeholder = {
            Text(
                text = stringResource(id = R.string.enter_email_address_or_username),
                style = MaterialTheme.typography.labelLarge,
                color = SubTextColor
            )
        },
        trailingIcon = {//显示清空按钮（当有输入内容时）
            IconButton(onClick = {
                viewModel.onTriggerEvent(
                    LoginWithEmailPhoneEvent.OnChangeEmailEntry(
                        ""
                    )
                )
            }) {
                if (email.first.isNotEmpty()) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cancel),
                        contentDescription = null
                    )
                }

            }
        }
    )
    //当页面切换到第1页时自动请求焦点
    LaunchedEffect(key1 = currentPage) {
        if (currentPage == 1) {
            focusRequester.requestFocus()
        }
    }
}



/**
 * 隐私政策
 * @param onClickAnnotation 点击注解
 */
@Composable
fun PrivacyPolicyText(onClickAnnotation: (String) -> Unit) {
    val annotatedString = buildAnnotatedString {
        append(stringResource(id = R.string.by_continuing_you_agree))

        pushStringAnnotation(
            tag = AppContract.Annotate.ANNOTATED_TAG,
            annotation = AppContract.Annotate.ANNOTATED_TERMS_OF_SERVICE
        )
        withStyle(
            style = SpanStyle(
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
        ) { append(" ${stringResource(id = R.string.terms_of_service)}") }
        pop()

        append(stringResource(id = R.string.and_confirm_that_you_have_read))

        pushStringAnnotation(
            tag = AppContract.Annotate.ANNOTATED_TAG,
            annotation = AppContract.Annotate.ANNOTATED_PRIVACY_POLICY
        )
        withStyle(
            style = SpanStyle(
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
        ) { append(" ${stringResource(id = R.string.privacy_policy)}") }
        pop()
    }

    ClickableText(
        text = annotatedString, onClick = { offset ->
            annotatedString.getStringAnnotations(
                tag = AppContract.Annotate.ANNOTATED_TAG, start = offset, end = offset
            ).firstOrNull()?.let { annotation ->
                onClickAnnotation(annotation.item)
            }
        }, style = TextStyle(
            fontFamily = fontFamily
        )
    )
}



/**
 * 推荐域名列表
 * @param onClickDomain 点击域名
 */
@Composable
fun DomainSuggestion(onClickDomain: (String) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(suggestedDomainList) { domain ->
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(2.dp),
                        color = SeparatorColor
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                ClickableText(
                    text = AnnotatedString(domain),
                    style = MaterialTheme.typography.titleSmall,
                    onClick = {
                        onClickDomain(domain)
                    })
            }
        }
    }
}