package com.abraham24.kotlinmvpmapsretrofit.Init

import com.google.gson.annotations.SerializedName

/**
 * Created by KOCHOR on 8/27/2017.
 */
class ResponseJSON {


    @SerializedName("routes")
    var routes: List<Route>? = null
    @SerializedName("status")
    var status: String? = null

}