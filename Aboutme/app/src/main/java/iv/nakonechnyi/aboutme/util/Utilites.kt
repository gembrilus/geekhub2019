package iv.nakonechnyi.aboutme.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import iv.nakonechnyi.aboutme.data.Me
import iv.nakonechnyi.aboutme.fileName
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


fun load(context: Context): Me {
    var input: ObjectInputStream? = null
    return try {
        input = ObjectInputStream(FileInputStream(File(context.filesDir, fileName)))
        input.readObject() as Me
    } catch (e: IOException) {
        Me()
    } finally {
        input?.close()
    }
}

fun save(context: Context, obj: Me) {
    var output: ObjectOutputStream? = null
    try {
        output = ObjectOutputStream(FileOutputStream(File(context.filesDir, fileName)))
        output.writeObject(obj)
    } catch (e: IOException) {
        showErrorPopup(
            context,
            context.getString(iv.nakonechnyi.aboutme.R.string.can_not_save_data)
        )
    } finally {
        output?.close()
    }
}

fun changeGroupVisible(viewGroup: ViewGroup){
    if (viewGroup.visibility == View.VISIBLE) viewGroup.visibility = View.GONE
    else viewGroup.visibility = View.VISIBLE
}

fun showErrorPopup(context: Context, s: String) =
    Toast.makeText(context, s, Toast.LENGTH_SHORT).show()

fun showMessageWithActions(viewGroup: ViewGroup, text: String, actionOk: (() -> Unit)? = null, actionCancel: (() -> Unit)? = null){
    Snackbar.make(viewGroup, text, Snackbar.LENGTH_INDEFINITE).apply {
        actionOk?.let { f ->
            setAction(android.R.string.ok) {
                f.invoke()
            }
        }
        actionCancel?.let { f ->
            setAction(android.R.string.cancel) {
                f.invoke()
            }
        }
        show()
    }
}


fun getAge(d: Long): Int {
    val date = Date(d)
    val cal = Calendar.getInstance()
    cal.time = date

    var a = Calendar.getInstance().get(Calendar.YEAR) - cal.get(Calendar.YEAR)
    if (Calendar.getInstance().get(Calendar.MONTH) < cal.get(Calendar.MONTH) ||
        (Calendar.getInstance().get(Calendar.MONTH) < cal.get(Calendar.MONTH) &&
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < cal.get(Calendar.DAY_OF_MONTH))
    ) {
        --a
    }
    return a
}

fun dateToLong(d: String): Long =
    SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).parse(d)?.time ?: 0L

fun dateToString(d: Long): String =
    SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(d))


fun getDisplaySize(context: Context) = Point().also {
    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
        .defaultDisplay.getSize(it)
}

fun resizeImage(image: String, devWidth: Int, devHeight: Int): Bitmap {
    var inSampleSize = 1;
    var options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeFile(image, options)
    val srcWidth = options.outWidth
    val srcHeight = options.outHeight;

    if (srcHeight > devHeight || srcWidth > devWidth) {
        val heightScale = 1f * srcHeight / devHeight;
        val widthScale = 1f * srcWidth / devWidth;
        inSampleSize = Math.round(if (heightScale > widthScale) heightScale else widthScale)
    }
    options = BitmapFactory.Options()
    options.inSampleSize = inSampleSize;
    return BitmapFactory.decodeFile(image, options)
}

fun resourceToUri(context: Context, resID: Int) = Uri.parse(
    ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
            context.resources.getResourcePackageName(resID) + '/'.toString() +
            context.resources.getResourceTypeName(resID) + '/'.toString() +
            context.resources.getResourceEntryName(resID)
)