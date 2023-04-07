package com.nrohpos.kotlineapp.ui

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OpenGallery(
    onImageUri: (Uri?) -> Unit = { }
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            onImageUri(uri)
        }
    )

    @Composable
    fun LaunchGallery() {
        SideEffect {
            launcher.launch("image/*")
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val permissionState = rememberPermissionState(
            permission = Manifest.permission.ACCESS_MEDIA_LOCATION
        )

        KotlinPermission(
            permissionStatus = permissionState.status,
            permissionNotAvailableContent = {
                LaunchGallery()
            },
        ) {
            LaunchGallery()
        }
    } else {
        LaunchGallery()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun KotlinPermission(
    permissionStatus: PermissionStatus,
    permissionNotAvailableContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    when (permissionStatus) {
        is PermissionStatus.Denied -> {
            permissionNotAvailableContent.invoke()
        }
        PermissionStatus.Granted -> content.invoke()
    }
}