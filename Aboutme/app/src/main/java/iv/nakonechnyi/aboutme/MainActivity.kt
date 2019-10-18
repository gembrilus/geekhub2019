package iv.nakonechnyi.aboutme

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import iv.nakonechnyi.aboutme.data.Me
import java.io.File


internal lateinit var perms: MutableMap<String, Boolean>
internal lateinit var store_file: File
internal lateinit var me: Me
internal val fileName = "me_store"

abstract class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())

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

    fun checkPerms(permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    protected abstract fun createFragment(): Fragment
    @LayoutRes protected open fun getLayout() = R.layout.activity_main
}