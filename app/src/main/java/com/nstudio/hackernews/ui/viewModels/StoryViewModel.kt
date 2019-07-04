package com.nstudio.hackernews.ui.viewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.nstudio.hackernews.api.ApiClient
import com.nstudio.hackernews.api.ApiInterface
import com.nstudio.hackernews.api.ApiService
import com.nstudio.hackernews.model.Comment
import com.nstudio.hackernews.model.Event
import com.nstudio.hackernews.model.QueryResponse
import com.nstudio.hackernews.model.Story

class StoryViewModel : ViewModel(){

    private val tag = StoryViewModel::class.java.simpleName
    private var apiService: ApiService

    // store list of stories in live data
    internal val storyList = MutableLiveData<MutableList<Story>>()
    // handle event in fragment
    internal var eventListener = MutableLiveData<QueryResponse>()
    // to store ids of all stories
    private var storyIds  = MutableLiveData<MutableList<Int>>()
    // load up to 10 stories per request
    private var loadCount = 10
    // to store index of last loaded story
    private var lastIndex : Int = 0


    private var queryListener: ApiService.OnStoryQueryListener = object : ApiService.OnStoryQueryListener {


        override fun onStoryAdd(story: Story) {
            storyList.value!!.add(story)
            eventListener.postValue(QueryResponse(Event.STORY_ADD))
            Log.i(tag,"Loaded till $lastIndex loaded stories > ${storyList.value!!.size}")
        }

        override fun onCommentAdd(comment: Comment) {

        }

        override fun onIdsLoaded(storyIds: List<Int>) {
            this@StoryViewModel.storyIds.value = storyIds.toMutableList()
            loadMoreStories()
            eventListener.postValue(QueryResponse(Event.STORY_LIST_LOAD))
        }

        override fun onError(msg: String) {
            eventListener.postValue(QueryResponse(Event.ERROR,msg))
        }
        override fun onDeleteStory(id: Int) {
            storyIds.value!!.remove(id)
        }

        override fun onDeleteComment(id: Int) {

        }

    }

    init {
        apiService = ApiService(ApiClient.getClient().create(ApiInterface::class.java),queryListener)
        storyList.value = arrayListOf()
        storyIds.value = arrayListOf()
    }

    fun loadMoreStories() {

        val storyCount = storyList.value!!.size
        val idCount = storyIds.value!!.size

        Log.i(tag,"total story ids > $idCount loaded stories > $storyCount")

        if (storyCount < idCount){

            var nextIndex = storyCount+loadCount

            if(nextIndex >= idCount){
                nextIndex = idCount
            }

            Log.i(tag,"Load From $lastIndex to $nextIndex")

            for (i in lastIndex until nextIndex ){
                apiService.loadStoryById(storyIds.value!![i])
            }

            lastIndex = nextIndex
        }

    }


    fun loadStories(){
        apiService.loadStoriesIds()
    }

    fun getStory(position: Int): Story? {
        if (storyList.value!=null && storyList.value!!.size>0 ){
            if(position< storyList.value!!.size){
                return storyList.value!![position]
            }
        }

        return null
    }

}