package iv.nakonechnyi.newsfeeder

import android.app.Service
import android.content.Intent
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder

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


    /*******************************Callbacks*****************************************/
    /*********************************************************************************/


    override fun onBind(intent: Intent): IBinder = binder

    /*****************************Custom methods**************************************/
    /*********************************************************************************/




    /****************************Nested classes***************************************/
    /*********************************************************************************/
    class NewsBinder : Binder() {

        companion object {

            private const val NAME_TAG = "iv.nakonechnyi.newsfeeder.NewsService.NewsBinder"

        }

        fun getService(): NewsService = NewsService()

    }

}
