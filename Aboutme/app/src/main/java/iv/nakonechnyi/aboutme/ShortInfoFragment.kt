package iv.nakonechnyi.aboutme

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.crashlytics.android.Crashlytics
import iv.nakonechnyi.aboutme.data.Me
import iv.nakonechnyi.aboutme.util.*
import kotlinx.android.synthetic.main.fragment_info_short.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ShortInfoFragment : Fragment() {

    private lateinit var vShortInfo: View
    private lateinit var photoPath: String
    private lateinit var photoURI: Uri
    private val REQUEST_CODE_CAMERA = 20
    private val REQUEST_CODE_STORAGE = 30
    private val FROM_STORAGE_CODE = 1
    private val FROM_CAMERA_CODE = 2
    private var hasCamera = false
    private var mDualPane = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        me = load(store_file)
        setHasOptionsMenu(true);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vShortInfo = inflater.inflate(R.layout.fragment_info_short, container, false).apply {
                b_info_text.setOnClickListener {
                    startActivity(Intent(activity, AdditionalInfoActivity::class.java))
                }
            }
        setMainInfo(me)
        registerForContextMenu(vShortInfo)
        hasCamera = (activity as FragmentActivity).packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        return vShortInfo
    }

    override fun onResume() {
        super.onResume()
        setMainInfo(me)
    }

    override fun onPause() {
        super.onPause()
        save( activity as FragmentActivity, store_file, me )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mDualPane = activity?.findViewById<FrameLayout>(R.id.activity_main2).run {
            this != null && visibility == View.VISIBLE
        }

        if (mDualPane) {
            vShortInfo.b_info_text.visibility = View.GONE
            showAddInfo()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_STORAGE -> {
                    val uri = data?.data ?: return
                    vShortInfo.iv_photo.setImageURI(uri)
                    me.photos = uri.toString()
                }
                REQUEST_CODE_CAMERA -> {
                    resizeImage(
                        photoPath,
                        getDisplaySize(activity as FragmentActivity).y,
                        getDisplaySize(activity as FragmentActivity).x)
                    vShortInfo.iv_photo.setImageURI(Uri.parse(photoPath))
                    me.photos = photoPath
                    activity?.revokeUriPermission(
                        photoURI,
                        (Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    )
                }
            }
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity?.menuInflater?.inflate(R.menu.context_menu, menu)
        if(hasCamera) menu.findItem(R.id.from_camera).isEnabled = true
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.from_storage -> getFromStorage()
            R.id.from_camera -> if(hasCamera) getFromCamera()
        }
        return super.onContextItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> startActivity(Intent(activity, SettingsActivity::class.java))
            R.id.crash_menu_item -> {
                Crashlytics.getInstance().crash()
            }
        }
        return true
    }

    private fun getFromStorage() {
        if (perms[android.Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ).apply { type = "image/*" }
            startActivityForResult(intent, REQUEST_CODE_STORAGE)
        } else ActivityCompat.requestPermissions(
            activity as FragmentActivity,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            FROM_STORAGE_CODE
        )
    }

    private fun getFromCamera() {
        if (perms.values.all { it }) {
            getPictureIntent()
        } else ActivityCompat.requestPermissions(
            activity as FragmentActivity,
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            FROM_CAMERA_CODE
        )
    }

    private fun createImageFile(): File {
        val imageFileName = "JPEG_" + Date().time
        val storageDir =
            activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        photoPath = image.absolutePath
        return image
    }

    private fun getPictureIntent() {
        val picIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        picIntent.resolveActivity((activity as FragmentActivity).packageManager).also {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                showErrorPopup(
                    activity as FragmentActivity,
                    getString(R.string.cant_create_file_image)
                )
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(
                    activity as FragmentActivity,
                    getString(R.string.fileprovider_authority),
                    photoFile
                )
                picIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                activity?.grantUriPermission(
                    it.packageName,
                    photoURI,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION ) ?: showErrorPopup(activity!!, getString(
                                        R.string.no_suitable_app))

                startActivityForResult(picIntent, REQUEST_CODE_CAMERA)
            }
        }
    }

    private fun setMainInfo(obj: Me) {
        val age = getAge(obj.birthday)
        val s = when (age % 10) {
            1 -> getString(R.string.year)
            in 2..4 -> getString(R.string.years)
            else -> getString(R.string.years2)
        }
        updateImage()
        vShortInfo.tw_invitation.text =
            getString(R.string.welcome_message, obj.name, obj.surname, age, s)
        val date = obj.birthday
        val localDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
        vShortInfo.birthday.text = getString(R.string.was_born, localDate)
    }

    private fun updateImage(){
        with(vShortInfo.iv_photo) {
            if (me.photos.isEmpty()) {
                setImageDrawable(
                    resources.getDrawable(
                        R.drawable.anonim_photo,
                        resources.newTheme()
                    )
                )
            } else setImageURI(Uri.parse(me.photos))
        }
    }

    private fun showAddInfo(){
        fragmentManager?.findFragmentById(R.id.activity_main2).also {
            it ?: fragmentManager!!.beginTransaction()
                .add(R.id.activity_main2, AdditionalInfoFragment())
                .commit()
        }

    }
}