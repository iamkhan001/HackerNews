package com.nstudio.hackernews.view


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.google.gson.Gson

import com.nstudio.hackernews.R
import com.nstudio.hackernews.model.Story
import com.nstudio.hackernews.rest.ApiClient
import com.nstudio.hackernews.rest.ApiInterface
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


/**
 * A simple [Fragment] subclass.
 *
 */
class StoriesFragment : Fragment() {

    private val list :  MutableList<Story> = mutableListOf()
    private lateinit var storiesAdapter : StoriesAdapter
    private lateinit var apiInterface: ApiInterface
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val rvStories = view.findViewById<RecyclerView>(R.id.rvStories)

        progressBar = view.findViewById(R.id.progressBar)

        storiesAdapter = StoriesAdapter(list)

        rvStories.layoutManager = LinearLayoutManager(context)
        rvStories.adapter = storiesAdapter

        apiInterface = ApiClient.getClient().create(ApiInterface::class.java)

        progressBar.visibility = View.VISIBLE

        loadStoriesIds()

    }

    private fun loadStoriesIds() {

        val call : Call<ResponseBody> = apiInterface.getStories()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                progressBar.visibility = View.GONE

                if (response.code() == 200){
                    if(response.body()!=null){

                        val ids = JSONArray(response.body()!!.string())

                        for (i in 0 until ids.length()) {

                            Log.e(tag,"Load >> "+ids.getInt(i))
                            loadStoryById(ids.getInt(i))

                        }

                        return

                    }
                }

                if (response.errorBody()!=null){
                    val error = response.errorBody()!!.string()
                    Log.e(tag, "Error $error")
                }

                Toast.makeText(context,"Error Occurred "+response.code(),Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(tag, "fail " + t.message)
            }
        })

    }

    private fun loadStoryById(id:Int){
        val call : Call<ResponseBody> = apiInterface.getDetials(id)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (response.code() == 200){
                    if(response.body()!=null){
                        try {
                            val story = Gson().fromJson(response.body()!!.string(),Story::class.java)
                            list.add(story)
                            storiesAdapter.notifyItemInserted(list.size)
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(tag, "fail " + t.message)
            }
        })
    }


}
