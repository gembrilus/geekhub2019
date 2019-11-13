package iv.nakonechnyi.newsfeeder

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import iv.nakonechnyi.newsfeeder.util.convertToDegrees
import iv.nakonechnyi.newsfeeder.util.longDateToString
import iv.nakonechnyi.newsfeeder.util.makeForActionNotification

class NewsService : Service() {

    companion object {

        private const val NAME_TAG = "iv.nakonechnyi.newsfeeder.NewsService"

    }

    /********************************Variables***************************************/
    /********************************************************************************/
    private val binder: IBinder get() = NewsBinder()
    private lateinit var locationManager: LocationManager
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    private val locationListener
        get() = object : LocationListener {
            override fun onLocationChanged(p0: Location?) = p0?.let { notifyForCoordinates(it) } ?: Unit
            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) = Unit
            override fun onProviderEnabled(p0: String?) = Unit
            override fun onProviderDisabled(p0: String?) = Unit
        }

    /*******************************Callbacks*****************************************/
    /*********************************************************************************/


    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.apply {
            requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10 * 60, 10f, locationListener)
            requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000 * 10 * 60,
                10f,
                locationListener
            )
        }
    }

    override fun onBind(intent: Intent): IBinder = binder

    /*****************************Custom methods**************************************/
    /*********************************************************************************/


    private fun notifyForCoordinates(location: Location) {
        val time = longDateToString(location.time)
        mLatitude = location.latitude
        mLongitude = location.longitude
        val res = String.format(
            "Координаты:%n%s, %s.%n%s",
            convertToDegrees(mLatitude),
            convertToDegrees(mLongitude),
            time
        )
        showNotification(res)
    }

    private fun showNotification(content: String) {

        val pendingIntent = PendingIntent.getActivity(this, 200, Intent(this, GeoActivity::class.java).apply {
            putExtra("LATITUDE", mLatitude)
            putExtra("LONGITUDE", mLongitude)
        }, PendingIntent.FLAG_UPDATE_CURRENT)

        makeForActionNotification(baseContext,
            getString(R.string.notification_service_title),
            content,
            pendingIntent)
    }


    /****************************Nested classes***************************************/
    /*********************************************************************************/
    class NewsBinder : Binder() {

        companion object {

            private const val NAME_TAG = "iv.nakonechnyi.newsfeeder.NewsService.NewsBinder"

        }

        fun getService(): NewsService = NewsService()

    }

}
