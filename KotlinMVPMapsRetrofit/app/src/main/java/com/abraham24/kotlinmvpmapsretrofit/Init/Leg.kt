package com.abraham24.kotlinmvpmapsretrofit.Init

import com.google.gson.annotations.SerializedName

/**
 * Created by KOCHOR on 8/27/2017.
 */
class Leg {

    @SerializedName("distance")
    var distance: Distance? = null
    @SerializedName("duration")
    var duration: Duration? = null
    @SerializedName("end_address")
    var endAddress: String? = null

    @SerializedName("start_address")
    var startAddress: String? = null


    @SerializedName("via_waypoint")
    var viaWaypoint: List<Any>? = null

}
