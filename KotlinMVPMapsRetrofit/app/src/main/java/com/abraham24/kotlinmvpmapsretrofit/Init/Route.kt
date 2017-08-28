package com.abraham24.kotlinmvpmapsretrofit.Init

import com.google.gson.annotations.SerializedName

/**
 * Created by KOCHOR on 8/27/2017.
 */
class Route {

    @SerializedName("copyrights")
    var copyrights: String? = null
    @SerializedName("legs")
    var legs: List<Leg>? = null
    @SerializedName("overview_polyline")
    var overviewPolyline: OverviewPolyline? = null
    @SerializedName("summary")
    var summary: String? = null
    @SerializedName("warnings")
    var warnings: List<Any>? = null
    @SerializedName("waypoint_order")
    var waypointOrder: List<Any>? = null

}