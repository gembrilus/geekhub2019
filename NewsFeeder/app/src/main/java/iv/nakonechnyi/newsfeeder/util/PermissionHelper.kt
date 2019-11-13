package iv.nakonechnyi.newsfeeder.util

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import iv.nakonechnyi.newsfeeder.R

class PermissionHelper {

    private lateinit var context: Activity


    companion object {

        private var isFirstAskPermission = true

        fun getPermissionHelper(context: Activity) = PermissionHelper().apply {
            this.context = context
        }
    }

    fun isPermissionGranted(
        grantPermissions: Array<out String>, grantResults: IntArray,
        permission: String
    ): Boolean {
        for (i in grantPermissions.indices) {
            if (permission == grantPermissions[i]) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED
            }
        }
        return false
    }

    fun requestPermission(
        activity: Activity,
        requestId: Int,
        permission: String
    ) {
        when {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> showExplainDialog() {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(permission),
                    requestId
                )
            }
            isFirstAskPermission -> {
                isFirstAskPermission = false
                ActivityCompat.requestPermissions(activity, arrayOf(permission), requestId)
            }
            else -> showDeniedDialog()
        }
    }

    private fun showExplainDialog(block: () -> Unit) {
        isFirstAskPermission = false
        AlertDialog.Builder(context).apply {
            setIcon(android.R.drawable.ic_dialog_alert)
            setTitle(context.getString(R.string.dialog_explain_title))
            setMessage(
                context.getString(R.string.dialog_explain_message)
            )
            setPositiveButton(android.R.string.ok) { _, _ -> block() }
        }.create().show()
    }

    private fun showDeniedDialog() {
        AlertDialog.Builder(context).apply {
            setIcon(android.R.drawable.ic_dialog_alert)
            setTitle(context.getString(R.string.dialog_denied_title))
            setMessage(
                context.getString(R.string.dialog_denied_message)
            )
            setPositiveButton(android.R.string.ok, null)
        }.create().show()
    }


}