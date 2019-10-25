package iv.nakonechnyi.aboutme

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import iv.nakonechnyi.aboutme.data.Address
import iv.nakonechnyi.aboutme.data.Me
import iv.nakonechnyi.aboutme.fragments.DatePickerFragment
import iv.nakonechnyi.aboutme.util.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.part_address_info.*
import kotlinx.android.synthetic.main.part_interest.*
import kotlinx.android.synthetic.main.part_job_and_study.*
import kotlinx.android.synthetic.main.part_main_info.*
import kotlinx.android.synthetic.main.part_photo_picker.*
import kotlinx.android.synthetic.main.part_social_settings.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.*


class SettingsActivity : AppCompatActivity() {

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var recyclerView: RecyclerView
    private lateinit var photoPath: String
    private lateinit var photoURI: Uri
    private val REQUEST_CODE_CAMERA = 20
    private val REQUEST_CODE_STORAGE = 30
    private val PERM_CODE_STORAGE = 1
    private val PERM_CODE_CAMERA = 2
    private var hasCamera = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = getString(R.string.title_settings)

        fillFields()

        hasCamera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

        val dialog = AlertDialog.Builder(this@SettingsActivity).apply {
            title = getString(R.string.choose_source)
            setItems(R.array.dialog_items) { _, pos ->
                when (pos) {
                    0 -> PermissionsHelper.requestPermissionsAndRun(
                        context = this@SettingsActivity,
                        permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        requestCode = PERM_CODE_STORAGE,
                        snackbar = Snackbar.make(
                            root_settings,
                            getString(R.string.explanation_for_perms_read_storage),
                            Snackbar.LENGTH_LONG),
                        block = { getFromStorage() }
                    )
                    1 -> if(hasCamera) {
                        PermissionsHelper.requestPermissionsAndRun(
                            context = this@SettingsActivity,
                            permissions = arrayOf(
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            requestCode = PERM_CODE_STORAGE,
                            snackbar = Snackbar.make(
                                root_settings,
                                getString(R.string.explanation_for_perms_camera),
                                Snackbar.LENGTH_LONG),
                            block = { getPictureIntent() }
                        )
                    }
                }
            }
            create()
        }

        viewManager = GridLayoutManager(this, 2, RecyclerView.HORIZONTAL, false)

        viewAdapter = MyRecycleAdapter(me.photos).apply {
            clickListener = object : ClickListener {
                override fun onItemClick(position: Int, v: View) {
                    dialog.show()
                }
            }
        }
        recyclerView = findViewById<RecyclerView>(R.id.recycler_photo).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        hided_group0.setOnClickListener { changeGroupVisible(ll_group0) }
        hided_group1.setOnClickListener { changeGroupVisible(ll_group1) }
        hided_group2.setOnClickListener { changeGroupVisible(ll_group2) }
        hided_group3.setOnClickListener { changeGroupVisible(ll_group3) }
        hided_group4.setOnClickListener { changeGroupVisible(ll_group4) }
        hided_group5.setOnClickListener { changeGroupVisible(ll_group5) }

        ib_date.setOnClickListener {
            DatePickerFragment()
                .show(supportFragmentManager, getString(R.string.choose_a_birthday))
        }

        b_save.setOnClickListener {
            GlobalScope.launch {
                me = fillMe()
                save(this@SettingsActivity, me)
            }
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_STORAGE -> {
                    val uri = data?.data ?: return
                    me.photos.add(uri.toString())
                }
                REQUEST_CODE_CAMERA -> {
                    resizeImage(
                        photoPath,
                        getDisplaySize(this).y,
                        getDisplaySize(this).x
                    )
                    me.photos.add(photoPath)
                    revokeUriPermission(
                        photoURI,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
            }
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERM_CODE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults.all {
                        it == PackageManager.PERMISSION_GRANTED
                    }) {
                    getFromStorage()
                }
            }
            PERM_CODE_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults.all {
                        it == PackageManager.PERMISSION_GRANTED
                    }) {
                    getPictureIntent()
                }
            }
        }
    }

    private fun fillMe() =
        Me(
            name = et_name.text.toString(),
            surname = et_surname.text.toString(),
            photos = me.photos,
            sex = if (is_a_man.isChecked) 0 else 1,
            birthday = dateToLong(tw_birthday.text.toString()),
            address = Address(
                country = et_country.text.toString(),
                city = et_city.text.toString(),
                address = et_address.text.toString()
            ),
            works = et_work.text.toString(),
            education = et_study.text.toString(),
            hobbies = et_hobbies.text.toString(),
            lovingMovies = et_loving_movies.text.toString(),
            lovingMusic = et_loving_music.text.toString(),
            lovingBooks = et_loving_books.text.toString(),
            phoneNumber = et_phonenumber.text.toString(),
            email = et_email.text.toString(),
            social = mapOf(
                "facebook" to et_facebook.text.toString(),
                "google" to et_google.text.toString(),
                "instagram" to et_instagram.text.toString(),
                "whatsapp" to et_whatsup.text.toString(),
                "twitter" to et_twitter.text.toString(),
                "youtube" to et_youtube.text.toString()
            )
        )

    private fun fillFields() =
        with(me) {
            if (sex == 0) is_a_man.isChecked = true
            else is_a_woman.isChecked = true
            et_name.setText(name)
            et_surname.setText(surname)
            tw_birthday.text = dateToString(birthday)
            et_country.setText(address.country)
            et_city.setText(address.city)
            et_address.setText(address.address)
            et_work.setText(works)
            et_study.setText(education)
            et_hobbies.setText(hobbies)
            et_loving_movies.setText(lovingMovies)
            et_loving_music.setText(lovingMusic)
            et_loving_books.setText(lovingBooks)
            et_phonenumber.setText(phoneNumber)
            et_email.setText(email)
            et_facebook.setText(social.get("facebook"))
            et_google.setText(social.get("google"))
            et_instagram.setText(social.get("instagram"))
            et_whatsup.setText(social.get("whatsapp"))
            et_twitter.setText(social.get("twitter"))
            et_youtube.setText(social.get("youtube"))
        }

    private fun getFromStorage() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).apply { type = "image/*" }
        startActivityForResult(intent, REQUEST_CODE_STORAGE)
    }

    private fun createImageFile(): File {
        val imageFileName = "JPEG_" + Date().time
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
        picIntent.resolveActivity(packageManager).also {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                showErrorPopup(
                    this,
                    getString(R.string.cant_create_file_image)
                )
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(
                    this,
                    getString(R.string.fileprovider_authority),
                    photoFile
                )
                picIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                grantUriPermission(
                    it.packageName,
                    photoURI,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                startActivityForResult(picIntent, REQUEST_CODE_CAMERA)
            }
        }
    }
}
