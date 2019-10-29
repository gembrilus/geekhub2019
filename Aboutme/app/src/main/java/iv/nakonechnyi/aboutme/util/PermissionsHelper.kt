package iv.nakonechnyi.aboutme.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionsHelper {

    companion object {

        private var isFirstAskPermission = true

        fun isGranted(context: Context, permission: String) =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED

        fun checkPermissionsAndRun(
            context: Activity,
            permissions: Array<String>,
            listener: PermissionAskListener
        ) {
            when {
                permissions.all { isGranted(context, it) } -> listener.onPermissionGranted()
                permissions.all {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        context,
                        it
                    )
                } -> listener.onPermissionPreviouslyDenied()
                isFirstAskPermission -> {
                    isFirstAskPermission = false
                    listener.onPermissionAsk()
                }
                else -> listener.onPermissionDisabled()
            }
        }
    }
}

interface PermissionAskListener {
    fun onPermissionAsk()
    fun onPermissionPreviouslyDenied()
    fun onPermissionDisabled()
    fun onPermissionGranted()
}