package com.xiaobai.loginwithemailphone

import com.xiaobai.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginWithEmailPhoneViewModel @Inject constructor() : BaseViewModel<ViewState, LoginWithEmailPhoneEvent>() {
    //当前页面
    private val _settledPage = MutableStateFlow<Int?>(null)
    val settledPage = _settledPage.asStateFlow()
    //电话号码
    private val _phoneNumber =
        MutableStateFlow<Pair<String, String?>>(Pair("", null))  //Pair(value,errorMsg)
    val phoneNumber = _phoneNumber.asStateFlow()
    //国家代码
    private val _dialCode = MutableStateFlow<Pair<String, String?>>(Pair("CN +86", null))
    val dialCode = _dialCode.asStateFlow()
    //邮箱
    private val _email = MutableStateFlow<Pair<String, String?>>(Pair("", null))
    val email = _email.asStateFlow()


    override fun onTriggerEvent(event: LoginWithEmailPhoneEvent) {
        when (event) {
            is LoginWithEmailPhoneEvent.EventPageChange -> _settledPage.value = event.settledPage
            is LoginWithEmailPhoneEvent.OnChangeEmailEntry -> _email.value =
                _email.value.copy(first = event.newValue)
            is LoginWithEmailPhoneEvent.OnChangePhoneNumber -> _phoneNumber.value =
                _phoneNumber.value.copy(first = event.newValue)
        }
    }


}