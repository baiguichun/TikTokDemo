package com.xiaobai.loginwithemailphone

import androidx.annotation.StringRes
import com.xiaobai.theme.R

data class ViewState(
    val isLoading: Boolean? = null,
    val error: String? = null,
)

sealed class LoginWithEmailPhoneEvent {
    data class EventPageChange(val settledPage: Int) : LoginWithEmailPhoneEvent()
    data class OnChangePhoneNumber(val newValue: String) : LoginWithEmailPhoneEvent()
    data class OnChangeEmailEntry(val newValue: String) : LoginWithEmailPhoneEvent()
}


enum class LoginPages(@StringRes val title: Int) {
    PHONE(title = R.string.phone),
    EMAIL_USERNAME(title = R.string.email_username)
}

val suggestedDomainList =
    arrayListOf("@gmail.com", "@hotmail.com", "@outlook.com", "@yahoo.com", "@icloud.com")