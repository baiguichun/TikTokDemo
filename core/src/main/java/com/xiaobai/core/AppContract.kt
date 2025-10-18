package com.xiaobai.core

object AppContract {
    /**
     * 平台类型
     */
    object Type {
        const val YOUTUBE = "type_youtube"
        const val INSTAGRAM = "type_instagram"
    }

    object Annotate {
        //标签注解
        const val ANNOTATED_TAG = "annotated_tag"
        //服务协议注解
        const val ANNOTATED_TERMS_OF_SERVICE = "annotated-terms_of_service"
        //隐私政策注解
        const val ANNOTATED_PRIVACY_POLICY = "annotated_privacy_policy"
        //了解更多注解
        const val ANNOTATED_LEARN_MORE = "learn_more"
    }
}