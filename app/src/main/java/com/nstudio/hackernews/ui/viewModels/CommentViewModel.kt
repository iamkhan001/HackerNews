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

class CommentViewModel : ViewModel(){


    private val tag = CommentViewModel::class.java.simpleName
    //to load comments via API
    private var apiService: ApiService

    //store list of all loaded comment details here
    internal val commentList = MutableLiveData<MutableList<Comment>>()

    //to handle event in fragment
    internal var eventListener = MutableLiveData<QueryResponse>()

    //store ids of all comments here
    private var commentIds  = MutableLiveData<MutableList<Int>>()

    //load 10 comments per request
    private var loadCount = 10
    //count / index of last loaded comment in list
    private var lastIndex : Int = 0


    // handle response from ApiService
    private var queryListener: ApiService.OnStoryQueryListener = object : ApiService.OnStoryQueryListener {
        override fun onDeleteStory(id: Int) {

        }

        override fun onDeleteComment(id: Int) {
            commentIds.value!!.remove(id)
        }

        override fun onStoryAdd(story: Story) {

        }

        override fun onCommentAdd(comment: Comment) {
            commentList.value!!.add(comment)
            eventListener.postValue(QueryResponse(Event.COMMENT_ADD))
            Log.i(tag,"Loaded till $lastIndex loaded comments > ${commentList.value!!.size}")
        }

        override fun onIdsLoaded(storyIds: List<Int>) {
            this@CommentViewModel.commentIds.value = storyIds.toMutableList()
            loadMoreComments()
            eventListener.postValue(QueryResponse(Event.STORY_LIST_LOAD))
        }

        override fun onError(msg: String) {
            eventListener.postValue(QueryResponse(Event.ERROR,msg))
        }


    }

    init {
        apiService = ApiService(ApiClient.getClient().create(ApiInterface::class.java),queryListener)

    }

    fun setCommentIds(ids:MutableList<Int>){
        commentIds.value = ids
        commentList.value = arrayListOf()
    }

    fun loadMoreComments() {

        val commentCount = commentList.value!!.size
        val idCount = commentIds.value!!.size

        Log.i(tag,"total comment ids > $idCount loaded comments > $commentCount")

        if (commentCount < idCount){

            var nextIndex = commentCount+loadCount

            if(nextIndex >= idCount){
                nextIndex = idCount
            }

            Log.i(tag,"Load From $lastIndex to $nextIndex")

            for (i in lastIndex until nextIndex ){
                apiService.loadCommentById(commentIds.value!![i])
            }

            lastIndex = nextIndex
        }

    }


}