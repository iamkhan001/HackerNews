package com.nstudio.hackernews.api

import android.util.Log
import com.google.gson.Gson
import com.nstudio.hackernews.model.Comment
import com.nstudio.hackernews.model.Story
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

/** to make api calls and get response
 * @param queryListener to delegate response to respective ViewModel class
 *
 */
class ApiService(private val apiInterface: ApiInterface,private val queryListener: OnStoryQueryListener){

    private val tag =ApiService::class.java.simpleName

    //get list of all ids
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

    //load details of story by given id
    fun loadStoryById(id:Int){

        val call : Call<ResponseBody> = apiInterface.getDetails(id)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (response.code() == 200){
                    if(response.body()!=null){
                        try {
                            val story = Gson().fromJson(response.body()!!.string(), Story::class.java)

                            if(story!=null){
                                if (story.deleted){
                                    //story is deleted hence remove this id from list of ids of stories
                                    queryListener.onDeleteStory(id)
                                    return
                                }
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

    // load details of comment by given comment id
    fun loadCommentById(id:Int){

        val call : Call<ResponseBody> = apiInterface.getDetails(id)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (response.code() == 200){
                    if(response.body()!=null){
                        try {
                            val comment = Gson().fromJson(response.body()!!.string(), Comment::class.java)

                            if(comment!=null){
                                if (comment.deleted){
                                    //comment id deleted hence delete id of this comment from list
                                    queryListener.onDeleteComment(id)
                                    return
                                }
                                //notify viewModel and add new comment in list
                                queryListener.onCommentAdd(comment)
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

    interface OnStoryQueryListener{
        fun onStoryAdd(story:Story)
        fun onCommentAdd(comment: Comment)
        fun onDeleteStory(id:Int)
        fun onDeleteComment(id:Int)
        fun onIdsLoaded(storyIds:List<Int>)
        fun onError(msg:String)
    }


}