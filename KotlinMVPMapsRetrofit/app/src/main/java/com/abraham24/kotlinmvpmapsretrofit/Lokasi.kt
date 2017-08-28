package com.abraham24.kotlinmvpmapsretrofit

/**
 * Created by KOCHOR on 8/27/2017.
 */
class Lokasi {


    constructor (namaPenjemputan: String, namaTujuan: String, jarak: String, harga: String, waktu: String, latitude: String, longitude: String) {
        this.namaPenjemputan = namaPenjemputan
        this.namaTujuan = namaTujuan
        this.jarak = jarak
        this.harga = harga
        this.waktu = waktu
        this.latitude = latitude
        this.longitude = longitude
    }

    var namaPenjemputan = ""
    var namaTujuan= ""
    var jarak = ""
    var harga = ""
    var waktu = ""
    var latitude = ""
    var longitude = ""

}