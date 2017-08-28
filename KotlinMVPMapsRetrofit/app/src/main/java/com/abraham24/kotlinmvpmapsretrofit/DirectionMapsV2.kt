package com.abraham24.kotlinmvpmapsretrofit

import android.content.Context
import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

/**
 * Created by KOCHOR on 8/27/2017.
 */
class DirectionMapsV2(private val context: Context) {
    private var polyz: List<LatLng>? = null
    private val TAG_HTML_INSTRUCTION = "html_instructions"
    var totalTime: Int = 0
        private set

    // menggambar polyline
    fun gambarRoute(map: GoogleMap, dataPoly: String) {

        polyz = decodePoly(dataPoly)
        for (i in 0..polyz!!.size - 1 - 1) {
            val src = polyz!![i]
            val dest = polyz!![i + 1]
            val line = map.addPolyline(PolylineOptions()
                    .add(LatLng(src.latitude, src.longitude),
                            LatLng(dest.latitude, dest.longitude)).width(5f)
                    .color(Color.DKGRAY).geodesic(true))

        }
    }

    fun removePolyline(poli: Polyline, map: GoogleMap) {
        poli.remove()
    }

    fun gambarRoute(map: GoogleMap, dataPoly: String, color: Int) {

        polyz = decodePoly(dataPoly)
        for (i in 0..polyz!!.size - 1 - 1) {
            val src = polyz!![i]
            val dest = polyz!![i + 1]
            val poli = Polyline(null!!)
            poli.remove()
            val line = map.addPolyline(PolylineOptions()
                    .add(LatLng(src.latitude, src.longitude),
                            LatLng(dest.latitude, dest.longitude)).width(4f)
                    .color(color).geodesic(true))

        }
    }

    // untuk memperoleh data durasi yang berbentuk tex
    fun getDurationText(jsonObject: JSONObject): String? {
        try {
            val x = getLegs(jsonObject)
            val duration = x!!.getJSONObject(TAG_DURATION)
            return duration.getString(TAG_TEXT)
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return null

    }

    // untuk memperoleh data durasi yang berbentuk data angka
    fun getDurationValue(jsonObject: JSONObject): Int {
        try {
            val x = getLegs(jsonObject)
            val duration = x!!.getJSONObject(TAG_DURATION)
            return duration.getInt(TAG_VALUE)
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return 0

    }

    // untuk memperoleh data jarak yang berbentuk tex
    fun getDistanceText(jsonObject: JSONObject): String? {
        try {
            val x = getLegs(jsonObject)
            val duration = x!!.getJSONObject(TAG_DISTANCE)
            return duration.getString(TAG_TEXT)
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return null

    }

    // untuk memperoleh data jarak yang berbentuk data angka
    fun getDistanceValue(jsonObject: JSONObject): Int? {
        try {
            val x = getLegs(jsonObject)
            val duration = x!!.getJSONObject(TAG_DISTANCE)
            return duration.getInt(TAG_VALUE)
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return null

    }

    // untuk memdapatkan titik legs pada routes
    private fun getLegs(jsonObject: JSONObject): JSONObject? {
        val legs: JSONArray
        try {
            legs = jsonObject.getJSONArray(TAG_LEGS)

            return legs.getJSONObject(0)
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return null
    }

    // ambill start address
    fun getStarAddress(jsonObject: JSONObject): String? {
        try {
            val x = getLegs(jsonObject)
            return x!!.getString(TAG_START_ADDRESS)
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return null

    }

    // ambill end address
    fun getEndAddress(jsonObject: JSONObject): String? {
        try {
            val x = getLegs(jsonObject)
            return x!!.getString(TAG_END_ADDRESS)
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return null

    }

    fun getDirection(jsonObject: JSONObject): ArrayList<LatLng> {
        var startLocation: JSONObject
        val endLocation: JSONObject
        val intruksi: JSONObject
        val distance: JSONObject
        val duration: JSONObject
        var polyline: JSONObject
        var lat: Double
        var lng: Double
        val listLatLng = ArrayList<LatLng>()
        val x = getLegs(jsonObject)
        val step: JSONArray
        try {
            step = x!!.getJSONArray(TAG_STEPS)

            if (step.length() > 0) {
                for (i in 0..step.length() - 1) {
                    val stepx = step.getJSONObject(i)

                    // ambil nilai untuk start point
                    startLocation = stepx.getJSONObject(TAG_START_LOCATION)
                    lat = java.lang.Double.parseDouble(startLocation.getString(TAG_LAT))
                    lng = java.lang.Double.parseDouble(startLocation.getString(TAG_LNG))
                    listLatLng.add(LatLng(lat, lng))

                    // ambil nilai polyline
                    polyline = stepx.getJSONObject(TAG_POLYLINE)
                    val point = polyline.getString(TAG_POINTS)
                    val arr = decodePoly(point)
                    for (j in arr.indices) {
                        listLatLng.add(LatLng(arr[j].latitude, arr[j].longitude))
                    }

                    // ambil nilai untuk end point
                    startLocation = stepx.getJSONObject(TAG_END_LOCATION)
                    lat = java.lang.Double.parseDouble(startLocation.getString(TAG_LAT))
                    lng = java.lang.Double.parseDouble(startLocation.getString(TAG_LNG))
                    listLatLng.add(LatLng(lat, lng))
                }
            }

        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return listLatLng
    }




    companion object {
        private val TAG_START_LOCATION = "start_location"
        private val TAG_LAT = "lat"
        private val TAG_LNG = "lng"
        private val TAG_POLYLINE = "polyline"
        private val TAG_POINTS = "points"
        private val TAG_END_LOCATION = "end_location"
        private val TAG_DURATION = "duration"
        private val TAG_VALUE = "value"
        private val TAG_TEXT = "text"
        private val TAG_DISTANCE = "distance"
        private val TAG_LEGS = "legs"

        private val TAG_START_ADDRESS = "start_address"
        private val TAG_END_ADDRESS = "end_address"
        private val TAG_STEPS = "steps"

        /* Method to decode polyline points */
        internal fun decodePoly(encoded: String): List<LatLng> {

            val poly = ArrayList<LatLng>()
            var index = 0
            val len = encoded.length
            var lat = 0
            var lng = 0

            while (index < len) {
                var b: Int
                var shift = 0
                var result = 0
                do {
                    b = encoded[index++].toInt() - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lat += dlat

                shift = 0
                result = 0
                do {
                    b = encoded[index++].toInt() - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lng += dlng

                val p = LatLng(lat.toDouble() / 1E5,
                        lng.toDouble() / 1E5)
                poly.add(p)
            }

            return poly
        }
    }


}