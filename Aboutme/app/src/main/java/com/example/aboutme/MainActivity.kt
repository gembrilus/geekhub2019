package com.example.aboutme

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.aboutme.data.Me
import com.example.aboutme.util.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var me: Me
    private lateinit var perms: MutableMap<String, Boolean>
    private lateinit var store_file: File
    private lateinit var photoPath: String
    private lateinit var photoURI: Uri
    private val REQUEST_CODE_SETTINGS = 10
    private val REQUEST_CODE_CAMERA = 20
    private val REQUEST_CODE_STORAGE = 30
    private val FROM_STORAGE_CODE = 1
    private val FROM_CAMERA_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize()

        with(iv_photo) {
            if (me.photos.isEmpty()) {
                setImageDrawable(
                    resources.getDrawable(
                        R.drawable.anonim_photo,
                        resources.newTheme()
                    )
                )
            } else {
                setImageURI(Uri.parse(me.photos))
            }
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
            maxHeight = getDisplaySize(this@MainActivity).x
        }

        b_info_text.setOnClickListener {
            startActivity(Intent(this, AdditionalInfo::class.java).apply {
                putExtra("ADD", me)
            })
        }
    }

    private fun initialize() {
        perms = mutableMapOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE to checkPerms(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE to checkPerms(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        )
        store_file = File(filesDir, "me_store")
        me = load(store_file)
        registerForContextMenu(iv_photo)
        setMainInfo(me)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        grantResults.forEachIndexed { index, i ->
            perms[permissions[index]] =
                i == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_SETTINGS -> {
                    me = data.getSerializableExtra("ME1") as Me
                    setMainInfo(me)
                }
                REQUEST_CODE_STORAGE -> {
                    val uri = data.data
                    iv_photo.setImageURI(uri)
                    me.photos = uri.toString()
                    save(this, store_file, me)
                }
                REQUEST_CODE_CAMERA -> {
                    iv_photo.setImageURI(photoURI)
                    me.photos = photoURI.toString()
                    save(this, store_file, me)
                }
            }
        } else showErrorPopup(this, getString(R.string.no_activity_result))
    }

    fun loadFromStorage(item: MenuItem) {
        if (perms[android.Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(intent, REQUEST_CODE_STORAGE)
        } else ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            FROM_STORAGE_CODE
        )
    }

    fun getFromCamera(item: MenuItem) {
        if (perms.values.all { it }) {
            getPictureIntent()
        } else ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            FROM_CAMERA_CODE
        )
    }

    fun setInfo(item: MenuItem) = startActivityForResult(
        Intent(this, Settings::class.java).apply {
            putExtra("ME", me)
        }, REQUEST_CODE_SETTINGS)

    private fun checkPerms(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private fun setMainInfo(obj: Me) {
        val age = getAge(obj.birthday)
        val s = when (age % 10) {
            1 -> getString(R.string.year)
            in 2..4 -> getString(R.string.years)
            else -> getString(R.string.years2)
        }
        tw_invitation.text = getString(R.string.welcome_message, obj.name, obj.surname, age, s)
        val date = obj.birthday
        val localDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
        birthday.text = getString(R.string.was_born, localDate)
    }

    private fun createImageFile(): File {
        val image = File.createTempFile(
            "JPEG_" + Date().time,
            ".jpg",
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
        photoPath = image.absolutePath
        return image
    }

    private fun getPictureIntent() = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        resolveActivity(packageManager).also {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                showErrorPopup(this@MainActivity, getString(R.string.cant_create_file_image))
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(
                    this@MainActivity,
                    "com.example.fileprovider",
                    photoFile
                )
                putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(this, REQUEST_CODE_CAMERA)
            }
        }
    }
}