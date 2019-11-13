package iv.nakonechnyi.newsfeeder

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class GeoActivity : AppCompatActivity(), OnMapReadyCallback {

    /********************************Variables***************************************/
    /********************************************************************************/

    private lateinit var mMap: GoogleMap

    /*******************************Callbacks*****************************************/
    /*********************************************************************************/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(R.layout.activity_geo)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if(intent != null){
            val lat = intent.getDoubleExtra("LATITUDE", 0.0)
            val lon = intent.getDoubleExtra("LONGITUDE", 0.0)
            val location = LatLng(lat, lon)
            val text = String.format("%.4f, %.4f", lat, lon)
            with(mMap){
                moveCamera(CameraUpdateFactory.newLatLng(location))
                animateCamera(CameraUpdateFactory.newLatLngZoom(location, maxZoomLevel/2));
                addMarker(MarkerOptions().position(location).title(text))
            }
        }
    }


    /****************************Private methods**************************************/
    /*********************************************************************************/



    /****************************Nested classes***************************************/
    /*********************************************************************************/

}