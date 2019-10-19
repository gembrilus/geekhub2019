package iv.nakonechnyi.aboutme

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

open class MainFragment : Fragment(){
    protected lateinit var photoPath: String
    protected lateinit var photoURI: Uri
    protected val REQUEST_CODE_CAMERA = 20
    protected val REQUEST_CODE_STORAGE = 30
    protected val FROM_STORAGE_CODE = 1
    protected val FROM_CAMERA_CODE = 2
    protected var hasCamera = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        hasCamera = (activity as FragmentActivity).packageManager
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }
}