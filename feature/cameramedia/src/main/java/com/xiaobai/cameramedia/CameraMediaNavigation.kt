package com.xiaobai.cameramedia

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.xiaobai.core.DestinationRoute

fun NavGraphBuilder.cameraMediaNavGraph(navController: NavController) {
    composable(route = DestinationRoute.CAMERA_ROUTE) {
        CameraMediaScreen(navController)
    }
}