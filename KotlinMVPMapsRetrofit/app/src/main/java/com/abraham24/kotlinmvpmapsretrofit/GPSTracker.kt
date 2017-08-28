package com.abraham24.kotlinmvpmapsretrofit

/**
 * Created by KOCHOR on 8/27/2017.
 */
import android.Manifest
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.util.Log

class GPSTracker
//private SessionManager sesi;


(c: Context) : Service(), LocationListener {
    private var context: Context? = null
    internal var isGPSEnabled = false
    internal var isNetworkEnabled = false
    internal var canGetLocation = false
    internal var location: Location? = null
    internal var latitude: Double = 0.toDouble()
    internal var longitude: Double = 0.toDouble()

    protected var locationManager: LocationManager? = null

    init {
        this.context = c
        //sesi = new SessionManager(c);

        getLocation()
    }

    private fun getLocation(): Location? {
        try {
            locationManager = context!!
                    .getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // getting gps status
            isGPSEnabled = locationManager!!
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)
            // getting network status
            isNetworkEnabled = locationManager!!
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            //if (!isGPSEnabled && !isNetworkEnabled) {
            if (!isGPSEnabled && !isNetworkEnabled) {
                showSettingGps()
            } else {
                canGetLocation = true
                // get lat/lng by network
                if (isNetworkEnabled) {

                    if (checkPermission(context)) {
                        locationManager!!.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER, MIN_TIME,
                                MIN_DISTANCE.toFloat(), this)
                        Log.d("network", "network enabled")
                        if (locationManager != null) {
                            location = locationManager!!
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            if (location != null) {
                                latitude = location!!.latitude
                                longitude = location!!.longitude
                            }
                        }
                    } else {
                        //    HeroHelper.pesan(context, "Permission for GPS not valid")
                    }
                }

                // get lat/lng by gps
                if (isGPSEnabled) {
                    if (location == null) {
                        if (checkPermission(context)) {
                            locationManager!!.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER, MIN_TIME,
                                    MIN_DISTANCE.toFloat(), this)
                            Log.d("GPS", "GPS enabled")
                            if (locationManager != null) {
                                location = locationManager!!
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                if (location != null) {
                                    latitude = location!!.latitude
                                    longitude = location!!.longitude
                                }

                            }
                        } else {
                            //HeroHelper.pesan(context, "Permission for GPS not valid")
                        }
                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return location

    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */
    fun stopUsingGPS () {
        if (locationManager != null) {
            if (checkPermission(context)) {
                locationManager!!.removeUpdates(this)
            }
        }
    }

    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }

        return latitude
    }

    val locations: Location?
        get() = if (location != null) {
            location
        } else null

    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }

        return longitude
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    fun canGetLocation (): Boolean {
        return canGetLocation
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    fun showSettingGps() {
        val alertBuilder = AlertDialog.Builder(context)

        alertBuilder.setTitle("GPS Setting")
        alertBuilder.setMessage("GPS is not enabled. Do you want to go to settings menu?")

        alertBuilder.setPositiveButton("Setting") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context!!.startActivity(intent)
        }
        alertBuilder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        alertBuilder.show()
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            //NurHelper.pesan(context, "perubahan Alamat");


            if (this.location !== location) {
                sendPosisi(location.latitude, location.longitude)
                this.location = location
            }


        }


    }

    override fun onProviderDisabled(provider: String) {
        // TODO Auto-generated method stub

    }

    override fun onProviderEnabled(provider: String) {
        // TODO Auto-generated method stub

    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        // TODO Auto-generated method stub

    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO Auto-generated method stub
        return null
    }


    fun sendPosisi(lat: Double, lng: Double) {
        val i = Intent(NEW_POSITION)
        i.putExtra("lat", lat)
        i.putExtra("lng", lng)
        context!!.sendBroadcast(i)

    }

    companion object {

        private val MIN_DISTANCE = 1.toLong() // 10 meter
        private val MIN_TIME = (1000 * 1 * 1).toLong() // 1minute
        val NEW_POSITION = "newPosition"


        fun checkPermission(context: Context?): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


                ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        }
    }


}