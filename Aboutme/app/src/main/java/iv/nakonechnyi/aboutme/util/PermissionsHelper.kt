package iv.nakonechnyi.aboutme.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class PermissionsHelper {

    companion object {

        private fun checkPerms(context: Context, permission: String) =
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED

        fun requestPermissionsAndRun(
            context: Activity,
            permissions: Array<String>,
            requestCode: Int,
            snackbar: Snackbar,
            block: () -> Unit
        ) {

            if (permissions.all { checkPerms(context, it) }) {
                block()
            } else {
                val deniedPermissions = permissions
                    .filterNot { checkPerms(context, it) }
                    .toTypedArray()

                if (deniedPermissions.none {
                        ActivityCompat.shouldShowRequestPermissionRationale(context, it)
                    }) {
                    snackbar.show()
                } else ActivityCompat.requestPermissions(context, permissions, requestCode)
            }
        }





    }
}