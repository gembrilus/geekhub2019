package iv.nakonechnyi.aboutme

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import iv.nakonechnyi.aboutme.data.Me
import java.io.File

internal lateinit var perms: MutableMap<String, Boolean>
internal lateinit var store_file: File
internal lateinit var me: Me
internal val fileName: String get() = "me_store"

abstract class MainActivity : AppCompatActivity() {

    protected abstract fun createFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        store_file = File(filesDir, fileName)

        perms = mutableMapOf(
            Manifest.permission.READ_EXTERNAL_STORAGE to checkPerms(Manifest.permission.READ_EXTERNAL_STORAGE),
            Manifest.permission.WRITE_EXTERNAL_STORAGE to checkPerms(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )

        supportFragmentManager.findFragmentById(R.id.activity_main1).also {
            it ?: supportFragmentManager
                .beginTransaction()
                .add(R.id.activity_main1, createFragment())
                .commit()
        }
    }

    private fun checkPerms(permission: String) =
        ContextCompat.checkSelfPermission(this, permission ) == PackageManager.PERMISSION_GRANTED
}