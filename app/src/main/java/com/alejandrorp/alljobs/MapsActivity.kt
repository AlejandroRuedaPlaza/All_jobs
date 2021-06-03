package com.alejandrorp.alljobs

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.IOException

class MapsActivity : Metodos(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var mapFragment:SupportMapFragment
    private lateinit var searchView:SearchView
    private  var latitud:String=""
    private  var longitud:String=""
    private  var nameLoc:String=""
    var v: Metodos= Metodos()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        bt_Location.isEnabled=false


//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//                .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)

        searchView = findViewById(R.id.sv_location)
        mapFragment = supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment

        searchView.setOnQueryTextListener( object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                var location:String = searchView.query.toString()
                var addressList: List<Address>? = null

                if (location != null || location.equals("")){
                    var geocoder: Geocoder = Geocoder(this@MapsActivity)
                    try {
                        addressList = geocoder.getFromLocationName( location, 1)
                    }catch ( e: IOException){
                        e.printStackTrace()
                    }

                    try{
                        map.clear()
                    var address:Address = addressList!!.get(0)
                    var latLng:LatLng = LatLng(address.latitude,address.longitude)
//                        v.showAlert(address.featureName.toString(),this@MapsActivity)
                        nameLoc=address.featureName.toString()//nombre de la direccion
                        latitud=(""+latLng.latitude)//latitud
                        longitud=(""+latLng.longitude)//longitud
                        bt_Location.isEnabled=true
                    map.addMarker( MarkerOptions().position(latLng).title(location))
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
                    }catch (iooe:IndexOutOfBoundsException){
                        Log.d("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA","Not fount")
                        v.showAlert("La direccion no ha sido encontrada, compruebe que este bien escrita", this@MapsActivity)
                    }catch (upae:UninitializedPropertyAccessException){
                        Log.d("eeeeeeeeeeeeeeeeeeeeeeeeeeeeee","Not Init")
                    }

                }


                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })

        mapFragment.getMapAsync(this)

        setup()

    }

    private fun setup() {
        bt_Location.setOnClickListener {

            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.putString("nameLoc", nameLoc)
            prefs.putString("latitud", latitud)
            prefs.putString("longitud", longitud)
            prefs.apply()

            onBackPressed()
        }
        bt_Volver.setOnClickListener {
            onBackPressed()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
//
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

}