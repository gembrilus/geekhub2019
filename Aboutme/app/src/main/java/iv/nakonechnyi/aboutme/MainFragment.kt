package iv.nakonechnyi.aboutme

import android.net.Uri
import androidx.fragment.app.Fragment

open class MainFragment : Fragment(){
    protected lateinit var photoPath: String
    protected lateinit var photoURI: Uri
    protected val REQUEST_CODE_SETTINGS = 10
    protected val REQUEST_CODE_CAMERA = 20
    protected val REQUEST_CODE_STORAGE = 30
    protected val FROM_STORAGE_CODE = 1
    protected val FROM_CAMERA_CODE = 2
}