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
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iv.nakonechnyi.aboutme.data.Address
import iv.nakonechnyi.aboutme.data.Me
import iv.nakonechnyi.aboutme.util.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.part_address_info.*
import kotlinx.android.synthetic.main.part_interest.*
import kotlinx.android.synthetic.main.part_job_and_study.*
import kotlinx.android.synthetic.main.part_main_info.*
import kotlinx.android.synthetic.main.part_photo_picker.*
import kotlinx.android.synthetic.main.part_social_settings.*
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
    private val FROM_STORAGE_CODE = 1
    private val FROM_CAMERA_CODE = 2
    private var hasCamera = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = getString(R.string.title_settings)

        fillFields()

        hasCamera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

        viewManager = GridLayoutManager(this, 2, RecyclerView.HORIZONTAL, false)
        viewAdapter = MyRecycleAdapter(me.photos).apply {
            clickListener = object : ClickListener{
                override fun onItemClick(position: Int, v: View) {
                    AlertDialog.Builder(this@SettingsActivity).apply {
                        title = getString(R.string.choose_source)
                        setItems(R.array.dialog_items
                        ) { _, pos ->
                            when(pos){
                                0 -> getFromStorage()
                                1 -> getFromCamera()
                            }
                        }
                        create()
                        show()
                    }
                }
            }
        }
        recyclerView = findViewById<RecyclerView>(R.id.recycler_photo).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        hided_group0.setOnClickListener {
            if (ll_group0.isVisible) ll_group0.visibility = View.GONE
            else ll_group0.visibility = View.VISIBLE
        }

        hided_group1.setOnClickListener {
            if (ll_group1.isVisible) ll_group1.visibility = View.GONE
            else ll_group1.visibility = View.VISIBLE
        }

        hided_group2.setOnClickListener {
            if (ll_group2.isVisible) ll_group2.visibility = View.GONE
            else ll_group2.visibility = View.VISIBLE
        }

        hided_group3.setOnClickListener {
            if (ll_group3.isVisible) ll_group3.visibility = View.GONE
            else ll_group3.visibility = View.VISIBLE
        }

        hided_group4.setOnClickListener {
            if (ll_group4.isVisible) ll_group4.visibility = View.GONE
            else ll_group4.visibility = View.VISIBLE
        }

        hided_group5.setOnClickListener {
            if (ll_group5.isVisible) ll_group5.visibility = View.GONE
            else ll_group5.visibility = View.VISIBLE
        }

        ib_date.setOnClickListener {
            DatePickerFragment()
                .show(supportFragmentManager, getString(R.string.choose_a_birthday))
        }

        b_save.setOnClickListener {
            me = fillMe()
            save(this, store_file, me)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_STORAGE -> {
                    val uri = data?.data
                    me.photos.add(uri.toString())
                }
                REQUEST_CODE_CAMERA -> {
                    resizeImage(
                        photoPath,
                        getDisplaySize(this).y,
                        getDisplaySize(this).x)
                    me.photos.add(photoPath)
                    recyclerView.adapter?.notifyDataSetChanged()
                    revokeUriPermission(
                        photoURI,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
            }
        }
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
        with(me){
            if(sex == 0) is_a_man.isChecked = true
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
        if (perms[android.Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ).apply { type = "image/*" }
            startActivityForResult(intent, REQUEST_CODE_STORAGE)
        } else ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            FROM_STORAGE_CODE
        )
    }

    private fun getFromCamera() {
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
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION )

                startActivityForResult(picIntent, REQUEST_CODE_CAMERA)
            }
        }
    }
}
