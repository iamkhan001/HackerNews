package com.nstudio.hackernews.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiInterface{

    // load stories from hacker news api
    @POST("/v0/topstories.json?print=pretty")
    fun getStories(): Call<ResponseBody>

    // load story or comment details
    @POST("/v0/item/{id}.json")
    fun getDetails(@Path("id") id : Int): Call<ResponseBody>

}