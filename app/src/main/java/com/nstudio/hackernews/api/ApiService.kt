package com.nstudio.hackernews.api

import android.util.Log
import com.google.gson.Gson
import com.nstudio.hackernews.model.Story
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class ApiService(private val apiInterface: ApiInterface,private val queryListener: OnStoryQueryListener){

    private val tag =ApiService::class.java.simpleName

    fun loadStoryById(id:Int){


        val call : Call<ResponseBody> = apiInterface.getDetials(id)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (response.code() == 200){
                    if(response.body()!=null){
                        try {
                            val story = Gson().fromJson(response.body()!!.string(), Story::class.java)

                            if(story!=null){
                                queryListener.onStoryAdd(story)
                            }
                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(tag, "fail " + t.message)
                queryListener.onError(t.message.toString())
            }
        })
    }

    fun loadStoriesIds() {



        val call : Call<ResponseBody> = apiInterface.getStories()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {


                if (response.code() == 200){
                    if(response.body()!=null){

                        val ids = JSONArray(response.body()!!.string())
                        val idList : MutableList<Int> = mutableListOf()

                        for (i in 0 until ids.length()) {
                            idList.add(ids.getInt(i))
                        }

                        queryListener.onIdsLoaded(idList)

                        return

                    }
                }

                if (response.errorBody()!=null){
                    val error = response.errorBody()!!.string()
                    Log.e(tag, "Error $error")
                    queryListener.onError(error)
                    return
                }

                queryListener.onError("Error Occurred")

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(tag, "fail " + t.message)
                queryListener.onError(t.message.toString())
            }
        })

    }


    interface OnStoryQueryListener{
        fun onStoryAdd(story:Story)
        fun onIdsLoaded(storyIds:List<Int>)
        fun onError(msg:String)
    }


}