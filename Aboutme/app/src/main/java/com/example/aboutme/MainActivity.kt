package com.example.aboutme

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.aboutme.data.Me
import com.example.aboutme.util.birthday
import com.example.aboutme.util.load
import com.example.aboutme.util.save
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var me: Me
    private lateinit var perms: MutableMap<String, Boolean>
    val SETTINGS_REQUEST_CODE = 1
    val FROM_STORAGE_CODE = 2
    val FROM_CAMERA_CODE = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize()

        iv_photo.apply {
            if (me.photos.isEmpty()) {
                setImageDrawable(resources.getDrawable(R.drawable.anonim_photo))
            } else {
                setImageURI(Uri.parse(me.photos))
            }
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        b_info_text.setOnClickListener {
            val intent = Intent(this, AdditionalInfo::class.java)
            intent.putExtra("ME", me)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        when (requestCode) {
            SETTINGS_REQUEST_CODE -> {
                me = data.getSerializableExtra("ME1") as Me
                getInvitation(me)
            }
            FROM_STORAGE_CODE -> {
                val uri = data.data
                iv_photo.setImageURI(uri)
                me.photos = uri.toString()
                save(this, File(filesDir, "me_store"), me)
            }
            FROM_CAMERA_CODE -> {
                val imageBitmap = data.extras?.get("data") as Bitmap
                iv_photo.setImageBitmap(imageBitmap)
            }
            else -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.no_activity_result),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
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

    private fun initialize() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        perms = mutableMapOf(
            android.Manifest.permission.CAMERA to checkPerms(android.Manifest.permission.CAMERA),
            android.Manifest.permission.READ_EXTERNAL_STORAGE to checkPerms(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE to checkPerms(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        )
        registerForContextMenu(iv_photo)
        val file = File(filesDir, "me_store")
        me = if (file.exists()) load(this, file) else Me()
        getInvitation(me)
    }

    fun loadFromStorage(item: MenuItem) {
        if (perms[android.Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(intent, FROM_STORAGE_CODE)
        } else ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            FROM_STORAGE_CODE
        )
    }

    fun getFromCamera(item: MenuItem) {
        if (perms.values.all { it }) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.resolveActivity(packageManager).also {
                startActivityForResult(takePictureIntent, FROM_CAMERA_CODE)
            }
        } else ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            ),
            FROM_STORAGE_CODE
        )
    }

    fun setInfo(item: MenuItem) {
        val intent = Intent(this, Settings::class.java)
        intent.putExtra("ME", me)
        startActivityForResult(intent, SETTINGS_REQUEST_CODE)
    }

    private fun checkPerms(permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    fun getInvitation(obj: Me) {
        val bDay = birthday(obj.birthday)
        val s = when(bDay % 10){
            1 -> getString(R.string.year)
            in 2..4 -> getString(R.string.years)
            else -> getString(R.string.years2)
        }
        tw_invitation.text = "${obj.name} ${obj.surname}, ${bDay} $s"
        val date = obj.birthday
        val localDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
        birthday.text = "${getString(R.string.was_born)} ${localDate}"
    }
}