package com.xiaobai.core

import com.xiaobai.core.DestinationRoute.PassedKey.USER_ID
import com.xiaobai.core.DestinationRoute.PassedKey.VIDEO_INDEX
/**
 * 页面路由
 */
object DestinationRoute {
    // 主页
    const val HOME_SCREEN_ROUTE = "home_screen_route"
    // 评论
    const val COMMENT_BOTTOM_SHEET_ROUTE = "comment_bottom_sheet_route"
    // 创作者个人资料页面
    const val CREATOR_PROFILE_ROUTE = "creator_profile_route"
    // 创作者视频页面
    const val CREATOR_VIDEO_ROUTE = "creator_video_route"
    //带参数的完整视频页面
    const val FORMATTED_COMPLETE_CREATOR_VIDEO_ROUTE =
        "$CREATOR_VIDEO_ROUTE/{$USER_ID}/{$VIDEO_INDEX}"
   // 收件箱
    const val INBOX_ROUTE = "inbox_route"
    // 我的个人资料
    const val MY_PROFILE_ROUTE = "my_profile_route"
    // 好友
    const val FRIENDS_ROUTE = "friends_route"
    // 相机
    const val CAMERA_ROUTE = "camera_route"
    // 认证
    const val AUTHENTICATION_ROUTE = "authentication_route"
    // 手机或邮箱登录注册
    const val LOGIN_OR_SIGNUP_WITH_PHONE_EMAIL_ROUTE = "login_signup_phone_email_route"
    // 设置
    const val SETTING_ROUTE="setting_route"

    object PassedKey {
        const val USER_ID = "user_id"
        const val VIDEO_INDEX = "video_index"
    }
}