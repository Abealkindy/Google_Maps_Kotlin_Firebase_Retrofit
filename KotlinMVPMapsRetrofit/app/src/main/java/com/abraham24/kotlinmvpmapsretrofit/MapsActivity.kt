package com.abraham24.kotlinmvpmapsretrofit

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.abraham24.kotlinmvpmapsretrofit.Init.InitRetrofit
import com.abraham24.kotlinmvpmapsretrofit.Init.ResponseJSON
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocomplete

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    var markerAwal: LatLng? = null
    var markerAkhir: LatLng? = null

    var gps: GPSTracker? = null

    var lat: Double? = null
    var long: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        var permission = (android.Manifest.permission.ACCESS_COARSE_LOCATION)
        ActivityCompat.requestPermissions(this@MapsActivity, arrayOf(permission), 2)

        gps = GPSTracker(this@MapsActivity)

        if (gps!!.canGetLocation) {
            lat = gps!!.getLatitude()
            long = gps!!.getLongitude()

//            var name = convertCoordinateToName(lat ,long)
//            edit_from.setText(name)
        } else {
            gps!!.showSettingGps()
        }

        val database = FirebaseDatabase.getInstance().getReference("lokasi")

        button_check_in.setOnClickListener {
            var lokasi = Lokasi(edit_from?.text.toString(), edit_to?.text.toString(), text_jarak?.text.toString()
                    , text_harga?.text.toString(), text_time?.text.toString(), lat.toString(), long.toString())
            var key = database.push().key

            database.child(key).setValue(lokasi)
        }
    }

//
    private fun convertCoordinateToName(lat: Double, long: Double): String {


        var nameLocation: String? = null
        //deklarasi geocoder
        var geoCoder = Geocoder(this@MapsActivity, Locale.getDefault())
        var insertCoor = geoCoder.getFromLocation(lat, long, 1)

        if (insertCoor.size > 0) {
            nameLocation = insertCoor.get(0).getAddressLine(0)

        }

        return nameLocation!!
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
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-6.1925297, 106.8001397)
        mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17.toFloat()))

        mMap!!.uiSettings.isZoomControlsEnabled = true
        mMap!!.uiSettings.isCompassEnabled = true
        mMap!!.uiSettings.setAllGesturesEnabled(true)
        mMap!!.uiSettings.isMyLocationButtonEnabled = true


        mMap!!.isBuildingsEnabled = true

        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL

        button_traffic.setOnClickListener {
            mMap!!.isTrafficEnabled = true
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ActivityCompat.checkSelfPermission(this@MapsActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this@MapsActivity,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

        mMap = googleMap

        edit_from.setOnClickListener {
            completeAuto(1)
        }

        edit_to.setOnClickListener {
            completeAuto(2)
        }


    }

    private fun completeAuto(i: Int) {
        val typeFilter = AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_BOOK_STORE)
                .setCountry("ID")
                .build()
        var intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                .setFilter(typeFilter)
                .build(this@MapsActivity)
        startActivityForResult(intent, i)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == 1 && resultCode != null) {

                //get data return
                var place = PlaceAutocomplete.getPlace(this, data)
                var lat = place.latLng.latitude
                var long = place.latLng.longitude

                //include latlong
                markerAwal = LatLng(lat, long)

                mMap!!.clear()



                edit_from.setText(place.address.toString())


                //add marker
                mMap!!.addMarker(MarkerOptions().position(markerAwal!!)
                        .title(place.address.toString())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                if (edit_to.text.toString().length > 0) {
                    mMap!!.addMarker(MarkerOptions().position(markerAkhir!!)
                            .title(place.address.toString())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))

                }
                //set camera zoom
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(markerAwal, 20.toFloat()))
            } else if (requestCode == 2 && resultCode != null) {

                //get data return
                var place = PlaceAutocomplete.getPlace(this, data)
                var lat = place.latLng.latitude
                var long = place.latLng.longitude

                //include latlong
                markerAkhir = LatLng(lat, long)





                edit_to.setText(place.address.toString())
                //add marker
                actionRute()
                mMap!!.addMarker(MarkerOptions().position(markerAkhir!!)
                        .title(place.address.toString())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                if (edit_from.text.toString().length > 0) {
                    mMap!!.addMarker(MarkerOptions().position(markerAwal!!)
                            .title(place.address.toString())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))

                }
                //set camera zoom
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(markerAkhir, 20.toFloat()))


            } else if (resultCode == 0) {
                Toast.makeText(applicationContext, "Belum Pilih Lokasi Bro", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {

        }
    }

    private fun actionRute() {

        var api = InitRetrofit().getInitInstance()

        var call = api.request_route(edit_from.text.toString(), edit_to.text.toString(), "driving")

        call.enqueue(object : Callback<ResponseJSON> {
            override fun onFailure(call: Call<ResponseJSON>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ResponseJSON>?, response: Response<ResponseJSON>?) {
                if (response != null) {
                    if (response.isSuccessful) {
                        // get json array route
                        var route = response.body()?.routes
                        // get object overview polyline
                        var overview = route?.get(0)?.overviewPolyline
                        // get string json point
                        var point = overview?.points

                        var direction = DirectionMapsV2(this@MapsActivity)
                        direction.gambarRoute(mMap!!, point!!)

//                        var legs = route?.get(0)?.legs
//
//                        var distance = legs?.get(0)?.distance
//                        text_jarak.setText(distance?.text.toString())
//
//                        var duration = legs?.get(0)?.duration
//                        text_time.setText(duration?.text.toString())
//
//                        var dist = Math.ceil(distance?.value?.toDouble()!! /1000)
//                        text_harga.setText("Rp. " + (dist * 5000).toString())

                        var jarak = route?.get(0)?.legs?.get(0)?.distance?.text
                        var jarak_meter = route?.get(0)?.legs?.get(0)?.distance?.value
                        var waktu = route?.get(0)?.legs?.get(0)?.duration?.text
                        var harga = (jarak_meter!! / 1000) * 5000 // angka setelah simbol bintang/kali itu tarif permeternya

                        text_jarak.setText("Jarak : " + jarak)
                        text_harga.setText("Harga : " + "Rp." + harga)
                        text_time.setText("Waktu : " + waktu)
                    }
                }
            }

        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 2) {
            mMap!!.isMyLocationEnabled = true
        }
    }


}
