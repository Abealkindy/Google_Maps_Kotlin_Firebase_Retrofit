package com.abraham24.kotlinmvpmapsretrofit.Init

import com.google.gson.annotations.SerializedName

/**
 * Created by KOCHOR on 8/27/2017.
 */
class Distance {

    @SerializedName("text")
    var text: String? = null
    @SerializedName("value")
    var value: Long? = null

}
