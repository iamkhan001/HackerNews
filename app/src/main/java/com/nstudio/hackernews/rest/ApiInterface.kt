package com.nstudio.hackernews.rest

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface ApiInterface{

    @GET("/v0/topstories.json?print=pretty")
    fun getStories(): Call<ResponseBody>

    @GET("/v0/item/{id}.json")
    fun getDetials(@Path("id") id : Int): Call<ResponseBody>

}